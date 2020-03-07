package com.example.geochallengeapp;

import android.util.Log;

import com.example.geochallengeapp.Activities.GameActivity;

import java.util.Map;

import Common.Converter;
import Common.GameData;
import GeoChallengeClient.IResponseHandler;

import static com.example.geochallengeapp.Constants.GAMEDATA_CONTENT_KEY;

public class ResponseHandler implements IResponseHandler {

    private static final String TAG = "========== ResponseHandler";

    private GameManager gameManager;
    private UiHandler uiHandler;
    private GameActivity gameActivity;

    public ResponseHandler(GameManager gameManager, GameActivity gameActivity, UiHandler uiHandler){
        this.gameManager = gameManager;
        this.uiHandler = uiHandler;
        this.gameActivity = gameActivity;
    }

    @Override
    public void handle(GameData gameData) {
        GameData.GameDataType gameDataType = gameData.getType();
        Map<String,String> data = gameData.getContent();

        Log.d(TAG,String.format("Handling event type %s",gameDataType.toString()));
        switch (gameDataType){
            case ERROR:
                handleError(data);
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

    private void handleError(Map<String,String> data) {
        uiHandler.makeToast(gameActivity.getApplicationContext(), data.get(GAMEDATA_CONTENT_KEY));
        gameActivity.finish();
    }

    private void handleData(Map<String,String> data){
        gameManager.setGameActive(true);
        gameManager.setGameStageList(Converter.toGameStageList(data));

        Log.d(TAG,data.toString());
        gameManager.prepareQuestion();
    }

    private void handleEnd(Map<String,String> data){
        gameManager.setGameActive(false);
        String summary = data.get(GAMEDATA_CONTENT_KEY);
        gameActivity.toOthersSummaryDisplay(summary);
    }
    private void handleUpdate(Map<String,String> data){
        String updateStr = data.get(GAMEDATA_CONTENT_KEY);
        Log.d(TAG,data.toString());
        gameActivity.updateUpdateDisplay(updateStr);
    }

    private void handleGrade(Map<String,String> data){
        Float currentScore = Float.valueOf(data.get(GAMEDATA_CONTENT_KEY));
        gameManager.addGrade(currentScore);
        gameActivity.updateScoreDisplay(gameManager.getTotalScore());

        if (gameManager.isAnsweredAllQuestions()){
            gameActivity.toPlayerSummaryDisplay();
        }
    }
}
