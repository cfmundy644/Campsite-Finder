package com.PersonalProjects.CampgroundFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.TreeSet;

@Controller
public class InputController {
    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

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
        Iterable<Facility> allFacs = facilityRepository.findAll();
        TreeSet<Facility> facsInRadAvailable = new TreeSet<Facility>(); // facilities in radius with availability, ordered by distance
        TreeSet<Facility> facsInRadUnavailable = new TreeSet<Facility>(); // facilities in radius with no availability, ordered by distance

        // make call to Google Geocode API to convert input address to lat/long
        GeocodeAPICall geocodeAPICall = new GeocodeAPICall(inputInfo, googleKey);
        if (!geocodeAPICall.getSuccess()) {return "Google geocode API call error";} // check to see if the API call was successful, if not return error
        Geocode geocode = geocodeAPICall.getGeocode();
        inputInfo.setLatitude(geocode.getResults()[0].getGeometry().getLocation().getLat());
        inputInfo.setLongitude(geocode.getResults()[0].getGeometry().getLocation().getLng());

        // go through all facilities, check if they are within radius from input address
        for (Facility f : allFacs) {
            // calculate distance between input address and facility (campground)
            LatLongsToMiles latLongsToMiles = new LatLongsToMiles(inputInfo.getLatitude(), inputInfo.getLongitude(), f.getLatitude(), f.getLongitude());
            double dist = latLongsToMiles.getDist();
            f.setDist(dist);

            // if facility (campground) is within radius, check for availability
            if (dist <= inputInfo.getRadius()) {
                boolean facHasAvailability = false;

                // https://www.recreation.gov/api/camps/availability/campground/234718/month?start_date=2021-04-01T00%3A00%3A00.000Z
                StringBuilder campgroundAvailabilityUrlSB = new StringBuilder();
                campgroundAvailabilityUrlSB.append("https://www.recreation.gov/api/camps/availability/campground/");
                campgroundAvailabilityUrlSB.append(f.getRgFacilityId());
                campgroundAvailabilityUrlSB.append("/month?start_date=");
                campgroundAvailabilityUrlSB.append(inputInfo.getCheckInDate().getYear());
                campgroundAvailabilityUrlSB.append("-");
                campgroundAvailabilityUrlSB.append(String.format("%02d", inputInfo.getCheckInDate().getMonthValue()));
                campgroundAvailabilityUrlSB.append("-01T00%3A00%3A00.000Z"); // this api call only accepts the first of the month

                URL availabilityUrl = null;
                ObjectMapper availabilityMapper = new ObjectMapper();
                CampgroundAvailability campgroundAvailability = new CampgroundAvailability();
                try {
                    availabilityUrl = new URL(campgroundAvailabilityUrlSB.toString());
                    campgroundAvailability = availabilityMapper.readValue(availabilityUrl, CampgroundAvailability.class);
                } catch (Exception ex) {
                    model.addAttribute("availabilityapicallurl", campgroundAvailabilityUrlSB.toString());
                    return "rgAPICallError";
                }

                // go through all campsites at facility to check for availability
                for (Campsites d : campgroundAvailability.getCampsites().values()) {
                    LocalDate dateHolder = inputInfo.getCheckInDate();
                    // cycle through all dates to check for availability across all nights, no need to check on check-out day
                    while (dateHolder.compareTo(inputInfo.getCheckOutDate()) < 0) {
                        StringBuilder stringBuilderDate = new StringBuilder();
                        stringBuilderDate.append(dateHolder.getYear());
                        stringBuilderDate.append("-");
                        stringBuilderDate.append(String.format("%02d", dateHolder.getMonthValue()));
                        stringBuilderDate.append("-");
                        stringBuilderDate.append(String.format("%02d", dateHolder.getDayOfMonth()));
                        stringBuilderDate.append("T00:00:00Z");
                        String dateKey = stringBuilderDate.toString();
                        String availabilityStatus = new String();
                        try {
                            availabilityStatus = d.getAvailabilities().get(dateKey).toString();
                        } catch (NullPointerException np) {
                            availabilityStatus = "Unavailable"; // certain unavailable sites will not return any availability info so need to override that here
                        }
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
        // TODO add weather to the two lists of facsInRad

        for (Facility f : facsInRadAvailable) {
            // google places API: pass in campsite name and lat long, receive google maps place information
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
                // return "Google Places API Call Error";
                // ok to come up empty here
            }
            f.setGoogRating(facGooglePlace.getResultsRating());
            f.setGoogName(facGooglePlace.getResultsName());
            f.setGoogPlaceId(facGooglePlace.getResultsPlace_id());
            f.setGoogUserRatingsTotal(facGooglePlace.getResultsUser_Ratings_Total());
        }

        // TODO consolidate repeated code block (above and below) into class
        for (Facility f : facsInRadUnavailable) {
            // google places API: pass in campsite name and lat long, receive google maps place information
            // example call: "https://maps.googleapis.com/maps/api/place/textsearch/json?input=CAMP%20GATEWAY%20-%20SANDY%20HOOK&locationbias=point:40,-74&key=AIzaSyDjzILiKx-IzTpbnq7B9B21DV3a7KyeQZc"
            StringBuilder facPlaceAPIUrlSB = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?input=");
            facPlaceAPIUrlSB.append(f.getRgFacilityName().replaceAll(" ", "%20"));
            facPlaceAPIUrlSB.append("%20campground&locationbias=point:");
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
                //return "Google Places API Call Error";
                // ok to come up empty here
            }
            f.setGoogRating(facGooglePlace.getResultsRating());
            f.setGoogName(facGooglePlace.getResultsName());
            f.setGoogPlaceId(facGooglePlace.getResultsPlace_id());
            f.setGoogUserRatingsTotal(facGooglePlace.getResultsUser_Ratings_Total());
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
