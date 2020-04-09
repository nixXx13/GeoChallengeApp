package com.example.geochallengeapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.geochallengeapp.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class SetupActivity extends AppCompatActivity {

    private Button bPlay;
    private EditText playerName;
    private EditText roomName;
    private TextView tv_roomSize;
    private TextView tv_roomType;
    private Boolean isCreate;
    private ViewGroup transitionsContainer;

    private Spinner roomSize;
    private Spinner roomType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transitionsContainer = findViewById(R.id.activity_setup);

        bPlay = findViewById(R.id.button_play);
        bPlay = findViewById(R.id.button_play);
        bPlay.setOnClickListener(new PlayListener(this));
        isCreate = false;

        playerName = findViewById(R.id.et_name);
        roomName = findViewById(R.id.et_room_name);//
        String name = "Ploni" + ThreadLocalRandom.current().nextInt(0, 1000);
        playerName.setText(name);
        roomName.setText("Oval room");

        roomSize = transitionsContainer.findViewById(R.id.sp_size);
        roomType = transitionsContainer.findViewById(R.id.sp_type);

        tv_roomSize = transitionsContainer.findViewById(R.id.tv_roomSize);
        tv_roomType = transitionsContainer.findViewById(R.id.tv_gameType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.RoomSizeArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSize.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.GameType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomType.setAdapter(adapter2);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rb_create:
                if (checked)
                    isCreate = true;
                    toggleCreateMenu(true);
                    break;

            case R.id.rb_join:
                if (checked)
                    isCreate = false;
                    toggleCreateMenu(false);
                    break;
        }
    }

    private void toggleCreateMenu(boolean isVisible){
        TransitionManager.beginDelayedTransition(transitionsContainer);
        int visibility = View.VISIBLE;
        if(!isVisible) {
            visibility = View.INVISIBLE;
        }
        roomSize.setVisibility(visibility);
        roomType.setVisibility(visibility);
        tv_roomSize.setVisibility(visibility);
        tv_roomType.setVisibility(visibility);
    }


    private  class PlayListener implements View.OnClickListener{

        private Context contex;
        PlayListener(Context contex){
            this.contex = contex;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(contex, GameActivity.class);

            i.putExtra("name", playerName.getText().toString());
            i.putExtra("rooName", roomName.getText().toString());
            i.putExtra("isCreate", isCreate.toString());
            i.putExtra("roomSize", roomSize.getSelectedItem().toString());
            i.putExtra("roomType", roomType.getSelectedItem().toString());
            startActivity(i);
        }
    }

}
