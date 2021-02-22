package com.PersonalProjects.CampgroundFinder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Facility {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String rgFacilityId; // rg = recreation.gov
    private String rgFacilityName;
    private double latitude;
    private double longitude;

    public Integer getId() {return id;};
    public void setId(Integer id) {this.id = id;}

    public String getRgFacilityId() {return rgFacilityId;}
    public void setRgFacilityId(String rgFacilityId) {this.rgFacilityId = rgFacilityId;};

    public String getRgFacilityName() {return rgFacilityName;}
    public void setRgFacilityName(String rgFacilityName) {this.rgFacilityName = rgFacilityName;};

    public double getLatitude() {return latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;};

    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;};

}
