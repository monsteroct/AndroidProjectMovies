package com.salab.project.projectmovies.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.salab.project.projectmovies.model.Movie;
import com.salab.project.projectmovies.model.Repository;
import com.salab.project.projectmovies.model.Review;
import com.salab.project.projectmovies.model.Trailer;

import java.util.List;

/**
 * View model of detail activity, preserves selected movie, its list of trailers, and list of reviews
 */
public class MovieDetailViewModel extends ViewModel {

    private static final String TAG = MovieDetailViewModel.class.getSimpleName();

    private int movieId;
    //MutableLiveData allows manually data changes, so its much suitable for async loading from servers or database
    private MutableLiveData<Movie> movie;
    private MutableLiveData<List<Review>> reviewList;
    private MutableLiveData<List<Trailer>> trailerList;

    //required to call the same repository instance, because the repository also holds the reference
    //of this view model instance, so it will know where to call back the results
    private Repository movieRepository;


    public MovieDetailViewModel(Application application, Movie movie) {
        //init ViewModel create default empty LiveData container before data update from
        //server or database
        this.movie = new MutableLiveData<Movie>();
        this.movie.setValue(movie);
        movieId = movie.getMovieId();

        reviewList = new MutableLiveData<List<Review>>();
        trailerList = new MutableLiveData<List<Trailer>>();

        movieRepository = new Repository(application,this);
        Log.d(TAG, "MovieDetail view model is instantiated ");

        //call update and initialize the live data
        loadMovieById(movieId);
        loadReviewList(application.getApplicationContext(), movieId);
        loadTrailerList(application.getApplicationContext(), movieId);
    }


    private void loadMovieById(int movieId){
        movieRepository.loadMovieById(movieId);
    }



    /**
     * every time Favorite button is clicked, this method will be invoked and swap favorite flag
     */
    public void swapMovieFavorite(){
        if (movie.getValue() != null) {
            Movie updatedMovie = movie.getValue();
            boolean favorite = movie.getValue().isFavorite();
            updatedMovie.setFavorite(!favorite);
            movie.setValue(updatedMovie);
            Log.d(TAG, "Movie favorite changed to " + !favorite );
        }
    }

    /**
     * Database only preserve favorite movies. The favorite ones will be insert or update
     * the rest will be delete from database if they exist.
     */
    public void saveMovieChanges(){
        if (movie.getValue() != null) {
            if (movie.getValue().isFavorite()){
                movieRepository.insertMovie(movie.getValue());
            } else {
                movieRepository.deleteMovie(movie.getValue());
            }
        }
    }

    public void updateMovieFavorite(Movie movie){
        if (movie != null && movie.isFavorite()){
            //currently only favorite movie will be saved in local database
            //the detail display relies on parcelable from main activity
            this.movie.setValue(movie);
        }
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList.setValue(reviewList);
    }

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList.setValue(trailerList);
    }

    public void loadReviewList(Context context, int movieId){
        movieRepository.fetchReviews(context, movieId);
    }
    public void loadTrailerList(Context context, int movieId){
        movieRepository.fetchTrailers(context, movieId);
    }

    public MutableLiveData<Movie> getMovie() {
        return movie;
    }

    public MutableLiveData<List<Review>> getReviewList() {
        return reviewList;
    }

    public MutableLiveData<List<Trailer>> getTrailerList() {
        return trailerList;
    }
}
