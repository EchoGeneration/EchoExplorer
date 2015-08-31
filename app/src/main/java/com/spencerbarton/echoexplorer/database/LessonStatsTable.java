package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.io.IOException;

/**
 * Created by Susan on 5/25/15.
 */
public class LessonStatsTable extends Database<LessonStats> {

    // Tag for debugging
    private static final String TAG = LessonStatsTable.class.getName();

    // The name of the database and table
    private static final String DB_NAME = "Statistics";
    private static final String TABLE_NAME = "LessonStats";

    // The columns of the table
    private static final String _idCol = "_id";
    private static final String RESPONSE_COL = "response";
    private static final String TIMESTAMP_COL = "timestamp";

    public LessonStatsTable(Context context) throws SQLiteException, IOException{
        super(context, DB_NAME, true);
    }

    @Override
    public LessonStats packCursorEntry(Cursor cursor) {
        int mTimestamp = Integer.parseInt(getColumnByName(cursor,
                TIMESTAMP_COL));
        String mResponse = getColumnByName(cursor, RESPONSE_COL);
        return new LessonStats(mTimestamp, mResponse);
    }

}
