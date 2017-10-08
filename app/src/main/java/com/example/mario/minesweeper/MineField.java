package com.example.mario.minesweeper;

import android.widget.Button;

import java.util.Random;

/**
 * Created by mario on 10/6/2017.
 */

public class MineField {
    private Block[][] blocks;
    private int bombCount;
    private boolean minesSet;

    public MineField(int rows, int columns, int bombs){
        this.blocks = new Block[rows][columns];
        this.bombCount = bombs;
        this.minesSet = false;
    }

    public Block[][] getBlocks(){
        return blocks;
    }

    public boolean areMinesSet(){
        return this.minesSet;
    }

    public void setMineField(int clickedRow, int clickedCol){
        Random rand = new Random();
        int rows = blocks.length;
        int columns = blocks[0].length;
        int mineRow, mineColumn;
        int nearbyMines;

        //set mines
        for (int i = 0; i < bombCount; i++){
            mineRow = rand.nextInt(rows-1);
            mineColumn = rand.nextInt(columns-1);

            //if block was the first clicked or already has a mine, retry
            if(blocks[mineRow][mineColumn].isMine() || (mineRow == clickedRow && mineColumn == clickedCol)){
                i--;
            }
            else{
                blocks[mineRow][mineColumn].setMine(true);
            }
        }

        //count surrounding mines
        for(int curRow = 0; curRow < rows; curRow++){
            for(int curCol = 0; curCol < columns; curCol++){
                if(blocks[curRow][curCol].isMine()){
                    continue;
                }
                nearbyMines = 0;
                for(int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if(curRow+i >= 0 && curRow+i < rows && curCol+j >= 0 && curCol+j < columns){
                            if(blocks[curRow+i][curCol+j].isMine()){
                                nearbyMines++;
                            }
                        }
                    }
                }
                blocks[curRow][curCol].setValue(nearbyMines);
            }
        }
        minesSet = true;
    }

}
