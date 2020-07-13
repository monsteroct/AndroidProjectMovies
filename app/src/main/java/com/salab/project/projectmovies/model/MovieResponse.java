package com.salab.project.projectmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The desired list of movies is preserved in 'results' field of fetched JSON Object,
 * so this helper class make gson can automatically resolve in to POJO.
 */
public class MovieResponse {
    @SerializedName("results")
    private List<Movie> movieList;

    public List<Movie> getMovieList() {
        return movieList;
    }
}
