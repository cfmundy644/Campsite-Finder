package com.PersonalProjects.CampgroundFinder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Campsite {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String rgCampsiteId; // rg = recreation.gov
    private String rgFacilityId; // rg = recreation.gov
    private String campsiteName;
    private String campsiteType;
    private String typeOfUse;
    private String campgroundLoop;
    private boolean campsiteAccessible;
    private double longitude;
    private double latitude;
    private Date createdDate;
    private Date lastUpdatedDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRgCampsiteId() {
        return rgCampsiteId;
    }

    public void setRgCampsiteId(String rgCampsiteId) {
        this.rgCampsiteId = rgCampsiteId;
    }

    public String getRgFacilityId() {
        return rgFacilityId;
    }

    public void setRgFacilityId(String rgFacilityId) {
        this.rgFacilityId = rgFacilityId;
    }

    public String getCampsiteName() {
        return campsiteName;
    }

    public void setCampsiteName(String campsiteName) {
        this.campsiteName = campsiteName;
    }

    public String getCampsiteType() {
        return campsiteType;
    }

    public void setCampsiteType(String campsiteType) {
        this.campsiteType = campsiteType;
    }

    public String getTypeOfUse() {
        return typeOfUse;
    }

    public void setTypeOfUse(String typeOfUse) {
        this.typeOfUse = typeOfUse;
    }

    public String getCampgroundLoop() {
        return campgroundLoop;
    }

    public void setCampgroundLoop(String campgroundLoop) {
        this.campgroundLoop = campgroundLoop;
    }

    public boolean isCampsiteAccessible() {
        return campsiteAccessible;
    }

    public void setCampsiteAccessible(boolean campsiteAccessible) {
        this.campsiteAccessible = campsiteAccessible;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
