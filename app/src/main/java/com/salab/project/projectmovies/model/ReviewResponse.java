package com.salab.project.projectmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * The desired list of reviews is preserved in 'results' field of fetched JSON Object,
 * so this helper class make gson can automatically resolve in to POJO.
 */
public class ReviewResponse {
    @SerializedName("results")
    private List<Review> reviewList;

    public List<Review> getReviewList() {
        return reviewList;
    }
}
