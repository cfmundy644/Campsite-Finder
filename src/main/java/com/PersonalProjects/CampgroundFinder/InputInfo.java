package com.PersonalProjects.CampgroundFinder;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.time.LocalDate;

public class InputInfo {
    private double latitude;
    private double longitude;
    private int radius;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate checkInDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate checkOutDate;

    public double getLatitude() {return latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}

    public int getRadius() {return radius;}
    public void setRadius(int radius) {this.radius = radius;}

    public LocalDate getCheckInDate() {return checkInDate;}
    public void setCheckInDate(LocalDate checkInDate) {this.checkInDate = checkInDate;}

    public LocalDate getCheckOutDate() {return checkOutDate;}
    public void setCheckOutDate(LocalDate checkOutDate) {this.checkOutDate = checkOutDate;}
}
