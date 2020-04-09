package com.example.geochallengeapp;

import android.util.Log;

import com.example.geochallengeapp.Activities.GameActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Common.GameData;
import Common.GameStage;
import GeoChallengeClient.GeoChallengeCoreFactory;
import GeoChallengeClient.IGeoChallengeCore;
import GeoChallengeClient.IResponseHandler;

import static com.example.geochallengeapp.Util.Constants.NO_ANSWER;

public class GameManager {

    private final String TAG = "============ GameManager";

    private GameActivity gameActivity;
    private IGeoChallengeCore geoChallengeCore;
    private boolean isGameActive;
    private List<GameStage> gameStageList;
    private int totalQuestionsNumber;
    private List<Float> scores;
    private String playerAnswer;

    public GameManager(String ip, int port, GameActivity gameActivity){
        geoChallengeCore = GeoChallengeCoreFactory.getGeoChallengeCore(ip,port);
        this.gameActivity = gameActivity;
        isGameActive = false;
        scores = new ArrayList<>();
        playerAnswer = NO_ANSWER;
        Log.i(TAG,"Starting geo challenge core");
        Thread t = new Thread(geoChallengeCore);
        t.start();
    }

    public void init(String playerName, String roomName, String isCreate, String roomSize, String roomType){
        String questionsNumber = "5";
        geoChallengeCore.send(new GameData(GameData.GameDataType.ACK,String.format("%s:%s:%s:%s:%s:%s",playerName,roomName,isCreate,
                roomSize,questionsNumber,roomType)));
    }

    public void setGameActive(boolean isGameActive) {
        this.isGameActive = isGameActive;
    }

    public boolean isGameActive() {
        return isGameActive;
    }

    public void setGameStageList(List<GameStage> gameStageList) {
        this.gameStageList = gameStageList;
        totalQuestionsNumber = gameStageList.size();
    }


    public void addGrade(Float grade){
        scores.add(grade);
    }

    public Float getTotalScore(){
        Float sum = 0f;
        for (Float score : scores) {
            sum += score;
        }
        return sum;
    }

    public boolean isAnsweredAllQuestions() {
        return scores.size() == totalQuestionsNumber;
    }

    public Float getMaxScore(){
        return Collections.max(scores);
    }

    public Float getMinScore(){
        return Collections.min(scores);
    }

     public boolean isGameEnded(){
         return isAnsweredAllQuestions() && !isGameActive;
     }

     public void registerHandler(IResponseHandler handler){
         geoChallengeCore.registerHandler(handler);
     }

     public void sendPlayerAnswer(float answerTime){
         Map<String,String> map = new HashMap<>();
         map.put("time",String.valueOf(answerTime));
         map.put("answer",playerAnswer);
         GameData answer = new GameData(GameData.GameDataType.DATA,map);
         geoChallengeCore.send(answer);
         prepareQuestion();
     }

    public void setPlayerAnswer(String playerAnswer) {
        this.playerAnswer = playerAnswer;
    }

    public String getPlayerAnswer() {
        return playerAnswer;
    }

    public void resetPlayerAnswer() {
        this.playerAnswer = NO_ANSWER;
    }

    public void prepareQuestion(){
        // todo - where this method should be?
        resetPlayerAnswer();
        if( !gameStageList.isEmpty()) {
            gameActivity.updateQuestionsDisplay(gameStageList.get(0));
            gameStageList.remove(0);
            TimeGradeThread timeGradeThread = new TimeGradeThread(new ScoreTimeout(gameActivity,this,20,0.1f));
            Thread t = new Thread(timeGradeThread);
            t.start();
        }
    }
}
