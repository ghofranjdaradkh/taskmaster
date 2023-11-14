package com.example.taskmaster.Activites;


import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;

public class taskMasterApplication extends Application {
 public static final String TAG="TaskMasterApp" ;
    @Override
    public void onCreate() {

        super.onCreate();
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            Log.e(TAG, "Error initializing amplify" +e.getMessage());

        }
    }

}

