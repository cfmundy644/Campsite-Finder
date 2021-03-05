package com.PersonalProjects.CampgroundFinder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geocode {
    private float resultsGeometryLocationLat;
    private float resultsGeometryLocationLng;

    public float getResultsGeometryLocationLat() {
        return resultsGeometryLocationLat;
    }

    public void setResultsGeometryLocationLat(float resultsGeometryLocationLat) {
        this.resultsGeometryLocationLat = resultsGeometryLocationLat;
    }

    public float getResultsGeometryLocationLng() {
        return resultsGeometryLocationLng;
    }

    public void setResultsGeometryLocationLng(float resultsGeometryLocationLng) {
        this.resultsGeometryLocationLng = resultsGeometryLocationLng;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("results")
    private void unpackNested(Map<String,Object> results[]) {
        Map<String, Object> geometry = (Map<String, Object>)results[0].get("geometry");
        Map<String, Object> location = (Map<String, Object>)geometry.get("location");
        double resultsGeometryLocationLatDouble = (double) (location.get("lat"));
        double resultsGeometryLocationLngDouble = (double) (location.get("lng"));
        this.resultsGeometryLocationLat = (float) resultsGeometryLocationLatDouble;
        this.resultsGeometryLocationLng = (float) resultsGeometryLocationLngDouble;
    }
}