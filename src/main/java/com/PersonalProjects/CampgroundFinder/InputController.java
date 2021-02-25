package com.PersonalProjects.CampgroundFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.time.LocalDate;
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
        // TODO discuss putting below logic here vs. in a class with CMTH
        Iterable<Facility> allFacs = facilityRepository.findAll();
        HashSet<Facility> facsInRadAvailable = new HashSet<Facility>(); // facilities in radius with availability
        HashSet<Facility> facsInRadUnavailable = new HashSet<Facility>(); // facilities in radius with no availability
        String googleKey = "AIzaSyDjzILiKx-IzTpbnq7B9B21DV3a7KyeQZc";

        // sample api call "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY"
        StringBuilder geocodeAPIUrlSB = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?address=");
        geocodeAPIUrlSB.append(inputInfo.getStreetAddress().replaceAll(" ", "%20"));
        geocodeAPIUrlSB.append(",%20");
        geocodeAPIUrlSB.append(inputInfo.getCity().replaceAll(" ", "%20"));
        geocodeAPIUrlSB.append(",%20");
        geocodeAPIUrlSB.append("&key=");
        geocodeAPIUrlSB.append(googleKey);
        URL geocodeAPIUrl = null;
        ObjectMapper geocodeMapper = new ObjectMapper();
        Geocode geocode = new Geocode();
        try {
            geocodeAPIUrl = new URL(geocodeAPIUrlSB.toString());
            geocode = geocodeMapper.readValue(geocodeAPIUrl, Geocode.class);
        } catch (Exception ex) {
            // TODO figure out what to put here
        }

        inputInfo.setLatitude(geocode.getResults()[0].getGeometry().getLocation().getLat());
        inputInfo.setLongitude(geocode.getResults()[0].getGeometry().getLocation().getLng());

        for (Facility f : allFacs) {


            // TODO change this from Euclidean distance (look at this MapBox post): https://blog.mapbox.com/fast-geodesic-approximations-with-cheap-ruler-106f229ad016

            // TODO create class LatLongsToMiles
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

                boolean facHasAvailability = false;

                // go through all campsites at facility to check for availability
                for (Campsite d : campsitesAtFac) {

                    ObjectMapper objectMapper = new ObjectMapper();

                    // sample API call url: "https://www.recreation.gov/api/camps/availability/campsite/92086?start_date=2021-03-09T00%3A00%3A00.000Z&end_date=2022-03-09T00%3A00%3A00.000Z"
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

                    try {
                        URL url = new URL(stringUrl);
                        CampsiteAvailability campsiteAvailability = objectMapper.readValue(url, CampsiteAvailability.class);
                        LocalDate dateHolder = inputInfo.getCheckInDate();
                        // cycle through all dates to check for availability across all nights, no need to check on check-out day
                        while (dateHolder.compareTo(inputInfo.getCheckOutDate()) < 0) {
                            // TODO confirm all possible values for availability (ie. Available, Open, Unavailable, etc.)
                            StringBuilder stringBuilderDate = new StringBuilder();
                            stringBuilderDate.append(dateHolder.getYear());
                            stringBuilderDate.append("-");
                            stringBuilderDate.append(String.format("%02d", dateHolder.getMonthValue()));
                            stringBuilderDate.append("-");
                            stringBuilderDate.append(String.format("%02d", dateHolder.getDayOfMonth()));
                            stringBuilderDate.append("T00:00:00Z");
                            String dateKey = stringBuilderDate.toString();
                            String availabilityStatus = campsiteAvailability.getAvailability().getAvailabilities().get(dateKey).toString();
                            // TODO consider changing assumption that user wants to stay in same site the entire time
                            // break out of date availability loop if it is not available on one of the nights of the stays
                            if (!availabilityStatus.equals("Available")) {
                                break;
                            }
                            // if the site is available and we have checked all dates
                            else if (dateHolder.compareTo(inputInfo.getCheckOutDate().minusDays(1)) == 0) {
                                facHasAvailability = true;
                            }
                            dateHolder = dateHolder.plusDays(1);
                        }

                    } catch (Exception ex) {
                        // TODO figure out what to put here
                    }

                    // if a single campsite has availability, no need to check the rest
                    if (facHasAvailability) {
                        facsInRadAvailable.add(f);
                        break;
                    }
                }

                // if we make it through all campsites and there is no availability, add facility to unavailable list
                if (!facHasAvailability) {facsInRadUnavailable.add(f);}
            }
        }
        // TODO add weather and ratings to the two lists of facsInRad

        for (Facility f : facsInRadAvailable) {
            // TODO copy in code here
        }

        // TODO consolidate repeated code block (above and below) into class
        for (Facility f : facsInRadUnavailable) {
            // places API: pass in campsite name and lat long, ask to receive name, rating
            // TODO add location bias to this call
            // example call: "https://maps.googleapis.com/maps/api/place/textsearch/json?input=CAMP%20GATEWAY%20-%20SANDY%20HOOK&locationbias=point:40,-74&key=AIzaSyDjzILiKx-IzTpbnq7B9B21DV3a7KyeQZc"
            StringBuilder facPlaceAPIUrlSB = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?input=");
            facPlaceAPIUrlSB.append(f.getRgFacilityName().replaceAll(" ", "%20"));
            facPlaceAPIUrlSB.append("&locationbias=point:");
            facPlaceAPIUrlSB.append(f.getLatitude());
            facPlaceAPIUrlSB.append(",");
            facPlaceAPIUrlSB.append(f.getLongitude());
            facPlaceAPIUrlSB.append("&key=");
            facPlaceAPIUrlSB.append(googleKey);
            URL facPlaceAPIUrl = null;
            ObjectMapper facPlaceMapper = new ObjectMapper();
            FacGooglePlace facGooglePlace = new FacGooglePlace();
            try {
                facPlaceAPIUrl = new URL(facPlaceAPIUrlSB.toString());
                facGooglePlace = facPlaceMapper.readValue(facPlaceAPIUrl, FacGooglePlace.class);
            } catch (Exception ex) {
                // TODO figure out what to put here
            }

            f.setGoogRating(facGooglePlace.getResultsRating());
            f.setGoogName(facGooglePlace.getResultsName());
            f.setGoogPlaceId(facGooglePlace.getResultsPlace_id());
            f.setGoogUserRatingsTotal(facGooglePlace.getResultsUser_Ratings_Total());

            // TODO add link to places based on this call "https://www.google.com/maps/place/?q=place_id:ChIJp4JiUCNP0xQR1JaSjpW_Hms"
        }

        model.addAttribute("facsInRadAvailable",facsInRadAvailable);
        model.addAttribute("facsInRadUnavailable",facsInRadUnavailable);
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
