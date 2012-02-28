package com.chmod0.yaatwic;

import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import twitter4j.Status;

import java.util.ArrayList;

public class TweetsAdapter extends ArrayAdapter<Status> {

    private Activity activity;
    private ArrayList<Status> items;

    public TweetsAdapter(Activity activity, ArrayList<Status> tweets) {
        super(activity, R.layout.tweet, tweets);
        this.activity = activity;
        this.items = tweets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View tweetView = inflater.inflate(R.layout.tweet, null);

        TextView message = (TextView) tweetView.findViewById(R.id.message);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        TextView date = (TextView) tweetView.findViewById(R.id.date);
        TextView author = (TextView) tweetView.findViewById(R.id.author);
        ImageView avatar = (ImageView) tweetView.findViewById(R.id.avatar);

        if (items.get(position) != null) {
            Status status = items.get(position);

            author.setText(status.getUser().getName());
            message.setText(Html.fromHtml(status.getText()));
            date.setText(status.getCreatedAt().toLocaleString());

            /* TODO fix images uri encoding
            // encode profile image url to uri...
            String encodedProfileImageURL = Uri.encode(status.getUser().getProfileImageURL().toString());
            avatar.setImageURI(Uri.parse(encodedProfileImageURL));
            */
        }

        return tweetView;
    }
}
