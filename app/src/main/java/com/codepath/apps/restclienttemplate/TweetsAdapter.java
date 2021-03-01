package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.bindHolder>{
    private void fixTextView(TextView tv) {
        SpannableString current = (SpannableString) tv.getText();
        URLSpan[] spans =
                current.getSpans(0, current.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = current.getSpanStart(span);
            int end = current.getSpanEnd(span);

            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(span.getURL()), start, end,
                    0);
        }
    }
    public static class DefensiveURLSpan extends URLSpan {
        private String mUrl;

        public DefensiveURLSpan(String url) {
            super(url);
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            // openInWebView(widget.getContext(), mUrl); // intercept click event and do something.
            // super.onClick(widget); // or it will do as it is.
        }
    }
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public bindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
            return new ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.video_tweet, parent, false);
            return  new VideoHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull bindHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet); 
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear()
    {
        tweets.clear();
    }

    public void addAll(List<Tweet> tweetList)
    {
        tweets.addAll(tweetList);
        //notifyDataSetChanged();
    }



    public class ViewHolder extends bindHolder {

        ImageView ivProfileImage;
        LinkifyTextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvDate;
        ImageView ivRetweet;
        ImageView ivLIke;
        ImageView ivReply;

        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            container = itemView.findViewById(R.id.rvTweet);
            ivRetweet = itemView.findViewById(R.id.retweet);
            ivLIke = itemView.findViewById(R.id.like);
            ivReply = itemView.findViewById(R.id.comment);
        }


        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(context.getString(R.string.screen_name, tweet.user.name));
            tvName.setText(tweet.user.name);
            tvDate.setText(tweet.getTimestamp());
            ivReply.setImageResource(R.drawable.vector_compose_dm_fab);
            if(tweet.retweeted)
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            else
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            if(tweet.favourited)
                ivLIke.setImageResource(R.drawable.ic_vector_heart);
            else
                ivLIke.setImageResource(R.drawable.ic_vector_heart_stroke);


            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailedTweet.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    @Override
    public int getItemViewType(int position) {
        //Log.i("FIND", String.format("%b, %s", tweets.get(position).videoUrl.isEmpty(), tweets.get(position).body));
        //Log.i("FIND", String.valueOf(tweets.get(position).videoUrl.get(0)));

        if(tweets.get(position).videoUrl.size() > 0 && !tweets.get(position).videoUrl.get(0).equals("") && isNetworkConnected())
        {
            return  1;
        }
        else
            return 0;
    }

    public class VideoHolder extends bindHolder {
        ImageView ivProfileImage;
        LinkifyTextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvDate;
        VideoView player;


        RelativeLayout container;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            container = itemView.findViewById(R.id.rvVideo);
            player = itemView.findViewById(R.id.player);
        }


        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(context.getString(R.string.screen_name, tweet.user.name));
            tvName.setText(tweet.user.name);
            tvDate.setText(tweet.getTimestamp());
            player.setVideoURI(Uri.parse(tweet.videoUrl.get(0)));
            player.start();


            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailedTweet.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });
        }
    }

    public abstract class bindHolder extends RecyclerView.ViewHolder
    {

        public bindHolder(@NonNull View itemView) {
            super(itemView);
        }

        public  abstract void bind(Tweet tweet);
    }

}
