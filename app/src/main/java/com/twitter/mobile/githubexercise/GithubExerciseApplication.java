package com.twitter.mobile.githubexercise;

import android.app.Application;
import android.content.Context;

/**
 * Created by SJ on 1/30/16.
 */
public class GithubExerciseApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getAppContext(){
        return sContext;
    }
}
