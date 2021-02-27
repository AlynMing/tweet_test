package com.codepath.apps.restclienttemplate.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Tweet.class}, version = 4)
public abstract class TweetDB extends RoomDatabase {
    public abstract TweetDao tweetDao();

    public  static  final String NAME = "TweetDataBase";
}
