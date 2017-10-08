package com.example.mario.minesweeper;

import android.content.Context;
import android.widget.Button;

import java.util.concurrent.BlockingQueue;

/**
 * Created by mario on 10/6/2017.
 */

class Block extends android.support.v7.widget.AppCompatButton{
    private boolean covered;
    private boolean flag;
    private boolean mine;
    private int value;

    public Block(Context context){
        super(context);
        this.covered = true;
        this.flag = false;
        this.mine = false;
        this.value = 0;
    }

    public int getValue(){
        return this.value;
    }
    public boolean isCovered(){
        return this.covered;
    }
    public boolean isFlag(){ return this.flag; }
    public boolean isMine(){ return this.mine; }

    public void setCovered(boolean bool){
        this.covered = bool;
    }
    public void setValue(int value){
        this.value = value;
    }
    public void setFlag(boolean flag){
        this.flag = flag;
        if(this.flag){
            this.setText("F");
        }
        else{
            this.setText("");
        }
    }

    public void setMine(boolean mine){
        this.mine = mine;
    }


    public void switchFlag(){
        setFlag(!this.flag);
    }

    public void uncover(){
        setCovered(false);
    }

}
