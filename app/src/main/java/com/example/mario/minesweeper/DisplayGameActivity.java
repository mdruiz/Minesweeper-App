package com.example.mario.minesweeper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayGameActivity extends Activity {

    private TableLayout board;
    private MineField mineField;
    private int screenWidth;
    private int blockWidth;
    private int blockHeight;
    private Vibrator vibe;
    private TextView timerText;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private final int[] bombs = {1,12,18,25};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_game);

        //get difficulty from previous activity
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainMenu.EXTRA_MESSAGE);

        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
        board = (TableLayout) findViewById(R.id.minefield);
        timerText = (TextView) findViewById(R.id.timerText);

        int rows = 12;
        int columns = 10;

        createMinefieldGrid(rows,columns,bombs[Integer.parseInt(message)]);
        updateNumberOfBombs();

    }

    protected void createMinefieldGrid(int rows, int columns, int bombs){
        mineField = new MineField(rows,columns,bombs);

        //get screen width
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenWidth = display.widthPixels;

        blockWidth = screenWidth/ (columns);
        blockHeight = blockWidth;
        int linearIndex = 0;
        Block[][] blocks = mineField.getBlocks();

        //make a new TableRow for each row of blocks
        for(int i = 0; i < rows; i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(blockWidth * columns, blockHeight * rows));
            for(int j = 0; j < columns; j++){
                blocks[i][j] = new Block(this);
                blocks[i][j].setLayoutParams(new TableRow.LayoutParams(blockWidth,blockHeight));
                blocks[i][j].setBackgroundResource(R.drawable.block_covered);
                linearIndex = i * columns + j;
                blocks[i][j].setId(linearIndex);
                blocks[i][j].setOnClickListener(buttonClick);
                blocks[i][j].setOnLongClickListener(buttonLongClick);
                tableRow.addView(blocks[i][j]);
            }
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(blockWidth * columns, blockHeight * rows);
            params.setMargins(0,0,0,0);
            board.addView(tableRow,params);
        }
    }

    //onclick listener for blocks
    final View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Block button = (Block) v;
            if(button.isFlag() || !mineField.areMinesActive()){
                return;
            }
            int value = v.getId();
            int totalColumns = mineField.getBlocks()[0].length;
            int row = value / totalColumns;
            int col = value % totalColumns;

            //if minefield has not been set, set it
            if(!mineField.areMinesSet()) {
                startTimer();
                mineField.setMineField(row, col);
            }
            //if mine, print 'M' instead of value
            if(button.isMine()){
                button.uncover();
                stopTimer();
                onLosePopup();
            }
            else if(!button.isCovered()){
                if(!mineField.uncoverSurrounding(row,col)){
                    stopTimer();
                    onLosePopup();
                    return;
                }
                isGameWon();
            }
            else {
                mineField.rippleUncover(row,col);
                isGameWon();
            }
        }
    };
    //onLongClick listener for blocks
    final View.OnLongClickListener buttonLongClick = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v){
            new Handler().post(new Runnable(){
                public void run(){
                    vibe.vibrate(50);
                }
            });
            Block button = (Block) v;
            if(!mineField.areMinesActive()){
                return true;
            }
            if(button.isCovered()) {
                button.switchFlag();
                if(button.isFlag()){
                    mineField.increaseFlagCount();
                }
                else{
                    mineField.decreaseFlagCount();
                }
                updateNumberOfBombs();
            }
            else{
                int value = v.getId();
                int totalColumns = mineField.getBlocks()[0].length;
                int row = value / totalColumns;
                int col = value % totalColumns;
                if(!mineField.uncoverSurrounding(row,col)){
                    stopTimer();
                    onLosePopup();
                    return true;
                }
                isGameWon();
            }
            return true;
        }
    };

    public void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    //debugging purposes
    public void printMinefield(){
        StringBuilder row = new StringBuilder();
        Block[][] blocks = mineField.getBlocks();
        for(int i = 0; i < blocks.length; i++){
            for(int j = 0; j < blocks[0].length; j++){
                if(blocks[i][j].isMine()){
                    row.append("M");
                }
                else {
                    row.append(blocks[i][j].getValue());
                }
            }
            Log.e(Integer.toString(i),row.toString());
            row.setLength(0);
        }
    }

    public void updateNumberOfBombs(){
        int bombs = mineField.getBombCount();
        int flags = mineField.getFlagCount();
        String str;
        if(bombs - flags < 0){
            str = "000";
        }
        else {
            str = "00" + (bombs - flags);
        }
        int len = str.length();
        TextView text = (TextView) findViewById(R.id.numberOfBombs);
        text.setText(str.substring(len-3,len));
    }

    public void isGameWon(){
        //if all blocks except for mines are uncovered, You Win!
        if(mineField.getCoveredCount() == mineField.getBombCount()){
            stopTimer();
            onWinPopup();
        }
    }

/////////////////////////////////// TIMER FUNCTIONALITY ///////////////////////////////////

    private Handler timer = new Handler();
    private int secondsPassed = 0;

    public void startTimer() {
        if (secondsPassed == 0)        {
            timer.removeCallbacks(updateTimeElasped);
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer() {
        // disable call backs
        timer.removeCallbacks(updateTimeElasped);
    }

    // timer call back when timer is ticked
    private Runnable updateTimeElasped = new Runnable() {
        public void run() {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;
            String str = "000" + Integer.toString(secondsPassed);
            int len = str.length();
            timerText.setText(str.substring(len-3,len));

            // add notification
            timer.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            timer.postDelayed(updateTimeElasped, 1000);
        }
    };

/////////////////////////////////// POPUP FUNCTIONALITY ///////////////////////////////////

    @Override
    public void onBackPressed(){
        //inflate layout for popup window
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popup_window,null);

        //create popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(container,height,width,true);
        popupWindow.showAtLocation(findViewById(R.id.gameContainer), Gravity.CENTER,0,0);

        //OnClickListeners for popup buttons
        View exit = container.findViewById(R.id.exitButton);
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View cancel = container.findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    public void onWinPopup(){
        //disable minefield
        mineField.deactivateMines();

        //inflate layout for popup window
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.win_popup,null);

        //create popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(container,height,width,true);
        popupWindow.showAtLocation(findViewById(R.id.gameContainer), Gravity.CENTER,0,0);

        int time = Integer.parseInt(timerText.getText().toString());
        TextView timeString = (TextView) container.findViewById(R.id.winTime);
        timeString.setText("Your time: "+time+" seconds");

        //OnClickListeners for popup buttons
        View again = container.findViewById(R.id.okButton);
        again.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        View cancel = container.findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void onLosePopup(){
        //disable minefield
        mineField.deactivateMines();

        //inflate layout for popup window
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.lose_popup,null);

        //create popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(container,height,width,true);
        popupWindow.showAtLocation(findViewById(R.id.gameContainer), Gravity.CENTER,0,0);

        //OnClickListeners for popup buttons
        View again = container.findViewById(R.id.okButton);
        again.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        View cancel = container.findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

}
