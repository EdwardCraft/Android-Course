package com.example.android.sunchine.app.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by PEDRO on 01/09/2016.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = "MyInstanceIDLS";


    /*
    *   Called if InstanceID token us updated. This may occur if the security of
    *   the previous token had been  compromised, This Call is initiated by the
    *   InstanceID provider.
    *
    * */


    @Override
    public void onTokenRefresh(){
        // Fetch updated Instance ID token.
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }





}
