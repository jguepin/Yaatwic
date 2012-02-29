package com.chmod0.yaatwic;

import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.URL;

public class Tools {

    public static Drawable loadDrawableFromImageUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "urlDrawable");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
