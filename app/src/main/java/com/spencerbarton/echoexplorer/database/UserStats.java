package com.spencerbarton.echoexplorer.database;

/**
 * Created by Susan on 8/25/15.
 */
public class UserStats {
    //private variables
    private int _mTimestamp;
    private int _mStepNum;
    private String _mResponse;

    // Empty constructor
    public UserStats(){

    }
    // constructor
    public UserStats(int mTimestamp, int mStepNum, String mResponse){
        this._mResponse = mResponse;
        this._mStepNum = mStepNum;
        this._mTimestamp = mTimestamp;
    }

    public int getTimestamp(){
        return this._mTimestamp;
    }

    public String getResponse(){
        return this._mResponse;
    }

    public int getStepNum(){
        return this._mStepNum;
    }

}
