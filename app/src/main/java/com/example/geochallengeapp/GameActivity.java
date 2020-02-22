package com.example.geochallengeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import Common.GameData;
import Common.GameStage;
import GeoChallengeClient.GeoChallengeCoreFactory;
import GeoChallengeClient.IGeoChallengeCore;
import GeoChallengeClient.IResponseHandler;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "========== GameActivity";

    private IGeoChallengeCore geoChallengeCore;
    private List<GameStage> gameStageList;
    private Button[] button_ops;

    private boolean gameOn;

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
        String playerName = i.getStringExtra("playerName");
        Log.d(TAG,"got player name " + playerName);

        buildOpsButtons();
        for ( Button b : button_ops){
            b.setOnClickListener((v)-> new AsyncTaskSend().execute(b.getText().toString()));
        }

        String SERVER_IP = "10.0.2.2";
        int PORT = 8888;


        geoChallengeCore = GeoChallengeCoreFactory.getGeoChallengeCore(SERVER_IP,PORT);
        geoChallengeCore.registerHandler(new ResponseHandler());
        Log.i(TAG,"Starting geo challenge core");
        Thread t = new Thread(geoChallengeCore);
        t.start();


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

    private class AsyncTaskSend extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            if(gameOn){
                String answer = strings[0];
                Log.d(TAG,"sending " + answer);
                geoChallengeCore.send(answer);
                loadNextQuestion();
            }
            return null;
        }
    }

    private void loadNextQuestion(){
        // TODO - change to game stages
        for(Button b:button_ops){
            String newText = b.getText()+"1";
            b.setText(newText);
        }
    }

    private class ResponseHandler implements IResponseHandler{

        @Override
        public void handle(GameData gameData) {
            GameData.GameDataType gameDataType = gameData.getType();
            Map<String,String> data = gameData.getContent();

            Log.d(TAG,String.format("Handling event type %s",gameDataType.toString()));
            switch (gameDataType){
                case DATA:
                    handleData(data);
                    break;
                case UPDATE:
                    handleUpdate(data);
                    break;
                case GRADE:
                    handleGrade(data);
                    break;
                case END:
                    handleEnd(data);
                    break;
            }
        }

        private void handleData(Map<String,String> data){
            gameOn = true;
            loadNextQuestion();
            Log.d(TAG,data.toString());
        }

        private void handleEnd(Map<String,String> data){
            gameOn = false;
            // TODO - fire summary intent
            Log.d(TAG,data.toString());
            finish();
        }
        private void handleUpdate(Map<String,String> data){
            // TODO - display new update
            Log.d(TAG,data.toString());
        }

        private void handleGrade(Map<String,String> data){
            // TODO - update player score
            Log.d(TAG,data.toString());
        }
    }
}
