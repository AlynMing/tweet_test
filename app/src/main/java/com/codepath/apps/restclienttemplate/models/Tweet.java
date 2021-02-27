package com.codepath.apps.restclienttemplate.models;

import android.annotation.SuppressLint;
import android.util.Log;

import com.codepath.apps.restclienttemplate.TimeFormatter;
import com.codepath.apps.restclienttemplate.TimelineActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Parcel
@Entity//(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId"))
public class Tweet {
    @ColumnInfo
    public String body;
    @ColumnInfo
    public String createdAt;
    @ColumnInfo
    public long tweetUserId;

    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String videoUrl;

    @Embedded
    public User user;

    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject, int requestType) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("full_text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.tweetUserId = tweet.user.userId;
        tweet.id = jsonObject.getLong("id");
        Log.i("DATA", jsonObject.toString());
        if(jsonObject.has("video_info")) {
            tweet.videoUrl = jsonObject
                    .getJSONObject("video_info")
                    .getJSONArray("variants")
                    .getJSONObject(0)
                    .getString("url");
        }
        return  tweet;
    }

    public String getTimestamp()
    {
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public  String getFullTime()
    {
        return  TimeFormatter.getTimeStamp(createdAt);
    }


    @SuppressLint("DefaultLocale")
    @NotNull
    @Override
    public String toString()
    {
        return  String.format("%s, %d", this.user.name, this.id);
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            tweets.add(fromJson(jsonArray.getJSONObject(i), TimelineActivity.TRUNCATED));
        }
        return  tweets;
    }
}
