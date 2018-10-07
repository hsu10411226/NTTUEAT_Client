package com.example.pohan_pc.nttueat;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by POHAN-PC on 2018/1/7.
 */

public class GenerateToken extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    public String refreshedToken;
    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
    }

    public void forcerefresh(){
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
    }
}
