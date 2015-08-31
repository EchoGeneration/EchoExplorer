package com.spencerbarton.echoexplorer.database;

/**
 * Created by Susan on 5/25/15.
 */
public class LessonStats {
    int mTimestamp;
    String mResponse;

    public LessonStats(int mTimestamp, String mResponse){
        this.mResponse = mResponse;
        this.mTimestamp = mTimestamp;
    }
}
