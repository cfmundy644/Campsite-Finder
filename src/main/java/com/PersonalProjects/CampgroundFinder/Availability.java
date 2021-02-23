// this is the second quotation class (one in from outer class) from the campsite availability API JSON response
package com.PersonalProjects.CampgroundFinder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Availability {

    private Map<String, Object> availabilities;

    public Map<String, Object> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(Map<String, Object> availabilities) {
        this.availabilities = availabilities;
    }

}