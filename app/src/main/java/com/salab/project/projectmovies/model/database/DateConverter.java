package com.salab.project.projectmovies.model.database;

import androidx.room.TypeConverter;

import java.util.Date;
/**
 * Converter for Date object to numbers that SQLite can utilize. Currently this is  not required.
 */
public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTime(Date date){
        return date == null ? null : date.getTime();
    }

}
