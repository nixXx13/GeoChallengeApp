package com.example.geochallengeapp;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Common.GameStage;
import GeoChallengeClient.GeoChallengeCoreFactory;
import GeoChallengeClient.IGeoChallengeCore;
import GeoChallengeClient.IResponseHandler;

public class GameManager {

    private final String TAG = "============ GameManager";
    private IGeoChallengeCore geoChallengeCore;
    private boolean isGameActive;
    private List<GameStage> gameStageList;
    private int totalQuestionsNumber;
    private List<Float> scores;

    public GameManager(String ip, int port){
        geoChallengeCore = GeoChallengeCoreFactory.getGeoChallengeCore(ip,port);
        isGameActive = false;
        scores = new ArrayList<>();
        Log.i(TAG,"Starting geo challenge core");
        Thread t = new Thread(geoChallengeCore);
        t.start();
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

    public List<GameStage> getGameStageList() {
        return gameStageList;
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

     public void sendServer(String s){
         geoChallengeCore.send(s);
     }
}
