package com.PersonalProjects.CampgroundFinder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Campsite {
    private Map<String, Object> availabilities;

    public Map<String, Object> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(Map<String, Object> availabilities) {
        this.availabilities = availabilities;
    }
}