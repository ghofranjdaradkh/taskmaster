package com.example.taskmaster.Activites;


import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.geo.location.AWSLocationGeoPlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

public class taskMasterApplication extends Application {
 public static final String TAG="TaskMasterApp" ;
    @Override
    public void onCreate() {

        super.onCreate();
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.addPlugin(new AWSLocationGeoPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            Log.e(TAG, "Error initializing amplify" +e.getMessage());

        }
    }

}

