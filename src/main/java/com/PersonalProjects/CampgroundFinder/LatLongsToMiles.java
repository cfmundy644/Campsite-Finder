package com.PersonalProjects.CampgroundFinder;

public class LatLongsToMiles {
    private double lat1;
    private double lon1;
    private double lat2;
    private double lon2;
    private double dist;

    public LatLongsToMiles(double lat1, double lon1, double lat2, double lon2) {
        this.lat1 = lat1;
        this.lon1 = lon1;
        this.lat2 = lat2;
        this.lon2 = lon2;

        // https://www.geodatasource.com/developers/java
        double theta = lon1 - lon2;
        double distHelper = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        distHelper = Math.acos(distHelper);
        distHelper = Math.toDegrees(distHelper);
        distHelper = distHelper * 60 * 1.1515;
        this.dist = distHelper;
    }

    public double getDist() {return dist;}
}
