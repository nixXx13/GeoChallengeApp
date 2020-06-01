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
import com.example.geochallengeapp.IGameManagerStarter;
import com.example.geochallengeapp.R;
import com.example.geochallengeapp.Handlers.ResponseHandler;
import com.example.geochallengeapp.Util.RestUtil;
import com.example.geochallengeapp.Util.UiHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import static com.example.geochallengeapp.Util.Constants.*;

public class GameActivity extends AppCompatActivity implements IGameManagerStarter,IResponseHandler {

//    private final String SERVER_IP = "10.0.2.2";
    // TODO - DO NOT PUBLISH
    private final String LAMBDA_URL = "https://xxxxxxxx.execute-api.eu-central-1.amazonaws.com/dev/gcserver";
    private final int PORT = 4567;

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

    private String playerName;
    private String roomName;
    private String roomType;
    private String roomSize;
    private String isCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHandler = new UiHandler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        transitionsContainer = findViewById(R.id.activity_battle);

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

        Intent i = getIntent();
        playerName = i.getStringExtra("name");
        roomName = i.getStringExtra("rooName");
        roomSize = i.getStringExtra("roomSize");
        roomType = i.getStringExtra("roomType");
        isCreate = i.getStringExtra("isCreate");

        new HttpGetRequest(this).execute(LAMBDA_URL, roomName);
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
            b.setOnClickListener((v)-> onButtonClick(v,b));
            b.setVisibility(View.INVISIBLE);
        }
    }

    public void toPlayerSummaryDisplay(){

        uiHandler.setViewVisability(tv_score, View.INVISIBLE);
        uiHandler.setViewVisability(tv_update, View.INVISIBLE);
        uiHandler.setViewVisability(tv_timer, View.INVISIBLE);
        uiHandler.setViewVisability(tv_question, View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[0], View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[1], View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[2], View.INVISIBLE);
        uiHandler.setViewVisability(button_ops[3], View.INVISIBLE);

        uiHandler.updateTextView(tv_sm_title,String.format(SM_TOTAL_SCORE_TEMPLATE,gameManager.getTotalScore()));
        uiHandler.updateTextView(tv_sm_scores,String.format(SM_SCORE_TEMPLATE,gameManager.getMaxScore(),gameManager.getMinScore()));
        uiHandler.updateTextView(tv_sm_others,"Waiting for other players to finish...");
        TransitionManager.beginDelayedTransition(transitionsContainer);

        uiHandler.setViewVisability(tv_sm_title, View.VISIBLE);
        uiHandler.setViewVisability(tv_sm_scores, View.VISIBLE);
        uiHandler.setViewVisability(tv_sm_others, View.VISIBLE);
        uiHandler.setViewVisability(pb_logo, View.VISIBLE);

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

    public void toSummaryDisplay(String summary){
        uiHandler.setViewVisability(pb_logo, View.INVISIBLE);
        Intent i = new Intent(this, SummaryActivity.class);

        String[] summaryArr = summary.split("&");
        i.putExtra("summary", summaryArr[0]);

        String best = "";
        if(summaryArr.length > 1)
            best = summaryArr[1];

        i.putExtra("best", best);
        startActivity(i);
        finish();
    }

    public void updateQuestionsDisplay(GameStage gameStage){
        uiHandler.updateTextView(tv_question,gameStage.getQuestion());
        int i = 0;
        for(Button b:button_ops){
            String newText = gameStage.getPossibleAnswers().get(i++);
            uiHandler.updateButtonText(b,newText);
            uiHandler.setViewVisability(b,View.VISIBLE);
        }
    }

    private void onButtonClick(View view, Button b) {
        if(gameManager.isGameActive()){
            String answer = b.getText().toString();
            Log.d(TAG,"Player chose answer " + answer);
            gameManager.setPlayerAnswer(answer);
        }
    }

    @Override
    public void handle(GameData gameData) {
        GameData.GameDataType gameDataType = gameData.getType();
        Map<String, String> data = gameData.getContent();

        if (gameDataType == GameData.GameDataType.ACK) {
            String ackMsg = data.get("msg");
            Log.d(TAG, String.format("Handling event type ack %s", ackMsg));
            if ("Connected".equals(ackMsg)) {
//                new AsyncTaskSendConfig(gameManager, playerName, roomName, isCreate).execute();
                new AsyncTask() {
                    @Override
                    protected Void doInBackground(Object... objects) {
                        gameManager.init(playerName, roomName, isCreate, roomSize, roomType);
                        return null;
                    }
                }.execute();
            } else {
                // joined room
                uiHandler.setViewVisability(pb_logo, View.INVISIBLE);
                uiHandler.updateTextView(tv_question,"Waiting for other players...");
            }
        }
    }

    @Override
    public void start(String ip,int port){
        gameManager = new GameManager(ip,port,this);
        gameManager.registerHandler(this);
        gameManager.registerHandler(new ResponseHandler(gameManager,this,uiHandler));
    }


    public class  HttpGetRequest extends AsyncTask<String, Void, String> {

        private IGameManagerStarter gameManagerStarter;
        public HttpGetRequest(IGameManagerStarter gameManagerStarter){
            this.gameManagerStarter = gameManagerStarter;
        }

        @Override
        protected String doInBackground(String... params){
            String url = params[0];
            String room = params[1];
            return RestUtil.get(url, room);
        }
        protected void onPostExecute(String result){
            JsonObject jsonObject = RestUtil.toJson(result);
            JsonElement jsonElement = jsonObject.get("body");
            if (jsonElement!= null) {
                String ip = jsonElement.getAsString().replace("\"", "");
                gameManagerStarter.start(ip,PORT);
            }

        }
    }
}
