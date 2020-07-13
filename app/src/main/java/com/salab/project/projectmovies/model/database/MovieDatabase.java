package com.salab.project.projectmovies.model.database;


import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.salab.project.projectmovies.model.Movie;

/**
 * Local SQLite database, currently only preserves favorite Movie
 */

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String databaseName = "MovieDatabase";
    private static final Object LOCK = new Object();
    private static MovieDatabase mInstance; //singleton pattern

    public static MovieDatabase getMovieDatabaseInstance(Context context){

        if (mInstance == null){
            synchronized (LOCK) {
                mInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, databaseName).build();
                Log.d(MovieDatabase.class.getSimpleName(), "MovieDatabase instantiated");
            }
        }
        return mInstance;
    }

    public abstract MovieDao movieDao();
}
