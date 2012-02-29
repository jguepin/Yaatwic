package com.chmod0.yaatwic;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TweetsAdapter extends ArrayAdapter<Status> {

    private Activity activity;
    private ArrayList<Status> tweets;
    private HashMap<Long, Drawable> usersProfileImages;

    public TweetsAdapter(Activity activity, ArrayList<Status> tweets, HashMap<Long, Drawable> usersProfileImages) {
        super(activity, R.layout.tweet, tweets);
        this.activity = activity;
        this.tweets = tweets;
        this.usersProfileImages = usersProfileImages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View tweetView = inflater.inflate(R.layout.tweet, null);

        TextView messageView = (TextView) tweetView.findViewById(R.id.message);

        TextView dateView = (TextView) tweetView.findViewById(R.id.date);
        TextView authorView = (TextView) tweetView.findViewById(R.id.author);
        ImageView avatarView = (ImageView) tweetView.findViewById(R.id.avatar);

        if (tweets.get(position) != null) {
            Status status = tweets.get(position);

            authorView.setText(status.getUser().getName());
            messageView.setText(status.getText());
            dateView.setText(status.getCreatedAt().toLocaleString());

            // Get stored user profile image or download it
            Drawable profileImage;
            Long userId = status.getUser().getId();
            if(usersProfileImages.containsKey(userId)) {
                profileImage = usersProfileImages.get(userId);
            } else {
                profileImage = Tools.loadDrawableFromImageUrl(status.getUser().getProfileImageURL().toString());
                usersProfileImages.put(userId, profileImage);
            }
            avatarView.setImageDrawable(profileImage);
        }

        return tweetView;
    }
}
