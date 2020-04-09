package com.example.geochallengeapp.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.geochallengeapp.R;

public class SummaryActivity extends AppCompatActivity {

    private ViewGroup transitionsContainer;

    private TextView[] playerScores;

    private TextView tv_bestScores;
    private TextView[] bestScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        transitionsContainer = findViewById(R.id.activity_summary);

        playerScores = new TextView[4];

        tv_bestScores = transitionsContainer.findViewById(R.id.tv_totalScores);
        tv_bestScores.setVisibility(View.INVISIBLE);
        bestScores = new TextView[10];

        buildPlayerScores();
        buildBestScores();

        Intent i = getIntent();
        String summary = i.getStringExtra("summary");
        setPlayerScores(summary);

        String best = i.getStringExtra("best");
        updateBestScores(best);

        transitionsContainer.setOnClickListener((v)-> {
                finish();
        });

    }

    private void buildPlayerScores(){
        playerScores[0] = transitionsContainer.findViewById(R.id.tv_player1);
        playerScores[1] = transitionsContainer.findViewById(R.id.tv_player2);
        playerScores[2] = transitionsContainer.findViewById(R.id.tv_player3);
        playerScores[3] = transitionsContainer.findViewById(R.id.tv_player4);
    }

    private void buildBestScores(){
        bestScores[0] = transitionsContainer.findViewById(R.id.tv_best1);
        bestScores[1] = transitionsContainer.findViewById(R.id.tv_best2);
        bestScores[2] = transitionsContainer.findViewById(R.id.tv_best3);
        bestScores[3] = transitionsContainer.findViewById(R.id.tv_best4);
        bestScores[4] = transitionsContainer.findViewById(R.id.tv_best5);
        bestScores[5] = transitionsContainer.findViewById(R.id.tv_best6);
        bestScores[6] = transitionsContainer.findViewById(R.id.tv_best7);
        bestScores[7] = transitionsContainer.findViewById(R.id.tv_best8);
        bestScores[8] = transitionsContainer.findViewById(R.id.tv_best9);
        bestScores[9] = transitionsContainer.findViewById(R.id.tv_best10);

        for(TextView best : bestScores){
            best.setVisibility(View.INVISIBLE);
        }

    }

    private void setPlayerScores(String summary){
        String[] playerScoresStr = summary.split(";");
        int numPlayers = playerScoresStr.length;

        for(int i=0; i<numPlayers; i++){
            String[] str = playerScoresStr[i].split(" - ");
            String name = str[0];
            String score = str[1];
            String finalString = formattedUserScoreString(name,score);
            playerScores[i].setText(finalString);
        }

    }

    private void updateBestScores(String result) {
        if(!"".equals(result)) {
            tv_bestScores.setVisibility(View.VISIBLE);
            String[] bestStrs = result.split(",");
            if (bestStrs.length <= 10) {
                int i = 0;
                for (String bestStr : bestStrs) {
                    if (!"".equals(bestStr)) {
                        String[] s = bestStr.split(":");
                        String finalString = formattedUserScoreString(s[0], s[1]);
                        bestScores[i].setText(finalString);
                        bestScores[i].setVisibility(View.VISIBLE);
                    }
                    i++;
                }
            }
        }
    }

    private String formattedUserScoreString(String name, String score){
        String separator = String.format("%0" + (20-name.length()) + "d", 0).replace("0", " ");
        return name+separator+score;
    }

}
