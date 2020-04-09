package com.example.geochallengeapp;

import android.util.Log;

import com.example.geochallengeapp.Activities.GameActivity;
import com.example.geochallengeapp.Util.ITickingObject;

import static com.example.geochallengeapp.Util.Constants.NO_ANSWER;

public class ScoreTimeout implements ITickingObject{

    private static final String TAG = "========== ScoreTimeout";

    private GameActivity gameActivity;
    private GameManager gameManager;
    private float timeLeft;
    private float timeStep;


    public ScoreTimeout(GameActivity gameActivity, GameManager gameManager, float maxTime, float timeStep){
        this.gameActivity = gameActivity;
        this.gameManager = gameManager;
        this.timeLeft = maxTime;
        this.timeStep = timeStep;
    }

    @Override
    public void init() {
        Log.d(TAG, "Waiting for player answer");
    }

    @Override
    public boolean onTick() {
        gameActivity.updateTimerDisplay(timeLeft+ " seconds to answer!");
        timeLeft = (float) (Math.round((timeLeft - timeStep)* 10d) / 10d);
        if (!gameManager.getPlayerAnswer().equals(NO_ANSWER)){
            Log.d(TAG, "Player chose an answer!");
            return true;
        }
        return false;
    }

    @Override
    public void onTrigger() {
        gameManager.sendPlayerAnswer(timeLeft);
    }
}
