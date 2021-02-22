package com.PersonalProjects.CampgroundFinder;

import java.util.Date;

public class InputInfo {
    private double latitude;
    private double longitude;
    private int radius;
    private Date checkInDate;
    private Date checkOutDate;

    public double getLatitude() {return latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}

    public int getRadius() {return radius;}
    public void setRadius(int radius) {this.radius = radius;}

    public Date getCheckInDate() {return checkInDate;}
    public void setCheckInDate(Date checkInDate) {this.checkInDate = checkInDate;}

    public Date getCheckOutDate() {return checkOutDate;}
    public void setCheckOutDate(Date checkOutDate) {this.checkOutDate = checkOutDate;}
}
