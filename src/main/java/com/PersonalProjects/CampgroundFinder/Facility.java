package com.PersonalProjects.CampgroundFinder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Facility implements Comparable<Facility> {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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

    public Integer getId() {return id;};
    public void setId(Integer id) {this.id = id;}

    public String getRgFacilityId() {return rgFacilityId;}
    public void setRgFacilityId(String rgFacilityId) {this.rgFacilityId = rgFacilityId;}

    public String getRgFacilityName() {return rgFacilityName;}
    public void setRgFacilityName(String rgFacilityName) {this.rgFacilityName = rgFacilityName;}

    public double getLatitude() {return latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}

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

    @Override
    public int compareTo(Facility that) {
        if (this.dist < that.dist) {return -1;}
        else if (this.dist == that.dist) {return 0;}
        else {return 1;}
    }

}
