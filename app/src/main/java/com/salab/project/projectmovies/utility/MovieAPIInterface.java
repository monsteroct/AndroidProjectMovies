package com.salab.project.projectmovies.utility;

import com.salab.project.projectmovies.model.MovieResponse;
import com.salab.project.projectmovies.model.ReviewResponse;
import com.salab.project.projectmovies.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for API fetching
 */
public interface MovieAPIInterface {

    //list of movies /movie/popular, /movie/top_rated
    @GET("movie/{sort_by}")
    Call<MovieResponse> getMovies(@Path("sort_by") String sortBy, @Query("api_key") String key);

    //reviews of a movie /movie/{movie_id}/reviews
    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getReviews(@Path("movie_id") int movieId, @Query("api_key") String key);

    //trailers of a movie /movie/{movie_id}/videos
    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getTrailers(@Path("movie_id") int movieId, @Query("api_key") String key);
}
