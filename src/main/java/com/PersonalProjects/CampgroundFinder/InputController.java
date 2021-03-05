package com.PersonalProjects.CampgroundFinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.util.TreeSet;
import java.time.temporal.ChronoUnit;


@Controller
public class InputController {
    private static Secret secret;
    private static final String GOOGLE_KEY = secret.getSecret();
    private static final int MAX_RADIUS = 250;
    private static final int MAX_TRIP_LENGTH_DAYS = 14;
    private static final int MAX_FACILITIES_IN_RADIUS = 50;

    @Autowired
    private FacilityRepository facilityRepository;

    @RequestMapping("/inputinfo")
    public String inputInfoForm(Model model) {
        InputInfo inputInfo = new InputInfo();
        inputInfo.setRadius(100); // default search radius (pre-populated into form)
        LocalDate dateMonthFromToday = LocalDate.now();
        dateMonthFromToday = dateMonthFromToday.plusMonths(1);
        inputInfo.setCheckInDate(dateMonthFromToday);
        int defaultNumNights = 3;
        inputInfo.setCheckOutDate(dateMonthFromToday.plusDays(defaultNumNights));
        model.addAttribute("inputInfo", inputInfo);
        model.addAttribute("MAX_RADIUS", MAX_RADIUS);
        model.addAttribute("MAX_TRIP_LENGTH_DAYS", MAX_TRIP_LENGTH_DAYS);
        return "inputInfo";
    }

    @RequestMapping("/showinfo")
    public String getSelectFacilities(@ModelAttribute InputInfo inputInfo, Model model) {
        // check that inputs don't break validation rules
        long inputTripLengthDays = ChronoUnit.DAYS.between(inputInfo.getCheckInDate(), inputInfo.getCheckOutDate());
        if (inputInfo.getRadius() > MAX_RADIUS || inputTripLengthDays > MAX_TRIP_LENGTH_DAYS) {
            model.addAttribute("message", "Inputs not valid");
            inputInfoForm(model);
            return "inputInfo";
        }

        // make call to Google Geocode API to convert input address to lat/long
        try {
            inputInfo.setLatLong(GOOGLE_KEY);
        }
        catch (Exception ex) {
            return "Google geocode API call error";
        }

        Iterable<Facility> allFacilities = facilityRepository.findAll();
        TreeSet<Facility> facilitiesInRadius = new TreeSet<Facility>(); // facilities in radius, ordered by availability, then distance

        int numFacilitiesInRadius = 0; // add counter that breaks search if too many facilities are in the radius (to avoid too many calls to Google places API)

        // go through all facilities, check if they are within radius from input address
        for (Facility f : allFacilities) {
            float dist = inputInfo.calcDistToFac(f); // calculate distance between input address and facility (campground)
            if (dist <= inputInfo.getRadius()) { // if facility (campground) is within radius, check for availability
                numFacilitiesInRadius++;
                if (numFacilitiesInRadius > MAX_FACILITIES_IN_RADIUS) {
                    inputInfoForm(model);
                    model.addAttribute("message", "Too many campgrounds in radius, please decrease radius");
                    return "inputInfo";
                }
                try {
                    f.callAvailabilityAPI(inputInfo);
                }
                catch (Exception ex) {
                    return "rgAPICallError";
                }
                facilitiesInRadius.add(f); // add facs to facsInRad after populating availability info (used by comparator)
                try {
                    f.callGoogPlaceAPI(GOOGLE_KEY);
                } // populate Google Place info for each facility in radius
                catch (Exception ex) {
                    return "googPlaceAPICallError";
                }
            }
        }
        model.addAttribute("facsInRad",facilitiesInRadius);
        model.addAttribute("inputInfo", inputInfo);
        return "result";
    }

    @GetMapping("/listfacilities")
    public @ResponseBody Iterable<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

}
