package com.PersonalProjects.CampgroundFinder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

public class GeocodeAPICall {
    private Geocode geocode;
    private boolean success;

    public GeocodeAPICall(InputInfo inputInfo, String googleKey) {
        success = true;
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
        try {
            geocodeAPIUrl = new URL(geocodeAPIUrlSB.toString());
            geocode = geocodeMapper.readValue(geocodeAPIUrl, Geocode.class);
        } catch (Exception ex) {
            success = false;
        }
    }

    public Geocode getGeocode() {return geocode;}
    public boolean getSuccess() {return success;}
}
