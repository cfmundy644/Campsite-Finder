package com.PersonalProjects.CampgroundFinder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geocode {
    private double resultsGeometryLocationLat;
    private double resultsGeometryLocationLng;

    public double getResultsGeometryLocationLat() {
        return resultsGeometryLocationLat;
    }

    public void setResultsGeometryLocationLat(double resultsGeometryLocationLat) {
        this.resultsGeometryLocationLat = resultsGeometryLocationLat;
    }

    public double getResultsGeometryLocationLng() {
        return resultsGeometryLocationLng;
    }

    public void setResultsGeometryLocationLng(double resultsGeometryLocationLng) {
        this.resultsGeometryLocationLng = resultsGeometryLocationLng;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("results")
    private void unpackNested(Map<String,Object> results[]) {
        Map<String, Object> geometry = (Map<String, Object>)results[0].get("geometry");
        Map<String, Object> location = (Map<String, Object>)geometry.get("location");
        this.resultsGeometryLocationLat = (double)location.get("lat");
        this.resultsGeometryLocationLng = (double)location.get("lng");
    }
}