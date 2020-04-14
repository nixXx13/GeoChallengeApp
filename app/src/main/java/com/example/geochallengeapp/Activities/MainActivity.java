package com.example.geochallengeapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.geochallengeapp.R;

// TODO - log4j in jar isnt set up correctly ( log4j properties file )
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup transitionsContainer = findViewById(R.id.activity_main);
        transitionsContainer.setOnClickListener((v)-> {
            FireIntent(SetupActivity.class);
        });

        TextView tv_play = transitionsContainer.findViewById(R.id.tv_play);
        TransitionManager.beginDelayedTransition(transitionsContainer);
        tv_play.setTextSize(50);
        tv_play.setVisibility(View.VISIBLE);


    }

    private void FireIntent(Class cls) {
        Log.d(TAG, "Fired cls" + cls.toString());
        Intent i = new Intent(this, cls);
        startActivity(i);
    }

}
