package com.salab.project.projectmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.salab.project.projectmovies.adapter.MovieAdapter;
import com.salab.project.projectmovies.model.Movie;
import com.salab.project.projectmovies.model.Repository;
import com.salab.project.projectmovies.utility.NetworkUtility;
import com.salab.project.projectmovies.viewmodel.MainViewModel;

import java.util.List;

/**
 * Activity to display list of movies
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickListener {

    //constants
    public static final String MOVIE_PARCELABLE_INTENT_KEY = "selectedMovie";
    //API provides poster in different sizes, and 150px one is chosen
    public static final int posterImageSize = 150;
    private static final String TAG = MainActivity.class.getSimpleName();

    //default number of grid for the recyclerView is 2, and it will be recalculated based on
    //real screen size by method calculateLayoutSpan() for every time onCreate() got called.
    private static int gridSpanCount = 2;
    //calculateLayoutSpan() need the reference to update span count
    private GridLayoutManager movieLayoutManger;

    private RecyclerView mMoviePostersRecyclerView;
    private TextView mErrorMsgTextView;
    private ProgressBar mLoadingProgressBar;
    private MovieAdapter movieAdapter;
    private MainViewModel movieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMsgTextView = findViewById(R.id.tv_poster_error_msg);
        mLoadingProgressBar = findViewById(R.id.pb_main_activity);
        //default page is favorite
        setTitle(getString(R.string.favorites));

        initRecyclerView();
        initViewModel();
    }

    /**
     * Initialize the view model which holds the list of movies in form of live data.
     * As list of movies is changed, adapter is called and update its content and views.
     */
    private void initViewModel() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication());

        movieViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        Log.d(this.getClass().getSimpleName(), movieViewModel.getMovieList().toString());
        movieViewModel.getMovieList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movieList) {
                movieAdapter.setMovieAdapterList(movieList);
                Log.d(TAG, "LiveData: movie list is changed");
                if (movieList == null || movieList.size() == 0){
                    //empty list
                    if (getTitle() == getString(R.string.favorites)) {
                        //empty favorite
                        String noFavorites = getString(R.string.error_msg_no_favorites);
                        switchErrorPage(true, noFavorites);
                    } else {
                        //empty network. could be network issue
                        String networkIssue = getString(R.string.error_msg_no_network);
                        switchErrorPage(true, networkIssue);
                    }
                } else {
                    switchLoadingPage(false);
                }
            }
        });
    }

    private void initRecyclerView() {

        mMoviePostersRecyclerView = findViewById(R.id.rv_poster);

        movieLayoutManger = new GridLayoutManager(MainActivity.this, gridSpanCount);
        mMoviePostersRecyclerView.setLayoutManager(movieLayoutManger);
        mMoviePostersRecyclerView.setHasFixedSize(false);

        movieAdapter = new MovieAdapter(this);
        mMoviePostersRecyclerView.setAdapter(movieAdapter);

        //depends configuration sets a reasonable span for grid layout.
        calculateLayoutSpan();
        switchLoadingPage(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * ask view model to provide new set of movies corresponding to the button clicked
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedItemId = item.getItemId();
        switch (selectedItemId) {
            case R.id.action_sort_by_pop:
                //type constant that repository required to separate the loading tasks
                requestData(Repository.TYPE_MOVIE_POPULAR);
                setTitle(getString(R.string.popular));
                return true;
            case R.id.action_sort_by_rate:
                requestData(Repository.TYPE_MOVIE_TOP_RATED);
                setTitle(getString(R.string.top_rated));
                return true;
            case R.id.action_favorite:
                requestData(Repository.TYPE_MOVIE_FAVORITE);
                setTitle(getString(R.string.favorites));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void requestData(String requestType) {
        Log.d(TAG, requestType + " clicked");
        //only favorite function is offline
        if (NetworkUtility.isOnline(this) || requestType == Repository.TYPE_MOVIE_FAVORITE) {
            movieViewModel.updateData(getApplication(), requestType);
            switchLoadingPage(true);
        } else {
            String networkMsg = getString(R.string.error_msg_no_network);
            switchErrorPage(true, networkMsg);
        }
    }

    /**
     * Start a detail activity when the poster is clicked
     */
    @Override
    public void onClickMovie(Movie selectedMovie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        //carries the movie object in form or parcelable to detail activity
        //the display will rely on the info inside
        intent.putExtra(MOVIE_PARCELABLE_INTENT_KEY, selectedMovie);
        startActivity(intent);
    }

    /**
     * The method is to control if need to show error message view to the user, and the
     * recyclerView will be hide if error message is shown, vice versa.
     * Three main views error_msg, progress bar, recycler view only one will be shown
     */
    public void switchErrorPage(boolean on, String errorMsg) {
        if (on) {
            mErrorMsgTextView.setVisibility(View.VISIBLE);
            mErrorMsgTextView.setText(errorMsg);
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            mMoviePostersRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mErrorMsgTextView.setVisibility(View.INVISIBLE);
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            mMoviePostersRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void switchLoadingPage(boolean on) {
        if (on) {
            mErrorMsgTextView.setVisibility(View.INVISIBLE);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mMoviePostersRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mErrorMsgTextView.setVisibility(View.INVISIBLE);
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            mMoviePostersRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Automatically calculate the span count based on the width of screen (in dp) to more
     * flexibly fit different screen size, calculation based on 150dp (posterImageSize)
     * width for each poster image.
     */
    public void calculateLayoutSpan(){
    /*reference : https://stackoverflow.com/questions/39436115/auto-fit-according-to-screen-size-in-grid-layout-android*/
        DisplayMetrics displayMetrics = MainActivity.this.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int dpi = displayMetrics.densityDpi;
        int widthDp = widthPixels * 160 / dpi;

        gridSpanCount = widthDp/posterImageSize;
        movieLayoutManger.setSpanCount(gridSpanCount);
        Log.d(TAG, "span is calculated and update to " + gridSpanCount);
    }

}