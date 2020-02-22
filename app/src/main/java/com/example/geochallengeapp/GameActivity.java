package com.example.geochallengeapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    private Button[] button_ops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent i = getIntent();
        String playerName       = i.getStringExtra("playerName");
        Log.d(TAG,"got player name " + playerName);

        buildOpsButtons();
        for ( Button b : button_ops){
            b.setOnClickListener((v)-> System.out.println("Clicked " + b.getText()));
        }


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

    private void buildOpsButtons(){
        button_ops = new Button[4];
        button_ops[0] = findViewById(R.id.button_op1);
        button_ops[1] = findViewById(R.id.button_op2);
        button_ops[2] = findViewById(R.id.button_op3);
        button_ops[3] = findViewById(R.id.button_op4);
    }
}
