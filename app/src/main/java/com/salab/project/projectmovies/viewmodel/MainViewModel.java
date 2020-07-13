package com.salab.project.projectmovies.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.salab.project.projectmovies.model.Movie;
import com.salab.project.projectmovies.model.Repository;

import java.util.List;

/**
 * View model of main activity, preserves list of movies
 */
//context(application) is needed for server API calls -> AndroidViewModel
public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<List<Movie>> movieList;
    private Repository mRepository;

    public MainViewModel(Application application){
        super(application);

        mRepository = new Repository(application, this);
        Log.d(TAG, "Movie ViewModel is initialized");

        if (movieList == null){
            movieList = new MutableLiveData<List<Movie>>();
            //change default page when app launching here
            updateData(application, Repository.TYPE_MOVIE_FAVORITE);
        }

    }

    public LiveData<List<Movie>> getMovieList() {
        return movieList;
    }

    public void updateData(Application application, String requestType){
        Log.d(TAG, "ViewModel data is updating");
        mRepository.loadMovieList(application, requestType);
    }

    public void setMovieList(List<Movie> movieList){
        this.movieList.setValue(movieList);
        Log.d(TAG, "ViewModel data has been updated");
    }

}
