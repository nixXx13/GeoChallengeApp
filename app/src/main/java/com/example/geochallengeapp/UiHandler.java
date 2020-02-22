package com.example.geochallengeapp;

import android.os.Handler;
import android.os.Looper;

public class UiHandler {

    private Handler mHandler;

    UiHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }


}

