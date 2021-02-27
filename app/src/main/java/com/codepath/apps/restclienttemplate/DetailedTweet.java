package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailedTweet extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvName;
    TwitterClient client;
    TextView tvDate;
    String fullText;
    ImageView ivImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_tweet);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvName = findViewById(R.id.tvName);
        ivProfileImage = findViewById(R.id.ivProfile);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvDate = findViewById(R.id.tvDate);
        tvBody = findViewById(R.id.tvBody);
        ivImg = findViewById(R.id.ivImg);

        if(tweet.images.size() > 0)
            Glide.with(this).load(tweet.images.get(0)).into(ivImg);
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        tvName.setText(tweet.user.name);
        tvScreenName.setText(tweet.user.screenName);
        tvDate.setText(tweet.getFullTime());
        tvBody.setText(tweet.body);



    }
}