package com.codepath.apps.restclienttemplate.models;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import javax.annotation.Nullable;

import okhttp3.Headers;

public class TweetDialog extends DialogFragment {

    public static  final int MAX_TWEET_LENGTH = 140;
    public static final String TAG = "COMPOSE ACTIVITY";

    EditText etCompose;
    Button btnTweet;
    TextView tvCharcount;
    FragmentActivity context;
    long replyId;

    TwitterClient client;

    public  TweetDialog()
    {
    }

    public interface TweetDialogListener {
        void onFinishTweetDialog(int resultData, Tweet data);
    }

    public static  TweetDialog newInstance(String title)
    {
        TweetDialog td = new TweetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        td.setArguments(args);
        return  td;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle saved)
    {
        return  inflater.inflate(R.layout.activity_compose, containter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle saved)
    {
        super.onViewCreated(view, saved);
        String title = getArguments().getString("title");
        getDialog().setTitle(title);
        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);
        client = TwitterApp.getRestClient(getContext());
        tvCharcount = view.findViewById(R.id.tvCharcount);
        tvCharcount.setText(String.format(this.getString(R.string.char_count), 0));
        context = getActivity();
        replyId = context.getIntent().getLongExtra("reply_id", -1);
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                tvCharcount.setText(String.format(context.getString(R.string.char_count), etCompose.length()));
                btnTweet.setEnabled(i <= MAX_TWEET_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()) {
                    Toast.makeText(context, "Cannot publish empty tweet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH)
                {
                    Toast.makeText(context, "Tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();
                client.publishTweet(tweetContent, replyId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccessPublish");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, tweet.toString());
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            TweetDialogListener listener = (TweetDialogListener)getActivity();
                            listener.onFinishTweetDialog(-1, tweet);
                            dismiss();
                            //setResult(RESULT_OK, intent);
                            //finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        TweetDialogListener listener = (TweetDialogListener)getActivity();
                        listener.onFinishTweetDialog(1, null);
                        dismiss();
                        Log.e(TAG, "onFailurePublish", throwable);
                    }
                });
            }
        });
    }
}
