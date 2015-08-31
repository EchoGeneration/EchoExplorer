package com.spencerbarton.echoexplorer.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Susan on 8/25/15.
 */
public class UserStatsTable extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UserStatsDB";

    //table name
    private static final String TABLE_ALLRESPONSE = "all_response";

    //Columns names
    private static final String KEY_TIMESTAMP = "time";
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_STEP = "step";

    public UserStatsTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESPONSE_TABLE = "CREATE TABLE " + TABLE_ALLRESPONSE + "("
                + KEY_TIMESTAMP + " INTEGER PRIMARY KEY,"
                + KEY_STEP + " INTEGER,"
                + KEY_RESPONSE + " TEXT" + ")";
        db.execSQL(CREATE_RESPONSE_TABLE);
        Log.d("onCreate", "table created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALLRESPONSE);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new response
    public void add(UserStats userStats) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("add", "make writable");
        ContentValues values = new ContentValues();
        values.put(KEY_TIMESTAMP, userStats.getTimestamp());
        values.put(KEY_STEP, userStats.getStepNum());
        values.put(KEY_RESPONSE, userStats.getResponse());
        // Inserting Row
        db.insert(TABLE_ALLRESPONSE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single response
    public UserStats getResponse(int timestamp) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALLRESPONSE, new String[]{KEY_TIMESTAMP,
                        KEY_STEP, KEY_RESPONSE}, KEY_TIMESTAMP + "=?",
                new String[]{String.valueOf(timestamp)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        UserStats response = new UserStats(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),cursor.getString(2));
        // return response
        return response;
    }

    // Getting All response
    public List<UserStats> getAllResponse() {
        List<UserStats> responseList = new ArrayList<UserStats>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALLRESPONSE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserStats response = new UserStats(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),cursor.getString(2));
                // Adding contact to list
                responseList.add(response);
            } while (cursor.moveToNext());
        }

        // return contact list
        return responseList;
    }

    // Deleting single response
    public void delete(UserStats response) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALLRESPONSE, KEY_TIMESTAMP + " = ?",
                new String[] { String.valueOf(response.getTimestamp()) });
        db.close();
    }


    // Getting Count
    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ALLRESPONSE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
        //return cursor.getCount();
    }

    public int getNumCol() {
        String countQuery = "SELECT  * FROM " + TABLE_ALLRESPONSE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getColumnCount();
        cursor.close();

        return count;
    }

    public Cursor readEntry(){
        String selectQuery = "SELECT  * FROM " + TABLE_ALLRESPONSE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

}
