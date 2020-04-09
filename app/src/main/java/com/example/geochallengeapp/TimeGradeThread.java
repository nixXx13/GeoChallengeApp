package com.example.geochallengeapp;

import android.util.Log;

import com.example.geochallengeapp.Util.ITickingObject;

public class TimeGradeThread implements Runnable{

    private static final String TAG = "========== TimeGradeThread";

    private float maxTime;
    private float timeStep;
    private ITickingObject tickingObject;

    public TimeGradeThread(ITickingObject tickingObject){
        this(tickingObject,10,0.1f);
    }

    public TimeGradeThread(ITickingObject tickingObject, float maxTime, float timeStep){
        this.tickingObject = tickingObject;
        this.maxTime = maxTime;
        this.timeStep = timeStep;
    }

    @Override
    public void run() {
        synchronized (this){
            try {
                float timeLeft = maxTime;
                while (timeLeft > 0) {
                    if (tickingObject.onTick()) {
                        break;
                    }
                    timeLeft = (float) (Math.round((timeLeft - timeStep)* 10d) / 10d);
                    wait(100);
                }
                tickingObject.onTrigger();
            } catch (InterruptedException e) {
                Log.e(TAG, "Error");
                e.printStackTrace();
            }
        }
    }
}

