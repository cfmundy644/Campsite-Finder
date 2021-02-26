package com.PersonalProjects.CampgroundFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.time.LocalDate;

public class InputInfo {
    private String streetAddress;
    private String city;
    private String state;
    private String zip;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate checkInDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate checkOutDate;

    private double latitude;
    private double longitude;

    public double getLatitude() {return latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}

    private int radius;

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getRadius() {return radius;}
    public void setRadius(int radius) {this.radius = radius;}

    public LocalDate getCheckInDate() {return checkInDate;}
    public void setCheckInDate(LocalDate checkInDate) {this.checkInDate = checkInDate;}

    public LocalDate getCheckOutDate() {return checkOutDate;}
    public void setCheckOutDate(LocalDate checkOutDate) {this.checkOutDate = checkOutDate;}

    public void setLatLong(String googleKey) throws Exception {

        // sample api call "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY"
        StringBuilder geocodeAPIUrlSB = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?address=");
        geocodeAPIUrlSB.append(streetAddress.replaceAll(" ", "%20"));
        geocodeAPIUrlSB.append(",%20");
        geocodeAPIUrlSB.append(city.replaceAll(" ", "%20"));
        geocodeAPIUrlSB.append(",%20");
        geocodeAPIUrlSB.append(state);
        geocodeAPIUrlSB.append(",%20");
        geocodeAPIUrlSB.append(zip);
        geocodeAPIUrlSB.append("&key=");
        geocodeAPIUrlSB.append(googleKey);
        Geocode geocode = null;
        URL geocodeAPIUrl = null;
        ObjectMapper geocodeMapper = new ObjectMapper();
        try {
            geocodeAPIUrl = new URL(geocodeAPIUrlSB.toString());
            geocode = geocodeMapper.readValue(geocodeAPIUrl, Geocode.class);
        } catch (Exception ex) {
            throw new Exception();
        }
        latitude = geocode.getResults()[0].getGeometry().getLocation().getLat();
        longitude = geocode.getResults()[0].getGeometry().getLocation().getLng();
    }

    public double calcDistToFac(Facility f) {
        double lat1, lon1, lat2, lon2, dist;
        lat1 = latitude;
        lon1 = longitude;
        lat2 = f.getLatitude();
        lon2 = f.getLongitude();

        // https://www.geodatasource.com/developers/java
        double theta = lon1 - lon2;
        double distHelper = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        distHelper = Math.acos(distHelper);
        distHelper = Math.toDegrees(distHelper);
        distHelper = distHelper * 60 * 1.1515;
        f.setDist(distHelper);
        return distHelper;
    }

}
