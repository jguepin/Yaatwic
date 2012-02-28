package com.chmod0.yaatwic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Activity that manages requesting user access token
 */
public class AccessTokenActivity extends Activity {

    private YaatwiConf yaatwiConf;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static AccessToken accessToken;

    private static TextView pinField;
    private static Button pinButton;
    private static Button launchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_token_launch);

        yaatwiConf = YaatwiConf.getInstance();
        twitter = yaatwiConf.getTwitter();

        try {
            requestToken = twitter.getOAuthRequestToken();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        launchButton = (Button) findViewById(R.id.launch_browser);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String requestTokenUrl = requestToken.getAuthorizationURL();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestTokenUrl));
                startActivity(browserIntent);
                changeActivityToReceive();

            }
        });


    }


    public void changeActivityToReceive() {

        setContentView(R.layout.request_token_receive);
        pinField = (TextView) findViewById(R.id.receive_token_field);
        pinButton = (Button) findViewById(R.id.receive_token);

        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = pinField.getText().toString();
                try {
                    if (!pin.isEmpty()) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.getOAuthAccessToken(requestToken);
                    }
                } catch (TwitterException e) {
                    if (e.getStatusCode() == 401)
                        Toast.makeText(getApplicationContext(), "Authentication credentials were missing or incorrect.",
                                Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                if (accessToken != null) {

                    Intent i = new Intent();
                    i.putExtra("accessToken", accessToken.getToken());
                    i.putExtra("accessTokenSecret", accessToken.getTokenSecret());
                    setResult(RESULT_OK, i);

                    finish();
                }

            }
        }

        );

    }

}
