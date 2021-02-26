package com.PersonalProjects.CampgroundFinder;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.URL;
import java.time.LocalDate;

@Entity
public class Facility implements Comparable<Facility> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String rgFacilityId; // rg = recreation.gov
    private String rgFacilityName;
    private double latitude;
    private double longitude;

    // following populated based on Google Place API
    private String googName;
    private double googRating;
    private int googUserRatingsTotal;
    private String googPlaceId;

    // following populated based on calcs
    private double dist;
    private boolean available;

    public Integer getId() {
        return id;
    }

    ;

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRgFacilityId() {
        return rgFacilityId;
    }

    public void setRgFacilityId(String rgFacilityId) {
        this.rgFacilityId = rgFacilityId;
    }

    public String getRgFacilityName() {
        return rgFacilityName;
    }

    public void setRgFacilityName(String rgFacilityName) {
        this.rgFacilityName = rgFacilityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGoogName() {
        return googName;
    }

    public void setGoogName(String googName) {
        this.googName = googName;
    }

    public double getGoogRating() {
        return googRating;
    }

    public void setGoogRating(double googRating) {
        this.googRating = googRating;
    }

    public int getGoogUserRatingsTotal() {
        return googUserRatingsTotal;
    }

    public void setGoogUserRatingsTotal(int googUserRatingsTotal) {
        this.googUserRatingsTotal = googUserRatingsTotal;
    }

    public String getGoogPlaceId() {
        return googPlaceId;
    }

    public void setGoogPlaceId(String googPlaceId) {
        this.googPlaceId = googPlaceId;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public int compareTo(Facility that) {
        if (this.available && !that.available) {
            return -1;
        } else if (!this.available && that.available) {
            return 1;
        }

        if (this.dist < that.dist) {
            return -1;
        } else if (this.dist == that.dist) {
            return 0;
        } else {
            return 1;
        }
    }

    public void callAvailabilityAPI(InputInfo inputInfo) throws Exception {
        boolean facHasAvailability = false; // flips to yes if availability is found at any campsite at facility
        URL availabilityUrl = null;
        ObjectMapper availabilityMapper = new ObjectMapper();
        CampgroundAvailability campgroundAvailability = null;
        try {
            availabilityUrl = new URL(createRgAvailApiString(inputInfo));
            campgroundAvailability = availabilityMapper.readValue(availabilityUrl, CampgroundAvailability.class);
        } catch (Exception ex) {
            throw new Exception();
        }

        // go through all campsites at facility to check for availability
        for (Campsite c : campgroundAvailability.getCampsites().values()) {
            LocalDate dateHolder = inputInfo.getCheckInDate();
            // cycle through all dates to check for availability across all nights, no need to check on check-out day
            while (dateHolder.compareTo(inputInfo.getCheckOutDate()) < 0) {
                String dateKey = createRgDateString(dateHolder);
                String availabilityStatus;
                try {
                    availabilityStatus = c.getAvailabilities().get(dateKey).toString();
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
                available = true;
                break;
            }
        }

        // if we make it through all campsites and there is no availability, add facility to unavailable list
        if (!facHasAvailability) {
            available = false;
        }
    }

    private String createRgDateString(LocalDate dateHolder) {
        StringBuilder stringBuilderDate = new StringBuilder();
        stringBuilderDate.append(dateHolder.getYear());
        stringBuilderDate.append("-");
        stringBuilderDate.append(String.format("%02d", dateHolder.getMonthValue()));
        stringBuilderDate.append("-");
        stringBuilderDate.append(String.format("%02d", dateHolder.getDayOfMonth()));
        stringBuilderDate.append("T00:00:00Z");
        return stringBuilderDate.toString();
    }

    private String createRgAvailApiString(InputInfo inputInfo) {
        // https://www.recreation.gov/api/camps/availability/campground/234718/month?start_date=2021-04-01T00%3A00%3A00.000Z
        StringBuilder campgroundAvailabilityUrlSB = new StringBuilder();
        campgroundAvailabilityUrlSB.append("https://www.recreation.gov/api/camps/availability/campground/");
        campgroundAvailabilityUrlSB.append(rgFacilityId);
        campgroundAvailabilityUrlSB.append("/month?start_date=");
        campgroundAvailabilityUrlSB.append(inputInfo.getCheckInDate().getYear());
        campgroundAvailabilityUrlSB.append("-");
        campgroundAvailabilityUrlSB.append(String.format("%02d", inputInfo.getCheckInDate().getMonthValue()));
        campgroundAvailabilityUrlSB.append("-01T00%3A00%3A00.000Z"); // this api call only accepts the first of the month
        return campgroundAvailabilityUrlSB.toString();
    }

    public void callGoogPlaceAPI(String googleKey) throws Exception {
        // google places API: pass in campsite name and lat long, receive google maps place information
        URL facPlaceAPIUrl = null;
        ObjectMapper facPlaceMapper = new ObjectMapper();
        FacGooglePlace facGooglePlace = new FacGooglePlace();
        try {
            facPlaceAPIUrl = new URL(createGoogPlaceAPICallString(googleKey));
            facGooglePlace = facPlaceMapper.readValue(facPlaceAPIUrl, FacGooglePlace.class);
        } catch (Exception ex) {
            // ok to come up empty here (if google can't find facility)
        }
        googRating = facGooglePlace.getResultsRating();
        googName = facGooglePlace.getResultsName();
        googPlaceId = facGooglePlace.getResultsPlace_id();
        googUserRatingsTotal = facGooglePlace.getResultsUser_Ratings_Total();
    }

    private String createGoogPlaceAPICallString(String googleKey) {
        // example call: "https://maps.googleapis.com/maps/api/place/textsearch/json?input=CAMP%20GATEWAY%20-%20SANDY%20HOOK&locationbias=point:40,-74&key=AIzaSyDjzILiKx-IzTpbnq7B9B21DV3a7KyeQZc"
        StringBuilder facPlaceAPIUrlSB = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?input=");
        facPlaceAPIUrlSB.append(rgFacilityName.replaceAll(" ", "%20"));
        facPlaceAPIUrlSB.append("%20campground&locationbias=point:");
        facPlaceAPIUrlSB.append(latitude);
        facPlaceAPIUrlSB.append(",");
        facPlaceAPIUrlSB.append(longitude);
        facPlaceAPIUrlSB.append("&key=");
        facPlaceAPIUrlSB.append(googleKey);
        return facPlaceAPIUrlSB.toString();
    }
}