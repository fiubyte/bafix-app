package com.fiubyte.bafix.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DrawableFromUrlLoader {
    public static void loadDrawableFromUrl(String url, final DrawableCallback callback) {
        new AsyncTask<String, Void, Drawable>() {
            @Override
            protected Drawable doInBackground(String... urls) {
                try {
                    Bitmap x;

                    HttpURLConnection connection =
                            (HttpURLConnection) new URL(urls[0]).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();

                    x = BitmapFactory.decodeStream(input);
                    return new BitmapDrawable(Resources.getSystem(), x);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Drawable result) {
                if (callback != null) {
                    callback.onDrawableLoaded(result);
                }
            }
        }.execute(url);
    }

    public interface DrawableCallback {
        void onDrawableLoaded(Drawable drawable);
    }
}
