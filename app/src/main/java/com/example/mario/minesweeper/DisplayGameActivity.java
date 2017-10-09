package com.example.mario.minesweeper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class DisplayGameActivity extends Activity {

    private TableLayout board;
    private MineField mineField;
    private int screenWidth;
    private int blockWidth;
    private int blockHeight;
    private int blockPadding;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_game);

        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;

        board = (TableLayout) findViewById(R.id.minefield);
        createMinefieldGrid(8,8,10);

    }

    protected void createMinefieldGrid(int rows, int columns, int bombs){

        mineField = new MineField(rows,columns,bombs);

        //Button[][] blocks = new Button[rows][columns];

        //get screen width
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenWidth = display.widthPixels;

        blockWidth = screenWidth/ (columns);
        blockHeight = blockWidth;
        blockPadding = blockWidth/2;
        int linearIndex = 0;
        Block[][] blocks = mineField.getBlocks();

        //make a new TableRow for each row of blocks
        for(int i = 0; i < rows; i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(blockWidth * columns, blockHeight * rows));
//            tableRow.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT));
            for(int j = 0; j < columns; j++){
                blocks[i][j] = new Block(this);
                blocks[i][j].setLayoutParams(new TableRow.LayoutParams(blockWidth,blockHeight));
                blocks[i][j].setBackgroundResource(R.drawable.block_covered);
//                blocks[i][j].setPadding(blockPadding, blockPadding,blockPadding, blockPadding);
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
            if(button.isFlag()){
                return;
            }
            int value = v.getId();
            int totalColumns = mineField.getBlocks()[0].length;
            int row = value / totalColumns;
            int col = value % totalColumns;

            //if minefield has not beeen set, set it
            if(!mineField.areMinesSet()) {
                mineField.setMineField(row, col);
            }
            //if mine, print 'M' instead of value
            if(button.isMine()){
                v.setBackgroundResource(R.drawable.block_uncovered);
                button.setText("M");
                makeToast("You Lose!");
            }
            else {
                mineField.rippleUncover(row,col);
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
//            startVibrate(500);
            Block button = (Block) v;
            button.switchFlag();
            return true;
        }
    };

    public void makeToast(int value){
        int totalColumns = mineField.getBlocks()[0].length;
        int row = value/totalColumns;
        int col = value%totalColumns;
        Toast.makeText(this,"Button ("+row+","+col+") clicked!",Toast.LENGTH_SHORT).show();
    }

    public void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

}
