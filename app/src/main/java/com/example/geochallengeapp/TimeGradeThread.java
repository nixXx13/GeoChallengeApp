package com.example.geochallengeapp;

import android.util.Log;

import com.example.geochallengeapp.Activities.GameActivity;
import static com.example.geochallengeapp.Constants.NO_ANSWER;

public class TimeGradeThread implements Runnable{

    private static final String TAG = "========== TimeGradeThread";

    private float maxTime;
    private float timeStep;
    private GameActivity gameActivity;
    private GameManager gameManager;

    public TimeGradeThread(GameActivity gameActivity, GameManager gameManager){
        this(gameActivity, gameManager,10,0.1f);
    }

    public TimeGradeThread(GameActivity gameActivity, GameManager gameManager, float maxTime, float timeStep){
        this.gameActivity = gameActivity;
        this.gameManager = gameManager;
        this.maxTime = maxTime;
        this.timeStep = timeStep;
    }

    @Override
    public void run() {
        synchronized (this){
            try {
                Log.d(TAG, "Waiting for player answer");

                float timeLeft = maxTime;
                while (timeLeft > 0) {
                    gameActivity.updateUpdateDisplay(timeLeft+ " seconds to answer!");
                    wait(100);
                    timeLeft = (float) (Math.round((timeLeft - timeStep)* 10d) / 10d);
                    if (!gameManager.getPlayerAnswer().equals(NO_ANSWER)){
                        Log.d(TAG, "Player chose an answer!");
                        break;
                    }
                }
                gameManager.sendPlayerAnswer(timeLeft);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error while waiting for player input");
                e.printStackTrace();
            }
        }
    }
}

