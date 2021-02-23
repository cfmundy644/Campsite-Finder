package com.PersonalProjects.CampgroundFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Scanner;

import java.util.HashSet;
import java.awt.geom.Point2D;

@Controller
public class InputController {
    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

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
    public @ResponseBody String getSelectFacilities(@ModelAttribute InputInfo inputInfo, Model model) {
    //public String getSelectFacilities(@ModelAttribute InputInfo inputInfo, Model model) {
        // TODO discuss putting below logic here vs. in a class with CMTH
        Iterable<Facility> allFacs = facilityRepository.findAll();
        HashSet<Facility> facsInRadAvailable = new HashSet<Facility>(); // facilities in radius with availability
        HashSet<Facility> facsInRadUnavailable = new HashSet<Facility>(); // facilities in radius with no availability

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
                // TODO need to optimize this database call
                Iterable<Campsite> allCampsites = campsiteRepository.findAll();
                HashSet<Campsite> campsitesAtFac = new HashSet<Campsite>();

                // create hashset of all campsites at current facility
                for (Campsite c : allCampsites) {
                    if (c.getRgFacilityId().equals(f.getRgFacilityId())) {
                        campsitesAtFac.add(c);
                    }
                }
                // TODO check if any of those campsites have availability on all required days here, if yes
                // GET https://www.recreation.gov/api/camps/availability/campsite/92086?start_date=2021-02-09T00%3A00%3A00.000Z&end_date=2022-02-09T00%3A00%3A00.000Z
                // go through all campsites at current facility and see if there is any availability
                for (Campsite d : campsitesAtFac) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    StringBuilder stringBuilderUrl = new StringBuilder("https://www.recreation.gov/api/camps/availability/campsite/");
                    stringBuilderUrl.append(d.getRgCampsiteId());

                    stringBuilderUrl.append("?start_date=");
                    stringBuilderUrl.append(inputInfo.getCheckInDate().getYear());
                    stringBuilderUrl.append("-");
                    stringBuilderUrl.append(String.format("%02d",inputInfo.getCheckInDate().getMonthValue()));
                    stringBuilderUrl.append("-");
                    stringBuilderUrl.append(String.format("%02d",inputInfo.getCheckInDate().getDayOfMonth()));

                    stringBuilderUrl.append("T00%3A00%3A00.000Z&end_date=");
                    // subtracting one date from all of end dates, as last day we need to check is the day of the final night, not the check out date
                    stringBuilderUrl.append(inputInfo.getCheckOutDate().minusDays(1).getYear());
                    stringBuilderUrl.append("-");
                    stringBuilderUrl.append(String.format("%02d",inputInfo.getCheckOutDate().minusDays(1).getMonthValue()));
                    stringBuilderUrl.append("-");
                    stringBuilderUrl.append(String.format("%02d",inputInfo.getCheckOutDate().minusDays(1).getDayOfMonth()));
                    stringBuilderUrl.append("T00%3A00%3A00.000Z");
                    String stringUrl = stringBuilderUrl.toString();
                    //String testStringUrl = "https://www.recreation.gov/api/camps/availability/campsite/92086?start_date=2021-03-09T00%3A00%3A00.000Z&end_date=2022-03-09T00%3A00%3A00.000Z";
                    // https://www.recreation.gov/api/camps/availability/campsite/81270?start_date=2021-02-04T00%3A00%3A00.000Z&end_date=2021-12-28T00%3A00%3A00.000Z
                    try {
                        URL url = new URL(stringUrl);
                        CampsiteAvailability campsiteAvailability = objectMapper.readValue(url, CampsiteAvailability.class);
                        // return stringUrl;
                        return campsiteAvailability.getAvailability().getAvailabilities().get("2021-08-26T00:00:00Z").toString();

                    } catch (Exception ex) {
                        // TODO figure out what to put here
                    }
                }
            }
        }
        return "error";
    }
        /*
        model.addAttribute("facsInRadAvailable",facsInRadAvailable);
        model.addAttribute("facsInRadUnavailable",facsInRadUnavailable);
        return "result";
         */

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
