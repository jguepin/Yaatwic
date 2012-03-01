package com.chmod0.yaatwic;

import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;
import twitter4j.Status;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Tools {

   public static void addTweets(ArrayList<Status> currentTL, ArrayList<Status> updatedTL) {
        updatedTL.removeAll(currentTL);
        currentTL.addAll(0, updatedTL);
    }

    public static void downloadImageFromUrl(String imageURL, String filePath) {
        try {
            URL url = new URL(imageURL);

            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            /*
            * Define InputStreams to read from the URLConnection.
            */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            /*
            * Read bytes to the Buffer until there is nothing more to read(-1).
            */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            fos.write(baf.toByteArray());
            fos.close();

        } catch (IOException e) {
            Log.d("ImageManager", "Error: " + e);
        }

    }
}
