package com.salab.project.projectmovies.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.salab.project.projectmovies.viewmodel.MainViewModel;
import com.salab.project.projectmovies.viewmodel.MovieDetailViewModel;
import com.salab.project.projectmovies.R;
import com.salab.project.projectmovies.model.database.MovieDatabase;
import com.salab.project.projectmovies.utility.AppExecutors;
import com.salab.project.projectmovies.utility.MovieAPIUtility;
import com.salab.project.projectmovies.utility.MovieAPIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class for model classes Movie, Review, Trailer and the instance will be connected
 * with view model classes. This class will handle data request and fetch them from database or
 * remote server and call view model back after async loading tasks complete.
 */
public class Repository {
    private static final String TAG = Repository.class.getSimpleName();

    public static final String TYPE_MOVIE_POPULAR = "popular";
    public static final String TYPE_MOVIE_TOP_RATED = "top_rate";
    public static final String TYPE_MOVIE_FAVORITE = "favorite";

    private MovieDatabase mDb;
    private ViewModel mViewModel;

    /**
     * A constructor and the repository instance will hold the reference of calling view model
     */
    public Repository(Application application, ViewModel mViewModel){
        this.mViewModel = mViewModel;
        mDb = MovieDatabase.getMovieDatabaseInstance(application.getApplicationContext());
        Log.d(TAG, "MovieRepository is instantiated");
    }


    public void loadMovieList(Application application, String movieType){
        Context context = application.getApplicationContext();
        Log.d(TAG, "Load " + movieType + " movie list");

        switch (movieType){
            case Repository.TYPE_MOVIE_TOP_RATED:
                loadTopRatedMovieList(context);
                break;
            case Repository.TYPE_MOVIE_FAVORITE:
                loadFavoriteMovieList();
                break;
            case Repository.TYPE_MOVIE_POPULAR: default:
                loadPopularMovieList(context);
                break;
        }
    }

    private void loadPopularMovieList(Context context){
        fetchMoviesFromServer(context, MovieAPIUtility.SORT_BY_POPULARITY);

    }

    private void loadTopRatedMovieList(Context context){
        fetchMoviesFromServer(context, MovieAPIUtility.SORT_BY_RATING);
    }

    private void loadFavoriteMovieList(){
        AppExecutors.getIOExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Movie> newMovieList = mDb.movieDao().queryFavoriteMovies();
                AppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        //back to main thread
                        passMovieListBackToViewModel(newMovieList);
                    }
                });

            }
        });
    }

    /**
     * The method will help loadFavoriteMovieList() pass the loaded data back to view model
     */
    private void passMovieListBackToViewModel(List<Movie> newMovieList){
        if (mViewModel instanceof MainViewModel){
            MainViewModel mainViewModel = (MainViewModel) mViewModel;
            mainViewModel.setMovieList(newMovieList);
            Log.d(TAG, "Pass loaded movie list back to view model");
        }
    }

    public void loadMovieById(int movieId){
        AppExecutors.getIOExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Movie newMovie = mDb.movieDao().queryByMovieId(movieId);
                AppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        //back to main thread
                        passMovieBackToViewModel(newMovie);
                    }
                });
            }
        });
    }
    /**
     * The method will help loadMovieById() pass the loaded data back to view model
     */
    private void passMovieBackToViewModel(Movie newMovie){
        if (mViewModel instanceof MovieDetailViewModel){
            MovieDetailViewModel movieDetailViewModel = (MovieDetailViewModel) mViewModel;
            movieDetailViewModel.updateMovieFavorite(newMovie);
            Log.d(TAG, "Pass loaded movie back to view model");
        }

    }

    public void deleteMovie(Movie movie){
        AppExecutors.getIOExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().deleteMovie(movie);
                Log.d(TAG, "Movie " + movie.getMovieId() + " has been removed from DB");
            }
        });
    }

    public void insertMovie(Movie movie){
        AppExecutors.getIOExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().insertMovie(movie);
                Log.d(TAG, "Movie " + movie.getMovieId() + " has been added into DB");
            }
        });
    }


    public void fetchMoviesFromServer(Context context, String sortBy) {
        //context is required because resources needs to be accessed from context
        String api_key = context.getString(R.string.tmdb_api_key);
        MovieAPIInterface service = MovieAPIUtility.buildService();
        Call<MovieResponse> call = service.getMovies(sortBy, api_key);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movieList = response.body().getMovieList();
                    //help method pass the data back to view model requesting the data
                    Repository.this.passMovieListBackToViewModel(movieList);
                    Log.d(TAG, "Fetching movie list from server succeed");
                } else {
                    Log.d(TAG, "Fetching movie list failed due to bad server response");
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d(TAG, "Fetching movie list failed encountered errors : " + t.getMessage());
            }
        });
    }

    public void fetchReviews(Context context, int movieId){
        String apiKey = context.getString(R.string.tmdb_api_key);
        MovieAPIInterface service = MovieAPIUtility.buildService();
        Call<ReviewResponse> call = service.getReviews(movieId, apiKey);

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Review> reviewRaw = response.body().getReviewList();
                    if (mViewModel instanceof MovieDetailViewModel){
                        MovieDetailViewModel movieDetailViewModel = (MovieDetailViewModel) mViewModel;
                        movieDetailViewModel.setReviewList(reviewRaw);
                    }
                    Log.d(TAG,"Fetching review list from server succeed");

                } else {
                    Log.d(TAG,"Fetching review list failed due to bad server response");
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.d(TAG,"Fetching review list failed encountered errors : " + t.getMessage());
            }
        });
    }


    public void fetchTrailers(Context context, int movieId){
        String apiKey = context.getString(R.string.tmdb_api_key);
        MovieAPIInterface service = MovieAPIUtility.buildService();
        Call<TrailerResponse> call = service.getTrailers(movieId, apiKey);

        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Trailer> trailerRaw = response.body().getTrailerList();
                    if (mViewModel instanceof MovieDetailViewModel){
                        MovieDetailViewModel movieDetailViewModel = (MovieDetailViewModel) mViewModel;
                        movieDetailViewModel.setTrailerList(trailerRaw);
                    }
                    Log.d(TAG,"Fetching trailer list from server succeed");

                } else {
                    Log.d(TAG,"Fetching trailer list failed due to bad server response");
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.d(TAG,"Fetching trailer list failed encountered errors : " + t.getMessage());
            }
        });
    }

}
