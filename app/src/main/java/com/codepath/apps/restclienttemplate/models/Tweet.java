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
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "tweetUserId"))
public class Tweet {
    @ColumnInfo
    public String body;
    @ColumnInfo
    public String createdAt;
    @ColumnInfo
    public long tweetUserId;

    //@Embedded
    @ColumnInfo
    public List<String> images;

    @ColumnInfo
    public List<String> videoUrl;

    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    public long id;

    @Embedded
    public User user;

    @ColumnInfo
    public boolean favourited;

    @ColumnInfo
    public boolean retweeted;

    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject, int requestType) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.images = new ArrayList<>();
        tweet.videoUrl = new ArrayList<>();
        tweet.body = jsonObject.getString("full_text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.tweetUserId = tweet.user.userId;
        tweet.id = jsonObject.getLong("id");
        tweet.favourited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        if(jsonObject.getJSONObject("entities").has("media"))
        {
            JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
            //Log.i("MEDIA", media.toString());
            for(int i = 0; i < media.length(); i++)
            {
                tweet.images.add(media.getJSONObject(i).getString("media_url"));
            }
        }
        if(jsonObject.has("retweeted_status") && jsonObject.getJSONObject("retweeted_status").getJSONObject("entities").has("media"))
        {
            JSONArray media = jsonObject.getJSONObject("retweeted_status").getJSONObject("entities").getJSONArray("media");
            for(int i = 0; i < media.length(); i++)
            {
                //Log.i("RTMEDIA", media.toString());
                tweet.images.add(media.getJSONObject(i).getString("media_url_https"));
            }
        }
        if(jsonObject.has("extended_entities"))
        {

            JSONArray media = jsonObject.getJSONObject("extended_entities").getJSONArray("media");
            //Log.i("VIDEO", media.getJSONObject(0).getString("type"));
            for(int i = 0; i < media.length(); i++)
            {
                JSONObject o = media.getJSONObject(i);
                if(o.getString("type").equals("video"))
                {
                    tweet.videoUrl.add(o.getJSONObject("video_info").getJSONArray("variants").getJSONObject(0).getString("url"));
                    //Log.i("VIDEO", tweet.videoUrl.get(tweet.videoUrl.size() - 1));
                }
                else if(o.getString("type").equals("photo"))
                {
                    tweet.images.add(o.getString("media_url_https"));
                }
            }
        }
        //Log.i("DATA", jsonObject.toString());

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
