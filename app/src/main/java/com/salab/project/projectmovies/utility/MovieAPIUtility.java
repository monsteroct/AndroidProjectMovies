package com.salab.project.projectmovies.utility;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The class handles tasks related to fetching data from tmdb server including constants, uri/url
 * building, create basic Retrofit connection services.
 */
public class MovieAPIUtility {

    private static final String TAG = MovieAPIUtility.class.getSimpleName();

    private static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_IMAGE_BASE_URL = "https://image.tmdb.org/t/p";
    private static final String YT_VIDEO_BASE_URL = "https://www.youtube.com/watch";
    private static final String YT_VIDEO_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";

    public static final String SORT_BY_POPULARITY = "popular";
    public static final String SORT_BY_RATING = "top_rated";
    public static final String IMAGE_SIZE_185 = "w185";
    public static final String IMAGE_SIZE_342 = "w342";
    public static final String IMAGE_SIZE_500 = "w500";

    public static final String KEY_VIDEO_QUERY = "v";
    public static final String THUMBNAIL_QUALITY = "sddefault.jpg";
    public static final String VIDEO_SITE_YT = "YouTube";


    //use the same Retrofit instance for network connection to save memory
    private static Retrofit retrofitInstance;

    public static URL buildPosterURL(String posterPath, String imgSize){
        //replace extra "/" at begin of posterPath
        posterPath = posterPath.replace("/","");
        Uri uriBuilt = Uri.parse(API_IMAGE_BASE_URL).buildUpon()
                .appendPath(imgSize)
                .appendPath(posterPath)
                .build();

        Log.d(TAG,"Image URL is built : " + uriBuilt.toString());

        return buildURLFromUri(uriBuilt);
    }

    public static Uri buildVideoUri(String videoPath){

        Uri uriBuilt = Uri.parse(YT_VIDEO_BASE_URL).buildUpon()
                .appendQueryParameter(KEY_VIDEO_QUERY, videoPath)
                .build();

        Log.d(TAG,"Video URL is built : " + uriBuilt.toString());

        return uriBuilt;
    }
    //youtube thumbnail https://img.youtube.com/vi/--lwtzRPAT0/sddefault.jpg
    public static URL buildVideoThumbnailUri(String videoId){

        Uri uriBuilt = Uri.parse(YT_VIDEO_THUMBNAIL_BASE_URL).buildUpon()
                .appendPath(videoId)
                .appendPath(THUMBNAIL_QUALITY)
                .build();

        Log.d(TAG,"Video Thumbnail URL is built : " + uriBuilt.toString());

        return buildURLFromUri(uriBuilt);
    }

    /**
     * helper method turn uri to URL
     */
    private static URL buildURLFromUri(Uri builtUri){
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;

    }

    private static Retrofit getRetrofit(){

        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Log.d(TAG,"A instance of Retrofit is created");
        }

        return retrofitInstance;
    }

    /**
     * Repository will call this method to return a service for further API fetching tasks
     */
    public static MovieAPIInterface buildService(){

        return getRetrofit().create(MovieAPIInterface.class);
    }


}


