package com.moutamid.gitweb;

import android.app.Application;

import com.downloader.PRDownloader;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PRDownloader.initialize(getApplicationContext());
    }
}
