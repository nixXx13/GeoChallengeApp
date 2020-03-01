package com.example.geochallengeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import Common.Converter;
import Common.GameData;
import Common.GameStage;
import GeoChallengeClient.GeoChallengeCoreFactory;
import GeoChallengeClient.IGeoChallengeCore;
import GeoChallengeClient.IResponseHandler;
import androidx.appcompat.app.AppCompatActivity;

import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.geochallengeapp.Constants.GAMEDATA_CONTENT_KEY;
import static com.example.geochallengeapp.Constants.SM_OTHERS_SCORE_TEMPLATE;
import static com.example.geochallengeapp.Constants.SM_SCORE_TEMPLATE;
import static com.example.geochallengeapp.Constants.SM_TOTAL_SCORE_TEMPLATE;

public class GameActivity extends AppCompatActivity {

    private final String SERVER_IP = "10.0.2.2";
    private final int PORT = 8888;

    private static final String TAG = "========== GameActivity";

    private IGeoChallengeCore geoChallengeCore;
    private List<GameStage> gameStageList;
    private int numberQuestions;

    private String SCORE_TEMPLATE = "Score: %.2f";
    private TextView tv_score;
    private float totalScore;
    private List<Float> questionsScores;

    private Button[] button_ops;
    private TextView tv_question;
    private TextView tv_update;
    private ProgressBar pb_logo;
    private ViewGroup transitionsContainer;

    private TextView tv_sm_title;
    private TextView tv_sm_scores;
    private TextView tv_sm_others;

    private UiHandler uiHandler;

    private boolean gameOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHandler = new UiHandler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();

        // TODO - delete
        String playerName = i.getStringExtra("playerName");
        Log.d(TAG,"got player name " + playerName);


        transitionsContainer = findViewById(R.id.activity_battle);
        transitionsContainer.setOnClickListener((v)-> {
            if(questionsScores.size()>0 && !gameOn){
                finish();
            }
        });
        pb_logo = transitionsContainer.findViewById(R.id.pbHeaderProgress);
        pb_logo.setVisibility(View.VISIBLE);

        buildOpsButtons();
        for ( Button b : button_ops){
            b.setOnClickListener((v)-> new AsyncTaskSend().execute(b.getText().toString()));
        }
        numberQuestions = -1;
        tv_question = transitionsContainer.findViewById(R.id.tv_question);
        tv_update = transitionsContainer.findViewById(R.id.tv_updates);

        tv_score = transitionsContainer.findViewById(R.id.tv_score);
        questionsScores = new ArrayList();
        totalScore = 0;

        transitionsContainer = findViewById(R.id.activity_battle);
        tv_sm_title = transitionsContainer.findViewById(R.id.tv_summary_title);
        tv_sm_scores = transitionsContainer.findViewById(R.id.tv_summary_answer);
        tv_sm_others = transitionsContainer.findViewById(R.id.tv_summary_others);

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
        button_ops[0] = transitionsContainer.findViewById(R.id.button_op1);
        button_ops[1] = transitionsContainer.findViewById(R.id.button_op2);
        button_ops[2] = transitionsContainer.findViewById(R.id.button_op3);
        button_ops[3] = transitionsContainer.findViewById(R.id.button_op4);
    }

    private class AsyncTaskSend extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG,String.format("Button clicked "));
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
        if ( !gameStageList.isEmpty() ){
            uiHandler.updateTextView(tv_question,gameStageList.get(0).getQuestion());
            int i = 0;
            for(Button b:button_ops){
                String newText = gameStageList.get(0).getPossibleAnswers().get(i++);
                uiHandler.updateButtonText(b,newText);
            }
            gameStageList.remove(0);
            // todo - fire count down
        }
    }

    private class ResponseHandler implements IResponseHandler{

        @Override
        public void handle(GameData gameData) {
            GameData.GameDataType gameDataType = gameData.getType();
            Map<String,String> data = gameData.getContent();

            Log.d(TAG,String.format("Handling event type %s",gameDataType.toString()));
            switch (gameDataType){
                case ERROR:
                    handleError(data);
                    break;
                case ACK:
                    handleAck(data);
                    break;
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

        private void handleAck(Map<String,String> data) {
            uiHandler.setViewVisability(pb_logo, View.INVISIBLE);
            uiHandler.updateTextView(tv_question,"Waiting for other players...");
        }

        private void handleError(Map<String,String> data) {
            uiHandler.makeToast(getApplicationContext(), data.get(GAMEDATA_CONTENT_KEY));
            finish();
        }


        private void handleData(Map<String,String> data){
            gameOn = true;
            gameStageList = Converter.toGameStageList(data);
            numberQuestions = gameStageList.size();
            loadNextQuestion();
            Log.d(TAG,data.toString());
        }

        private void handleEnd(Map<String,String> data){
            gameOn = false;
            uiHandler.setViewVisability(pb_logo, View.INVISIBLE);
            TransitionManager.beginDelayedTransition(transitionsContainer);
            uiHandler.updateTextView(tv_sm_others,String.format(SM_OTHERS_SCORE_TEMPLATE,data.get(GAMEDATA_CONTENT_KEY)));
        }
        private void handleUpdate(Map<String,String> data){
            String updateStr = data.get(GAMEDATA_CONTENT_KEY);
            uiHandler.updateTextView(tv_update,updateStr);
            Log.d(TAG,data.toString());
        }

        private void handleGrade(Map<String,String> data){
            Float currentScore = Float.valueOf(data.get(GAMEDATA_CONTENT_KEY));
            questionsScores.add(currentScore);
            totalScore += currentScore;
            String scoreUpdate = String.format(SCORE_TEMPLATE,currentScore);
            uiHandler.updateTextView(tv_score,scoreUpdate);

            // received final grade
            if (questionsScores.size() == numberQuestions){
                Float max = Collections.max(questionsScores);
                Float min = Collections.min(questionsScores);
                uiHandler.updateTextView(tv_sm_title,String.format(SM_TOTAL_SCORE_TEMPLATE,totalScore));
                uiHandler.updateTextView(tv_sm_scores,String.format(SM_SCORE_TEMPLATE,max,min));
                uiHandler.updateTextView(tv_sm_others,"Waiting for other players to finish...");
                TransitionManager.beginDelayedTransition(transitionsContainer);

                uiHandler.setViewVisability(tv_sm_title, View.VISIBLE);
                uiHandler.setViewVisability(tv_sm_scores, View.VISIBLE);
                uiHandler.setViewVisability(tv_sm_others, View.VISIBLE);
                uiHandler.setViewVisability(pb_logo, View.VISIBLE);

                uiHandler.setViewVisability(tv_score, View.INVISIBLE);
                uiHandler.setViewVisability(tv_update, View.INVISIBLE);
                uiHandler.setViewVisability(tv_question, View.INVISIBLE);
                uiHandler.setViewVisability(button_ops[0], View.INVISIBLE);
                uiHandler.setViewVisability(button_ops[1], View.INVISIBLE);
                uiHandler.setViewVisability(button_ops[2], View.INVISIBLE);
                uiHandler.setViewVisability(button_ops[3], View.INVISIBLE);
                // present user score/stats
                // TODO - wait for other players to finish
            }
        }
    }
}
