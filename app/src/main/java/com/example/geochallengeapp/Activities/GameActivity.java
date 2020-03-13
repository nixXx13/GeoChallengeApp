package com.example.geochallengeapp.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import Common.GameData;
import Common.GameStage;
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

import com.example.geochallengeapp.GameManager;
import com.example.geochallengeapp.R;
import com.example.geochallengeapp.ResponseHandler;
import com.example.geochallengeapp.UiHandler;

import java.util.Map;

import static com.example.geochallengeapp.Constants.*;

public class GameActivity extends AppCompatActivity {

    private final String SERVER_IP = "10.0.2.2";
//    private final String SERVER_IP = "127.0.0.1";
    private final int PORT = 8888;

    private static final String TAG = "========== GameActivity";

    private TextView tv_score;
    private Button[] button_ops;
    private TextView tv_question;
    private TextView tv_update;
    private TextView tv_timer;
    private ProgressBar pb_logo;
    private ViewGroup transitionsContainer;
    private TextView tv_sm_title;
    private TextView tv_sm_scores;
    private TextView tv_sm_others;

    private GameManager gameManager;
    private UiHandler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHandler = new UiHandler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // TODO - delete
        Intent i = getIntent();
        String playerName = i.getStringExtra("playerName");
        Log.d(TAG,"got player name " + playerName);

        transitionsContainer = findViewById(R.id.activity_battle);
        transitionsContainer.setOnClickListener((v)-> {
            if(gameManager.isGameEnded()){
                finish();
            }
        });
        pb_logo = transitionsContainer.findViewById(R.id.pbHeaderProgress);
        pb_logo.setVisibility(View.VISIBLE);

        buildOpsButtons();

        tv_question = transitionsContainer.findViewById(R.id.tv_question);
        tv_update = transitionsContainer.findViewById(R.id.tv_updates);
        tv_timer = transitionsContainer.findViewById(R.id.tv_timer);

        tv_score = transitionsContainer.findViewById(R.id.tv_score);

        transitionsContainer = findViewById(R.id.activity_battle);
        tv_sm_title = transitionsContainer.findViewById(R.id.tv_summary_title);
        tv_sm_scores = transitionsContainer.findViewById(R.id.tv_summary_answer);
        tv_sm_others = transitionsContainer.findViewById(R.id.tv_summary_others);

        gameManager = new GameManager(SERVER_IP,PORT,this);
        gameManager.registerHandler(new AckResponseHandler());
        gameManager.registerHandler(new ResponseHandler(gameManager,this,uiHandler));

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
        for ( Button b : button_ops){
            b.setOnClickListener((v)-> new AsyncTaskSend().execute(b.getText().toString()));
        }
    }

    public void toPlayerSummaryDisplay(){
        uiHandler.updateTextView(tv_sm_title,String.format(SM_TOTAL_SCORE_TEMPLATE,gameManager.getTotalScore()));
        uiHandler.updateTextView(tv_sm_scores,String.format(SM_SCORE_TEMPLATE,gameManager.getMaxScore(),gameManager.getMinScore()));
        uiHandler.updateTextView(tv_sm_others,"Waiting for other players to finish...");
        TransitionManager.beginDelayedTransition(transitionsContainer);

        uiHandler.setViewVisability(tv_sm_title, View.VISIBLE);
        uiHandler.setViewVisability(tv_sm_scores, View.VISIBLE);
        uiHandler.setViewVisability(tv_sm_others, View.VISIBLE);
        uiHandler.setViewVisability(pb_logo, View.VISIBLE);

        uiHandler.setViewVisability(tv_score, View.INVISIBLE);
        uiHandler.setViewVisability(tv_update, View.INVISIBLE);
        uiHandler.setViewVisability(tv_timer, View.INVISIBLE);
        uiHandler.setViewVisability(tv_question, View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[0], View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[1], View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[2], View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[3], View.INVISIBLE);
    }

    public void updateScoreDisplay(float score){
        String scoreUpdate = String.format(SCORE_TEMPLATE,score);
        uiHandler.updateTextView(tv_score,scoreUpdate);
    }

    public void updateUpdateDisplay(String update){
        uiHandler.updateTextView(tv_update,update);
    }

    public void updateTimerDisplay(String update){
        uiHandler.updateTextView(tv_timer,update);
    }

    public void toOthersSummaryDisplay(String othersSummary){
        uiHandler.setViewVisability(pb_logo, View.INVISIBLE);
        TransitionManager.beginDelayedTransition(transitionsContainer);
        uiHandler.updateTextView(tv_sm_others,String.format(SM_OTHERS_SCORE_TEMPLATE,othersSummary));
    }

    public void updateQuestionsDisplay(GameStage gameStage){
        uiHandler.updateTextView(tv_question,gameStage.getQuestion());
        int i = 0;
        for(Button b:button_ops){
            String newText = gameStage.getPossibleAnswers().get(i++);
            uiHandler.updateButtonText(b,newText);
        }
    }

    private class AsyncTaskSend extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG,String.format("Button clicked "));
            if(gameManager.isGameActive()){
                String answer = strings[0];
                Log.d(TAG,"Player chose answer " + answer);
                gameManager.setPlayerAnswer(answer);
            }
            return null;
        }
    }

    private class AckResponseHandler implements IResponseHandler{

        @Override
        public void handle(GameData gameData) {
            GameData.GameDataType gameDataType = gameData.getType();
            Map<String,String> data = gameData.getContent();

            if (gameDataType == GameData.GameDataType.ACK) {
                Log.d(TAG,String.format("Handling event type %s",gameDataType.toString()));
                handleAck(data);
            }
        }

        private void handleAck(Map<String,String> data) {
            uiHandler.setViewVisability(pb_logo, View.INVISIBLE);
            uiHandler.updateTextView(tv_question,"Waiting for other players...");
        }

    }
}
