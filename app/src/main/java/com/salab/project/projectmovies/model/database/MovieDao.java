package com.salab.project.projectmovies.model.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.salab.project.projectmovies.model.Movie;

import java.util.List;

/**
 * DAO for Movie
 */
@Dao
public interface MovieDao {

    //currently only favorite movies will be conserved in database
    @Query("SELECT * FROM movie WHERE favorite = 1")
    List<Movie> queryFavoriteMovies();

    @Query("SELECT * FROM movie WHERE movieId = :id")
    Movie queryByMovieId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);


}
