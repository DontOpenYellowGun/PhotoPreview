package com.sven.photospreview;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Sven on 2017/1/3.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        Fresco.initialize(this);
    }
}
