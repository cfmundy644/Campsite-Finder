package com.PersonalProjects.CampgroundFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeSet;
import java.time.temporal.ChronoUnit;


@Controller
public class InputController {
    @Autowired
    private FacilityRepository facilityRepository;

    private String googleKey = "AIzaSyDjzILiKx-IzTpbnq7B9B21DV3a7KyeQZc";

    @GetMapping("/testfile")
    public String testfile(Model model) {
        return "testfile";
    }

    @RequestMapping("/inputinfo")
    public String inputinfoForm(Model model) {
        InputInfo inputInfo = new InputInfo();
        inputInfo.setRadius(100); // default search radius (pre-populated into form)
        LocalDate dateMonthFromToday = LocalDate.now();
        dateMonthFromToday = dateMonthFromToday.plusMonths(1);
        inputInfo.setCheckInDate(dateMonthFromToday);
        int defaultNumNights = 3;
        inputInfo.setCheckOutDate(dateMonthFromToday.plusDays(defaultNumNights));
        model.addAttribute("inputinfo", inputInfo);
        return "inputinfo";
    }

    @RequestMapping("/showinfo")
    public String getSelectFacilities(@ModelAttribute InputInfo inputInfo, Model model) {
        // check that inputs don't break validation rules
        int maxRadius = 250;
        int maxTripLengthDays = 14;
        long inputTripLengthDays = ChronoUnit.DAYS.between(inputInfo.getCheckInDate(), inputInfo.getCheckOutDate());
        if (inputInfo.getRadius() > maxRadius || inputTripLengthDays > maxTripLengthDays) {
            model.addAttribute("message", "Inputs not valid");
            inputinfoForm(model);
            return "inputinfo";
        }

        // make call to Google Geocode API to convert input address to lat/long
        try {inputInfo.setLatLong(googleKey);}
        catch (Exception ex) {return "Google geocode API call error";}

        Iterable<Facility> allFacs = facilityRepository.findAll();
        TreeSet<Facility> facsInRad = new TreeSet<Facility>(); // facilities in radius, ordered by availability, then distance

        int numFacsInRad = 0; // add counter that breaks search if too many facilities are in the radius (to avoid too many calls to Google places API)
        int maxFacsInRad = 30;

        // go through all facilities, check if they are within radius from input address
        for (Facility f : allFacs) {
            double dist = inputInfo.calcDistToFac(f); // calculate distance between input address and facility (campground)
            if (dist <= inputInfo.getRadius()) { // if facility (campground) is within radius, check for availability
                numFacsInRad++;
                if (numFacsInRad > maxFacsInRad) {
                    inputinfoForm(model);
                    model.addAttribute("message", "Too many campgrounds in radius, please decrease radius");
                    return "inputinfo";
                }
                try {f.callAvailabilityAPI(inputInfo);}
                catch (Exception ex) {return "rgAPICallError";}
                facsInRad.add(f); // add facs to facsInRad after populating availability info (used by comparator)
                try {f.callGoogPlaceAPI(googleKey);} // populate Google Place info for each facility in radius
                catch (Exception ex) {return "googPlaceAPICallError";}
            }
        }
        model.addAttribute("facsInRad",facsInRad);
        model.addAttribute("inputInfo", inputInfo);
        return "result";
    }

    /* No need to add facilities manually via post
    @PostMapping("/addfacility")
    public @ResponseBody String addNewFacility (@RequestParam Integer id, @RequestParam String rgFacilityId, @RequestParam String rgFacilityName, @RequestParam double latitude, @RequestParam double longitude) {
        Facility f = new Facility();
        f.setId(id);
        f.setRgFacilityId(rgFacilityId);
        f.setRgFacilityName(rgFacilityName);
        f.setLatitude(latitude);
        f.setLongitude(longitude);
        facilityRepository.save(f);
        return "Saved";
    }
     */

    @GetMapping("/listfacilities")
    public @ResponseBody Iterable<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

}
