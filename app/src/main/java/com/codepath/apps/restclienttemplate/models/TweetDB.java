package com.codepath.apps.restclienttemplate.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, Tweet.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class TweetDB extends RoomDatabase {
    public abstract TweetDao tweetDao();


    public  static  final String NAME = "TweetDataBase";
}
