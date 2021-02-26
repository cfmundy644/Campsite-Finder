package com.PersonalProjects.CampgroundFinder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampgroundAvailability {
    // https://www.baeldung.com/jackson-nested-values
    private Map<String, Campsite> campsites;

    public Map<String, Campsite> getCampsites() {
        return campsites;
    }

    public void setCampsites(Map<String, Campsite> campsites) {
        this.campsites = campsites;
    }
}
