package com.example.geochallengeapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.geochallengeapp.R;

// TODO - log4j in jar isnt set up correctly ( log4j properties file )
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

    private ViewGroup transitionsContainer;
    private TextView tv_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transitionsContainer = findViewById(R.id.activity_main);
        transitionsContainer.setOnClickListener((v)-> {
            FireIntent(SetupActivity.class);
        });

        tv_play = transitionsContainer.findViewById(R.id.tv_play);
        TransitionManager.beginDelayedTransition(transitionsContainer);
        tv_play.setTextSize(50);
        tv_play.setVisibility(View.VISIBLE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void FireIntent(Class cls) {
        Log.d(TAG, "Fired cls" + cls.toString());
        Intent i = new Intent(this, cls);
        startActivity(i);
    }

}
