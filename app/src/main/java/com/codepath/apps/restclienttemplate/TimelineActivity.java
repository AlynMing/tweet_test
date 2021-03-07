package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDialog;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements TweetDialog.TweetDialogListener {

    public static final String TAG = "TimelineActivity";
    public static final int TRUNCATED = 0;
    public static final int FULL = 1;
    public static final int REQUEST_CODE = 20;
    TwitterClient client;
    FloatingActionButton fbCompose;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    List<Tweet> t1;
    TimelineActivity context;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        prefs = this.getSharedPreferences("com.codepath.apps.restclienttemplate", Context.MODE_PRIVATE);
         Log.i("NAME", getApplicationContext().getPackageName());

        client = TwitterApp.getRestClient(this);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data");
                populateHomeTimeline();
            }
        });


        rvTweets = findViewById(R.id.rvTweets);
        fbCompose = findViewById(R.id.fbCompose);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);
        context = this;

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "loading more");
                loadMoredata();
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        fbCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });

        populateHomeTimeline();
    }

    public void ShowDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        TweetDialog td = TweetDialog.newInstance("Compose", prefs.getString("tweetText", ""));
        td.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishTweetDialog(int resultCode, Tweet data, String text)
    {
        if(resultCode == TweetDialog.OK)
        {
            tweets.add(0, data);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.tweetDao.insertUser(data.user);
                    LoginActivity.tweetDao.insertTweet(data);
                }
            });
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        if(resultCode == TweetDialog.NO_INTERNET)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "no internet", Toast.LENGTH_SHORT).show();
                }
            });

        }

        if(resultCode == TweetDialog.CANCELLED)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save or dismiss draft?");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prefs.edit().putString("tweetText", text).apply();
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Dismissed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogInterface.cancel();
                }
            });
            builder.show();
        }

        Log.i("CODE", String.valueOf(resultCode));
    }



    private void loadMoredata() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess more" + tweets.get(tweets.size() - 1).id);
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    adapter.addAll(tweets);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFail more", throwable);
            }
        }, tweets.get(tweets.size() - 1).id);
    }



     @Override
     public boolean onOptionsItemSelected(MenuItem item)
     {
         if(item.getItemId() == R.id.compose)
         {
             //Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();
             Intent i = new Intent(this, ComposeActivity.class);
             startActivityForResult(i, REQUEST_CODE);
             return true;
         }
         return false;
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.tweetDao.insertUser(tweet.user);
                    LoginActivity.tweetDao.insertTweet(tweet);
                }
            });
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<Tweet> items = Tweet.fromJsonArray(jsonArray);
                                for(Tweet item : items)
                                {
                                    LoginActivity.tweetDao.insertUser(item.user);
                                    LoginActivity.tweetDao.insertTweet(item);
                                    Log.i("insert", "insert");

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        };
                    });
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "error parsing json", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                new LongOperation().execute();


                        //adapter.addAll(t);
                Log.i(TAG, "failure" + response, throwable);
            }
        });
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            adapter.clear();
            List<Tweet> t = LoginActivity.tweetDao.getTweets();
            tweets.addAll(t);
            Log.i("ASYNC", String.valueOf(t.size()));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("ASYNC", "NOTIFIED");
            adapter.notifyDataSetChanged();
        }
    }
}