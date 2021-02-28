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
        URL availabilityUrl1 = null;
        URL availabilityUrl2 = null; // populate for next month in case trip leaks into next month
        ObjectMapper availabilityMapper1 = new ObjectMapper();
        ObjectMapper availabilityMapper2 = new ObjectMapper();
        CampgroundAvailability campgroundAvailability1 = null;
        CampgroundAvailability campgroundAvailability2 = null;
        try {
            availabilityUrl1 = new URL(createRgAvailApiString(inputInfo.getCheckInDate()));
            availabilityUrl2 = new URL(createRgAvailApiString(inputInfo.getCheckInDate().plusMonths(1)));
            campgroundAvailability1 = availabilityMapper1.readValue(availabilityUrl1, CampgroundAvailability.class);
            campgroundAvailability2 = availabilityMapper1.readValue(availabilityUrl2, CampgroundAvailability.class);
        } catch (Exception ex) {
            throw new Exception();
        }
        Iterable<Campsite> campsitesAtFac = campgroundAvailability1.getCampsites().values();
        // go through all campsites at facility to check for availability
        for (Campsite c : campsitesAtFac) {
            LocalDate dateHolder = inputInfo.getCheckInDate();
            // cycle through all dates to check for availability across all nights, no need to check on check-out day
            while (dateHolder.compareTo(inputInfo.getCheckOutDate()) < 0) {
                String dateKey = createRgDateString(dateHolder);
                String availabilityStatus;

                // if we are still in the same month as check in date, stick with first month of data, otherwise look to second month of data
                if (dateHolder.getMonthValue() == inputInfo.getCheckInDate().getMonthValue()) {
                    if (campgroundAvailability1.getCampsites().get(c.getCampsite_id()).getAvailabilities().isEmpty()) {
                        availabilityStatus = "Unavailable"; // certain unavailable sites will not return any availability info so need to override that here
                    }
                    else {
                        try {
                            availabilityStatus = campgroundAvailability1.getCampsites().get(c.getCampsite_id()).getAvailabilities().get(dateKey).toString();
                        } catch (NullPointerException np) {
                            throw new Error();
                        }
                    }
                }
                else {
                    if (campgroundAvailability2.getCampsites().get(c.getCampsite_id()).getAvailabilities().isEmpty()) {
                        availabilityStatus = "Unavailable"; // certain unavailable sites will not return any availability info so need to override that here
                    }
                    else {
                        try {
                            availabilityStatus = campgroundAvailability2.getCampsites().get(c.getCampsite_id()).getAvailabilities().get(dateKey).toString();
                        } catch (NullPointerException np) {
                            throw new Error();
                        }
                    }
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

    private String createRgAvailApiString(LocalDate dateHolder) {
        // https://www.recreation.gov/api/camps/availability/campground/234718/month?start_date=2021-04-01T00%3A00%3A00.000Z
        StringBuilder campgroundAvailabilityUrlSB = new StringBuilder();
        campgroundAvailabilityUrlSB.append("https://www.recreation.gov/api/camps/availability/campground/");
        campgroundAvailabilityUrlSB.append(rgFacilityId);
        campgroundAvailabilityUrlSB.append("/month?start_date=");
        campgroundAvailabilityUrlSB.append(dateHolder.getYear());
        campgroundAvailabilityUrlSB.append("-");
        campgroundAvailabilityUrlSB.append(String.format("%02d", dateHolder.getMonthValue()));
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