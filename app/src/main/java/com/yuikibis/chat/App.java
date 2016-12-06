package com.yuikibis.chat;

import android.app.Application;

import com.deploygate.sdk.DeployGate;
import com.firebase.client.Firebase;

/**
 * Created on 15/06/08.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        DeployGate.install(this);
    }
}
