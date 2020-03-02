package com.example.geochallengeapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UiHandler {

    private Handler mHandler;

    public UiHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void updateTextView(final TextView tv, final String s) {
        mHandler.post(() -> tv.setText(s));
    }

    public void updateButtonText(final Button b, final String s) {
        mHandler.post(() -> b.setText(s));
    }

    public void setViewVisability(final View view, final int visability) {
        mHandler.post(() -> view.setVisibility(visability));
    }

    public void makeToast(final Context context, final String msg) {
        mHandler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }

    public void finish(Activity activity){
        activity.finish();
    }
}
