package com.example.mario.minesweeper;

import java.util.Random;

/**
 * Created by mario on 10/6/2017.
 */

public class MineField {
    private Block[][] blocks;
    private int bombCount;
    private int coveredCount;
    private int flagCount;
    private boolean minesSet;
    private boolean minesActive;

    public MineField(int rows, int columns, int bombs){
        this.blocks = new Block[rows][columns];
        this.bombCount = bombs;
        this.coveredCount = rows*columns;
        this.flagCount = 0;
        this.minesSet = false;
        this.minesActive = true;
    }

    public Block[][] getBlocks(){
        return blocks;
    }
    public int getCoveredCount(){
        return  this.coveredCount;
    }
    public int getFlagCount(){
        return this.flagCount;
    }
    public int getBombCount(){return this.bombCount;}

    public boolean areMinesSet(){
        return this.minesSet;
    }
    public boolean areMinesActive(){ return this.minesActive; }

    public void deactivateMines(){this.minesActive = false;}
    public void decreaseCoveredCount(){this.coveredCount--;}
    public void increaseFlagCount(){this.flagCount++;}
    public void decreaseFlagCount(){this.flagCount--;}


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

    public void rippleUncover(int row, int column){
        Block block = blocks[row][column];
        //already uncovered or game lost
        if(!block.isCovered() || !areMinesActive()){
            return;
        }
        block.uncover();
        decreaseCoveredCount();

        if(blocks[row][column].getValue() == 0){
            int totalRows = blocks.length;
            int totalColumns = blocks[0].length;

            for(int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if(row+i >= 0 && row+i < totalRows && column+j >= 0 && column+j < totalColumns){
                        if(blocks[row+i][column+j].isMine() || !blocks[row+i][column+j].isCovered() || blocks[row+i][column+j].isFlag()){
                            continue;
                        }
                        rippleUncover((row+i),(column+j));
                    }
                }
            }
        }

    }

    //returns false if mine was uncovered (game lost)
    public boolean uncoverSurrounding(int row, int column){
        int totalRows = blocks.length;
        int totalColumns = blocks[0].length;
        int nearbyFlags = 0;
        boolean mineUncovered = false;
        //if game is already lost
        if(!areMinesActive()){
            return mineUncovered;
        }

        //count surrounding flags
        for(int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if(row+i >= 0 && row+i < totalRows && column+j >= 0 && column+j < totalColumns){
                    if(blocks[row+i][column+j].isFlag()){
                        nearbyFlags++;
                    }
                }
            }
        }

        if(blocks[row][column].getValue() <= nearbyFlags) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (row + i >= 0 && row + i < totalRows && column + j >= 0 && column + j < totalColumns) {
                        if (!blocks[row + i][column + j].isCovered() || blocks[row + i][column + j].isFlag()) {
                            continue;
                        }
                        if(blocks[row+i][column+j].isMine()){
                            mineUncovered = true;
                            Block block = blocks[row+i][column+j];
                            block.uncover();
                            continue;
                        }
                        rippleUncover((row + i), (column + j));
                    }
                }
            }
        }
        return !mineUncovered;
    }

}
