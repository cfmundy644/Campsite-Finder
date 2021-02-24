package com.PersonalProjects.CampgroundFinder;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
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
}
