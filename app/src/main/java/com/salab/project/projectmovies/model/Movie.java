package com.salab.project.projectmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


/**
 * Model class of movie and implements ROOM for saving data in SQLite, gson for auto parsing, and
 * Parcelable for the object can be carried by Intent to detail activity.
 */
@Entity(tableName = "movie")
public class Movie implements Parcelable {


    //API provided columns
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private String title;
    @SerializedName("original_title")
    private String originalTitle;
    private String overview;
    @SerializedName("release_date")
    private String releaseData;
    @SerializedName("original_language")
    private String originalLanguage;
    @PrimaryKey
    @SerializedName("id")
    private int movieId;
    @SerializedName("vote_count")
    private int voteCount;
//    @SerializedName("genre_ids")
//    private int [] genreIds;
    @SerializedName("vote_average")
    private float voteAverage;
    private float popularity;
    private boolean adult;
    private boolean video;

    //locally created columns
    @ColumnInfo(defaultValue = "0")
    private boolean favorite;
//    private Date favoriteDate;
//    private Date updatedAt;

    //no-args constructor for Room
    public Movie(){}

    protected Movie(Parcel in) {
        posterPath = in.readString();
        backdropPath = in.readString();
        title = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseData = in.readString();
        originalLanguage = in.readString();
        movieId = in.readInt();
        voteCount = in.readInt();
        voteAverage = in.readFloat();
        popularity = in.readFloat();
        adult = in.readByte() != 0;
        video = in.readByte() != 0;
        favorite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseData);
        dest.writeString(originalLanguage);
        dest.writeInt(movieId);
        dest.writeInt(voteCount);
        dest.writeFloat(voteAverage);
        dest.writeFloat(popularity);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseData() {
        return releaseData;
    }

    public void setReleaseData(String releaseData) {
        this.releaseData = releaseData;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

}
