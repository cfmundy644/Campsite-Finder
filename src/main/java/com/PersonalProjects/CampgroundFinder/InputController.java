package com.PersonalProjects.CampgroundFinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.awt.geom.Point2D;

@Controller
public class InputController {
    @Autowired
    private FacilityRepository facilityRepository;

    @GetMapping("/testfile")
    public String testfile(Model model) {
        return "testfile";
    }

    @RequestMapping("/inputinfo")
    public String inputinfoForm(Model model) {
        model.addAttribute("inputinfo", new InputInfo());
        return "inputinfo";
    }

    /* shows the data that was input to input info
    @RequestMapping("/showinfo")
    public String inputinfoSubmit(@ModelAttribute InputInfo inputInfo, Model model) {
        model.addAttribute("inputinfo", inputInfo);
        return "result";
    }
     */

    /* shows all facilities
    @RequestMapping("/showinfo")
    public @ResponseBody Iterable<Facility> getSelectFacilities() {
        return facilityRepository.findAll();
    }
     */

    @RequestMapping("/showinfo")
    public String getSelectFacilities(@ModelAttribute InputInfo inputInfo, Model model) {
        Iterable<Facility> allFacs = facilityRepository.findAll();
        HashSet<Facility> facsInRad = new HashSet<Facility>(); // facilities in radius
        for (Facility f : allFacs) {
            // TODO change this from Euclidean distance (look at this MapBox post): https://blog.mapbox.com/fast-geodesic-approximations-with-cheap-ruler-106f229ad016

            double lon1 = inputInfo.getLongitude();
            double lat1 = inputInfo.getLatitude();
            int radHolder = inputInfo.getRadius();

            double lon2 = f.getLongitude();
            double lat2 = f.getLatitude();
            //double distToFac = Point2D.distance(latHolder, longHolder, f.getLatitude(), f.getLongitude());
            // https://www.geodatasource.com/developers/java
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (dist <= radHolder) {
                facsInRad.add(f);
            }
        }
        model.addAttribute("facsInRad",facsInRad);
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
