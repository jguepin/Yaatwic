package com.chmod0.yaatwic;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class YaatwiConf {

    private static YaatwiConf singleton;

    private static String consumerKey = "rDDHFT3xb8xPr4Wvl3b4g";
    private static String consumerSecret = "U0kkrigwSWsODmmID3Y5m9HYcGevnfgKZt0b0ZY5GdE";
    private AccessToken accessToken;
    private Twitter twitter;
    public final static String preferencesFileName = "yaatwic.properties";
    public final static String tweetsFileName = "tweets";

    private YaatwiConf() {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret);

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    private YaatwiConf(AccessToken accessToken) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken.getToken())
                .setOAuthAccessTokenSecret(accessToken.getTokenSecret());

        this.accessToken = accessToken;

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public static YaatwiConf getInstance() {
        if (singleton == null)
            singleton = new YaatwiConf();

        return singleton;
    }

    public static YaatwiConf getInstance(AccessToken accessToken) {
        if (singleton == null)
            singleton = new YaatwiConf(accessToken);

        return singleton;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
        twitter.setOAuthAccessToken(accessToken);
    }

    public boolean isReady() {
        return accessToken != null;
    }
}
