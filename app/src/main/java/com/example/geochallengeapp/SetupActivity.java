package com.example.geochallengeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import java.util.concurrent.ThreadLocalRandom;

public class SetupActivity extends AppCompatActivity {

    private Button bPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bPlay = findViewById(R.id.button_play);
        bPlay.setOnClickListener(new PlayListener(this));
    }


    private  class PlayListener implements View.OnClickListener{

        private Context contex;

        PlayListener(Context contex){
            this.contex = contex;
        }

        @Override
        public void onClick(View view) {

            Intent i = new Intent(contex , GameActivity.class);
            i.putExtra("playerName"     ,"Ploni_" + ThreadLocalRandom.current().nextInt(0, 1000));
            startActivity(i);

        }
    }

}
