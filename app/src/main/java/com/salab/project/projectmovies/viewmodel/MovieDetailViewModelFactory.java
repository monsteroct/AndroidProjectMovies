package com.salab.project.projectmovies.viewmodel;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.salab.project.projectmovies.model.Movie;

/**
 * View model factor to help creating view model of detail activity, since it require movie id as parameter
 */
public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Application application;
    private Movie movie;

    public MovieDetailViewModelFactory(Application application, Movie movie) {
        this.application = application;
        this.movie = movie;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MovieDetailViewModel(application, movie);
    }

}
