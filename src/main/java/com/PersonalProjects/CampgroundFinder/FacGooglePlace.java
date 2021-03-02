package com.PersonalProjects.CampgroundFinder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FacGooglePlace {
    // https://www.baeldung.com/jackson-nested-values
    private String resultsName;
    private float resultsRating;
    private int resultsUser_Ratings_Total;
    private String resultsPlace_id;


    public String getResultsName() {
        return resultsName;
    }

    public void setResultsName(String resultsName) {
        this.resultsName = resultsName;
    }

    public float getResultsRating() {
        return resultsRating;
    }

    public void setResultsRating(float resultsRating) {
        this.resultsRating = resultsRating;
    }

    public int getResultsUser_Ratings_Total() {
        return resultsUser_Ratings_Total;
    }

    public void setResultsUser_Ratings_Total(int resultsUser_Ratings_Total) {
        this.resultsUser_Ratings_Total = resultsUser_Ratings_Total;
    }

    public String getResultsPlace_id() {
        return resultsPlace_id;
    }

    public void setResultsPlace_id(String resultsPlace_id) {
        this.resultsPlace_id = resultsPlace_id;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("results")
    private void unpackNested(Map<String,Object> results[]) {
        this.resultsName = (String)results[0].get("name");
        this.resultsRating = (float)results[0].get("rating");
        this.resultsUser_Ratings_Total = (int)results[0].get("user_ratings_total");
        this.resultsPlace_id = (String)results[0].get("place_id");
    }
}
