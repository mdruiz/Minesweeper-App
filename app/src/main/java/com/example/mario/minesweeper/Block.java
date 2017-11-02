package com.example.mario.minesweeper;

import android.content.Context;
import android.graphics.Color;

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
            this.setBackgroundResource(R.drawable.flag_block);
        }
        else{
            this.setBackgroundResource(R.drawable.block_covered);
        }
    }

    public void setMine(boolean mine){
        this.mine = mine;
    }
    public void switchFlag(){
        setFlag(!this.flag);
    }

    public void uncover(){
        if(this.isMine()){
            this.setBackgroundResource(R.drawable.block_mine);
        }
        else{
            this.setBackgroundResource(R.drawable.block_uncovered);
            if(this.getValue() > 0) {
                int[] colors = {0, Color.BLUE, Color.parseColor("#008000"), Color.RED, Color.parseColor("#000080"),
                        Color.parseColor("#800000"), Color.parseColor("#009999"), Color.BLACK, Color.LTGRAY};
                String val = Integer.toString(this.getValue());
                this.setText(val);
                this.setTextSize(14);
                this.setTextColor(colors[this.getValue()]);
            }
        }
        setCovered(false);
    }

}
