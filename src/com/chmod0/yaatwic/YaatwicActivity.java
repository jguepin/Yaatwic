package com.chmod0.yaatwic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class YaatwicActivity extends Activity {
    private ArrayList<Status> tweets;
    private HashMap<Long, String> usersAvatars;
    private Twitter twitter;
    private YaatwiConf yaatwiConf;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Build interface */

        // Create the list of tweets
        tweets = new ArrayList<Status>();
        usersAvatars = new HashMap<Long, String>();

        ArrayAdapter<Status> tweetsAdapter = new TweetsAdapter(this, tweets, usersAvatars);

        // Get the tweets list view
        ListView tweetsView = (ListView) findViewById(R.id.tweets);
        tweetsView.setAdapter(tweetsAdapter);

        /* Load twitter data */

        // load accessToken from accessTokenFileName
        //AccessToken accessToken = loadAccessToken(); TODO uncomment for production
        AccessToken accessToken = new AccessToken("164509432-sZzSYgwbjBcmvhx1ihuhgAtdVjjqNIrLOPQX1QPw",
                "hiWd3Sdri3pwFWCJR4XQAAtvb2VPNoaxNl9HyClpVJE");
        if (accessToken != null)
            yaatwiConf = YaatwiConf.getInstance(accessToken);
        else
            yaatwiConf = YaatwiConf.getInstance();

        // init the twitter factory
        twitter = yaatwiConf.getTwitter();

        // request the token to the user if it's null
        if (yaatwiConf.getAccessToken() == null) {
            requestAccessToken();
        } else {
            loadSavedTweets();
            loadOnlineData();
        }
    }

    public void requestAccessToken() {
        Intent i = new Intent(this, AccessTokenActivity.class);
        startActivityForResult(i, 42);
    }

    public void saveAccessToken(String accessTokenStr, String accessTokenSecretStr) {
        SharedPreferences settings = getSharedPreferences(YaatwiConf.preferencesFileName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("accessToken", accessTokenStr);
        editor.putString("accessTokenSecret", accessTokenSecretStr);
    }

    public AccessToken loadAccessToken() {
        SharedPreferences settings = getSharedPreferences(YaatwiConf.preferencesFileName, 0);
        String token = settings.getString("accessToken", "empty");
        String tokenSecret = settings.getString("accessTokenSecret", "empty");
        if (token.equals("empty") || tokenSecret.equals("empty"))
            return null;
        else
            return new AccessToken(token, tokenSecret);
    }

    public void loadOnlineData() {

        if (yaatwiConf.isReady()) {
            try {
                // Load tweets from twitter
                Tools.addTweets(tweets, new ArrayList<Status>(twitter.getHomeTimeline()));

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveTweets() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(YaatwiConf.tweetsFileName, Context.MODE_PRIVATE));
            oos.writeObject(tweets);

            oos = new ObjectOutputStream(openFileOutput(YaatwiConf.avatarsFileName, Context.MODE_PRIVATE));
            oos.writeObject(usersAvatars);

            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the serialized list of tweets from internal memory
     */
    public void loadSavedTweets() {
        try {
            ObjectInputStream ois = new ObjectInputStream(openFileInput(YaatwiConf.tweetsFileName));
            Tools.addTweets(tweets, (ArrayList<Status>) ois.readObject());

            ois = new ObjectInputStream(openFileInput(YaatwiConf.avatarsFileName));
            usersAvatars = (HashMap<Long, String>) ois.readObject();

            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method waiting for results from AccessTokenActivity
     *
     * @param requestCode call request code
     * @param resultCode  result status code
     * @param data        data to extract
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the request went well (OK) and the request was to AccessTokenActivity
        if (resultCode == Activity.RESULT_OK && requestCode == 42) {
            // Extract and save the accessToke
            String accessTokenStr = data.getStringExtra("accessToken");
            String accessTokenSecretStr = data.getStringExtra("accessTokenSecret");

            if (accessTokenStr.isEmpty() || accessTokenSecretStr.isEmpty()) {
                requestAccessToken();

            } else {
                saveAccessToken(accessTokenStr, accessTokenSecretStr);
                yaatwiConf.setAccessToken(new AccessToken(accessTokenStr, accessTokenSecretStr));

                loadSavedTweets();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                loadOnlineData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTweets();
    }
}
