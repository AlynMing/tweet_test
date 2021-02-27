package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT * FROM Tweet where id == :id")
    public Tweet getByTweetId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertTweet(Tweet tweet);

    //@Query("SELECT * FROM Tweet ORDER BY ID DESC LIMIT 50")
    //List<SampleModel> recentTweets();

    @Query("SELECT * FROM User INNER JOIN Tweet ON User.userId == Tweet.userId")
    public List<Tweet> getTweets();

    @Delete
    public void deleteTweet(Tweet tweet);

    @Query("SELECT * FROM User where userId == :id")
    public  User getByUserId(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertUser(User user);

    @Delete
    public void deleteUser(User user);

}