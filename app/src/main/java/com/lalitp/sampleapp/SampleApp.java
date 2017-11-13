package com.lalitp.sampleapp;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.lalitp.sampleapp.Database.DatabaseHelper;

import java.io.File;

/**
 * Created by atulsia on 17/2/16.
 */
public class SampleApp extends MultiDexApplication {

    private static SampleApp sInstance;

    public static SampleApp getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
    public static DatabaseHelper databaseHelper = null;
    //private static GoogleAnalytics analytics;
    //private static Tracker tracker;

    @Override
    public void onCreate() {

        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        sInstance = this;

    }

    public static DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //databaseHelper = OpenHelperManager.getHelper(getAppContext(), DatabaseHelper.class);
            databaseHelper =  new DatabaseHelper(getAppContext());
        }
        return databaseHelper;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public static void getImage(ImageView imageview, String url, int drawble) {
        try {
            Glide.with(getAppContext())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(drawble)
                    .error(drawble)
                    .fallback(drawble)
                    .crossFade()
                    .dontAnimate()
                    .into(imageview);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
