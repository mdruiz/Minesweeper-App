package com.example.mario.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class MainMenu extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.minesweeper.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void startGame(View view){
        //check which difficulty was selected
        RadioButton easy = (RadioButton) findViewById(R.id.easy_button);
        RadioButton normal = (RadioButton) findViewById(R.id.normal_button);
        RadioButton hard = (RadioButton) findViewById(R.id.hard_button);
        String message = "0";

        if(easy.isChecked()){ message = "1"; }
        else if (normal.isChecked()){ message = "2"; }
        else if (hard.isChecked()){ message = "3"; }
        else{
            //makeToast("Select a difficulty");
            return;
        }

        Intent intent = new Intent(this,DisplayGameActivity.class);
        intent.putExtra(EXTRA_MESSAGE,message);
        startActivity(intent);
    }
}
