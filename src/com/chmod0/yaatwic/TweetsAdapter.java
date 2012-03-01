package com.chmod0.yaatwic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import twitter4j.Status;
import twitter4j.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class TweetsAdapter extends ArrayAdapter<Status> {

    private LayoutInflater inflater;
    private String cachePath;
    private ArrayList<Status> tweets;
    private HashMap<Long, String> usersAvatars;
    private int densityDpi;

    public TweetsAdapter(Context context, ArrayList<Status> tweets, HashMap<Long, String> usersAvatars) {
        super(context, R.layout.tweet, tweets);

        // cache the layoutInflater to avoid asking a new one each time
        inflater = LayoutInflater.from(context);

        this.tweets = tweets;
        this.usersAvatars = usersAvatars;
        this.cachePath = context.getCacheDir().getPath();
        this.densityDpi = context.getResources().getDisplayMetrics().densityDpi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /* OPTIMIZATIONS */

        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tweet, null);

            // Creates a ViewHolder and store references to the children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.messageView = (TextView) convertView.findViewById(R.id.message);
            holder.dateView = (TextView) convertView.findViewById(R.id.date);
            holder.authorView = (TextView) convertView.findViewById(R.id.author);
            holder.avatarView = (ImageView) convertView.findViewById(R.id.avatar);

            convertView.setTag(holder);

        } else {
            // Get the ViewHolder back to get fast access to all children views
            holder = (ViewHolder) convertView.getTag();
        }

        /* ADD DATA TO VIEWS */

        if (tweets.get(position) != null) {
            Status status = tweets.get(position);

            holder.authorView.setText(status.getUser().getName());
            holder.messageView.setText(status.getText());
            holder.dateView.setText(status.getCreatedAt().toLocaleString());

            // Get stored user profile image or download it
            Drawable profileImage;
            Long userId = status.getUser().getId();
            if (usersAvatars.containsKey(userId)) {
                if (new File(usersAvatars.get(userId)).exists())
                    profileImage = Drawable.createFromPath(usersAvatars.get(userId));
                else
                    profileImage = downloadNStoreAvatar(status.getUser());
            } else {
                profileImage = downloadNStoreAvatar(status.getUser());
            }

            holder.avatarView.setImageDrawable(profileImage);
        }

        /* Add swipe listener to list */
        
        final GestureDetector gestureDetector = new GestureDetector(new SwipeDetector(densityDpi));
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                boolean hasSwiped = gestureDetector.onTouchEvent(event);
                // handle swipe event
                if(hasSwiped){
                    TextView content = (TextView)v.findViewById(R.id.message);
                    Log.d("Yaa", "swiped " + content.getText());
                }

                return hasSwiped;
            }
        };
        convertView.setOnTouchListener(gestureListener);
        
        return convertView;
    }

    public Drawable downloadNStoreAvatar(User user) {
        Drawable profileAvatar;
        String filePath = cachePath + "/" + user.getId();

        Tools.downloadImageFromUrl(user.getProfileImageURL().toString(), filePath);
        profileAvatar = Drawable.createFromPath(filePath);

        usersAvatars.put(user.getId(), filePath);

        return profileAvatar;
    }

    static class ViewHolder {
        TextView messageView;
        TextView dateView;
        TextView authorView;
        ImageView avatarView;
    }
}

