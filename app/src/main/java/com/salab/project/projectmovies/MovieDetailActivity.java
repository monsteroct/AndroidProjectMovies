package com.salab.project.projectmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.salab.project.projectmovies.adapter.ReviewAdapter;
import com.salab.project.projectmovies.adapter.TrailerAdapter;
import com.salab.project.projectmovies.databinding.ActivityMovieDetailBinding;
import com.salab.project.projectmovies.model.Movie;
import com.salab.project.projectmovies.model.Review;
import com.salab.project.projectmovies.model.Trailer;
import com.salab.project.projectmovies.utility.NetworkUtility;
import com.salab.project.projectmovies.utility.MovieAPIUtility;
import com.salab.project.projectmovies.viewmodel.MovieDetailViewModel;
import com.salab.project.projectmovies.viewmodel.MovieDetailViewModelFactory;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;


/**
 * Activity to display detail information of a movie and its trailer and reviews
 */
public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerOnClickListener{

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    //adapters references will be needed to update the content inside
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    private MovieDetailViewModel mViewModel;
    //button of add / remove favorite
    private MenuItem favoriteMenuItem;
    //auto-generated ViewBinding class
    private ActivityMovieDetailBinding activityMovieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize ViewBinding class and activity layout
        activityMovieDetailBinding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        View rootView = activityMovieDetailBinding.getRoot();
        setContentView(rootView);
        
        initReviewRecyclerView();
        initTrailerRecyclerView();
        //check network connection and show error messages
        checkConnectivity();

        //rotation can still preserve the intent, so basically there must be a intent to start the activity
        Intent intent = getIntent();
        Log.d(this.getClass().getSimpleName(),intent.toString());
        if (intent != null && intent.hasExtra(MainActivity.MOVIE_PARCELABLE_INTENT_KEY)){
            //only bind date when this view is activated by valid intent
            Movie selectedMovie = intent.getParcelableExtra(MainActivity.MOVIE_PARCELABLE_INTENT_KEY);
            if (selectedMovie != null){
                initBindContentView(selectedMovie);
                initViewModel(selectedMovie);

                Log.d(TAG,"Detail view created with movie id: " + selectedMovie.getMovieId());
            }
        }
    }

    /**
     * Detail activity view model preserves movie and its trailers and reviews
     */
    private void initViewModel(Movie selectedMovie) {
        //factor class needed to create view model with parameters (movie id)
        MovieDetailViewModelFactory factory =
                new MovieDetailViewModelFactory(this.getApplication(), selectedMovie);
        mViewModel = new ViewModelProvider(this, factory).get(MovieDetailViewModel.class);

        //changed movie refers to favorite button clicked, so update appearance
        mViewModel.getMovie().observe(this, new Observer<Movie>()
        {
            @Override
            public void onChanged(Movie movie) {
                Log.d(TAG, "LiveData: movie is changed");
                updateFavoriteButton();
            }
        });

        //for now reviews and trailer should only be changed as data is loaded from server
        mViewModel.getReviewList().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                Log.d(TAG, "LiveData: review list is changed");
                mReviewAdapter.setReviewList(reviews);

                //if no review available show msg and placeholder
                if (reviews == null || reviews.size() == 0) {
                    String noContent = getString(R.string.error_msg_no_reviews);
                    activityMovieDetailBinding.rvReview.setVisibility(View.INVISIBLE);
                    activityMovieDetailBinding.tvReviewOnError.setVisibility(View.VISIBLE);
                    activityMovieDetailBinding.tvReviewOnError.setText(noContent);
                } else {
                    activityMovieDetailBinding.rvReview.setVisibility(View.VISIBLE);
                    activityMovieDetailBinding.tvReviewOnError.setVisibility(View.INVISIBLE);
                }
            }
        });
        mViewModel.getTrailerList().observe(this, new Observer<List<Trailer>>() {
            @Override
            public void onChanged(List<Trailer> trailers) {
                Log.d(TAG, "LiveData: trailer list is changed");
                mTrailerAdapter.setTrailerList(trailers);

                //if no trailer available show msg and placeholder
                if (trailers == null || trailers.size() == 0) {
                    String noContent = getString(R.string.error_msg_no_trailer);
                    activityMovieDetailBinding.rvTrailer.setVisibility(View.INVISIBLE);
                    activityMovieDetailBinding.tvTrailerOnError.setVisibility(View.VISIBLE);
                    activityMovieDetailBinding.tvTrailerOnError.setText(noContent);
                } else {
                    activityMovieDetailBinding.rvTrailer.setVisibility(View.VISIBLE);
                    activityMovieDetailBinding.tvTrailerOnError.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    private void initReviewRecyclerView() {
        RecyclerView mReviewRecyclerView = findViewById(R.id.rv_review);
        LinearLayoutManager reviewManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(reviewManager);
        //Whole page is wrapper in scroll view, no need for additional scroll
        mReviewRecyclerView.setNestedScrollingEnabled(false);

        mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);
    }

    private void initTrailerRecyclerView() {
        RecyclerView mTrailerRecyclerView = findViewById(R.id.rv_trailer);
        LinearLayoutManager trailerManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false);
        mTrailerRecyclerView.setLayoutManager(trailerManager);

        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
    }

    /**
     * Get the individual view reference from ViewBinding class to initialize the layout.
     */
    private void initBindContentView(Movie selectedMovie){
        URL posterURL = MovieAPIUtility.buildPosterURL(selectedMovie.getPosterPath(), MovieAPIUtility.IMAGE_SIZE_342);
        Picasso.get()
                .load(posterURL.toString())
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)
                .into(activityMovieDetailBinding.ivDetailPoster);

        activityMovieDetailBinding.tvDetailVoteAvg.setText(String.valueOf(selectedMovie.getVoteAverage()));
        activityMovieDetailBinding.tvDetailVoteFrom.setText(getString(R.string.format_detail_view_vote_from, selectedMovie.getVoteCount()));
        activityMovieDetailBinding.tvDetailReleaseDate.setText(selectedMovie.getReleaseData());
        activityMovieDetailBinding.tvDetailOverview.setText(selectedMovie.getOverview());
        activityMovieDetailBinding.tvDetailTitle.setText(selectedMovie.getTitle());

        setTitle(selectedMovie.getTitle());
    }


    private void checkConnectivity() {
        //whether show network error pages or not is opposite to the connectivity
        String networkError = getString(R.string.error_msg_no_network);
        switchNetworkErrorPage(!NetworkUtility.isOnline(this), networkError);
    }

    private void switchNetworkErrorPage(boolean on, String msgOnErrorPage){
        if (on) {
            activityMovieDetailBinding.rvReview.setVisibility(View.INVISIBLE);
            //the network error msg could be override by empty trailer/review msg
            activityMovieDetailBinding.tvReviewOnError.setVisibility(View.VISIBLE);
            activityMovieDetailBinding.tvReviewOnError.setText(msgOnErrorPage);

            activityMovieDetailBinding.tvTrailerOnError.setVisibility(View.VISIBLE);
            activityMovieDetailBinding.tvTrailerOnError.setText(msgOnErrorPage);
        } else {
            activityMovieDetailBinding.rvReview.setVisibility(View.VISIBLE);
            activityMovieDetailBinding.tvReviewOnError.setVisibility(View.INVISIBLE);
            activityMovieDetailBinding.tvTrailerOnError.setVisibility(View.INVISIBLE);
        }
    }

    private void updateFavoriteButton() {
        //could be invoked before menu creation, need an extra check
        if (favoriteMenuItem != null && mViewModel.getMovie().getValue() != null) {
            //update favorite button is hollow or solid based on 'favorite' stored in Movie
            Boolean isFavorite = mViewModel.getMovie().getValue().isFavorite();
            if (isFavorite) {
                //has been collected in favorites. show solid one
                favoriteMenuItem.setTitle(getString(R.string.menu_item_remove_favorite));
                favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_24);
            } else {
                //no in favorites. show hollow one
                favoriteMenuItem.setTitle(getString(R.string.menu_item_add_favorite));
                favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        favoriteMenuItem = menu.findItem(R.id.action_add_remove_favorite);
        updateFavoriteButton();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_add_remove_favorite:
                onFavoriteButtonClick();
                break;
            case R.id.action_share:
                onShareButtonClick();
                break;
            default:
                return super.onOptionsItemSelected(item);                 
        }
        return true;
    }

    private void onShareButtonClick() {
        Movie movie = mViewModel.getMovie().getValue();
        List<Trailer> trailers = mViewModel.getTrailerList().getValue();

        if (movie != null && trailers != null) {
            String movieTitle = movie.getTitle();
            String videoPath = trailers.get(0).getKey();
            String url = MovieAPIUtility.buildVideoUri(videoPath).toString();
            String text = getString(R.string.share_trailer_message, movieTitle, url);

            ShareCompat.IntentBuilder.from(this)
                    .setText(text)
                    .setType("text/plain")
                    .startChooser();
        }
    }

    private void onFavoriteButtonClick() {
        //update 'favorite' the Movie stored in view model
        //the live data will be responsible to trigger button update
        mViewModel.swapMovieFavorite();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //sync movie favorite status with DB only when onStop is called
        mViewModel.saveMovieChanges();

    }


    /**
     * Start implicit intent directing the user to the app (ex. youtube) to watch the trailer
     */
    @Override
    public void onTrailerClick(String videoPath) {

        Uri uri = MovieAPIUtility.buildVideoUri(videoPath);
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW, uri);
        if (openBrowserIntent.resolveActivity(getPackageManager()) != null){
            startActivity(openBrowserIntent);
            Log.d(TAG, "Redirecting to browser or youtube " + uri.toString());
        }
    }
}