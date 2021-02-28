package com.PersonalProjects.CampgroundFinder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Campsite {
    private Map<String, Object> availabilities;
    private String campsite_id;

    public Map<String, Object> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(Map<String, Object> availabilities) {
        this.availabilities = availabilities;
    }

    public String getCampsite_id() {
        return campsite_id;
    }

    public void setCampsite_id(String campsite_id) {
        this.campsite_id = campsite_id;
    }
}