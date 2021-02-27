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

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        tvName.setText(tweet.user.name);
        tvScreenName.setText(tweet.user.screenName);
        tvDate.setText(tweet.getFullTime());
        tvBody.setText(tweet.body);

        client = TwitterApp.getRestClient(this);
        client.getTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    tvBody.setText(json.jsonObject.getString("full_text"));
                    ;
                    String url =json
                            .jsonObject
                            .getJSONObject("video_info")
                            .getJSONArray("variants")
                            .getJSONObject(0)
                            .getString("url");
                    Log.i("123","123");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("Detailed", "Fail");
            }
        }, tweet.id);

    }
}