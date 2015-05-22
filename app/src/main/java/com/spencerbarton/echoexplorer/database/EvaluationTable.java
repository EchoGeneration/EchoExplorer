package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationTable extends Database<Evaluation> {

    // Tag for debugging
    private static final String TAG = EvaluationTable.class.getName();

    // The name of the database and table
    private static final String DB_NAME = "LessonDatabase";
    private static final String TABLE_NAME = "EvaluationStep";

    // The columns of the table
    private static final String _idCol = "_id";
    private static final String LESSON_NUMBER_COL = "lessonNumber";
    private static final String STEP_NUMBER_COL = "stepNumber";
    private static final String AUDIO_DIR_COL = "directionsAudioFile";
    private static final String ECHO_COL = "echoAudioFile";
    private static final String TEXT_DIR_COL = "textDirections";
    private static final String RESPONSE_OPT_COL = "responseOptions";
    private static final String CORRECT_RESPONSE_COL = "correctResponse";

    // Constructor
    public EvaluationTable(Context context) throws SQLiteException, IOException {
        super(context, DB_NAME, true);
    }

    // Given a cursor, retrieves all the columns and packs them into a Tutorial Step structure
    public Evaluation packCursorEntry(Cursor cursor) {

        int lessonNumber = Integer.parseInt(getColumnByName(cursor,
                LESSON_NUMBER_COL));
        int stepNumber = Integer.parseInt(getColumnByName(cursor,
                STEP_NUMBER_COL));
        String audioDirFile = getColumnByName(cursor, AUDIO_DIR_COL);
        String echoFile = getColumnByName(cursor, ECHO_COL);
        String textDir = getColumnByName(cursor, TEXT_DIR_COL);
        String responseOptions = getColumnByName(cursor, RESPONSE_OPT_COL);
        int correctResponse = Integer.parseInt(getColumnByName(cursor,
                CORRECT_RESPONSE_COL));

        return new Evaluation(lessonNumber, stepNumber, audioDirFile, echoFile, textDir,
                responseOptions, correctResponse);
    }

    // Given a lesson number and a step, retrieves a given step of a tutorial
    public Evaluation getRow(int lessonNumber, int stepNumber) {
        String query = "SELECT * FROM " + TABLE_NAME + " where " + LESSON_NUMBER_COL + " = ? and "+
                STEP_NUMBER_COL + " = ?";

        String[] args = {Integer.toString(lessonNumber), Integer.toString(stepNumber)};
        Evaluation[] result = unbufferedQuery(query, args, Evaluation.class);
        Log.e(TAG +".getRow", "Querying for row with lessonId=" + Integer.toString(lessonNumber)+
                " and stepNumber=" + Integer.toString(stepNumber));

        // The cursor is empty, then the tutorial does not exist
        if (result == null) {
            Log.e(TAG +".getRow", "Query result is empty!");
            return null;
        }

        return result[0];
    }

    // Gets a list of all the tutorials, sorted by lessonId, then by step number
    public Evaluation[] getAllRows() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + LESSON_NUMBER_COL + " ASC, "
                + " ASC";

        Evaluation[] result = unbufferedQuery(query, null, Evaluation.class);
        Log.e(TAG +".getTutorials", "Querying for all tutorial steps");

        // The cursor is empty, the table is empty
        if (result == null) {
            Log.e(TAG +".getTutorials", "Query result is empty!");
        }

        return result;
    }

    // Given a lesson number, returns the tutorials in order of their steps
    public Evaluation[] getAllRows(int lessonNumber) {
        String query = "SELECT * FROM " + TABLE_NAME + " where " + LESSON_NUMBER_COL + " = ? " +
                "ORDER BY " + STEP_NUMBER_COL + " ASC";

        String[] args = {Integer.toString(lessonNumber)};
        Evaluation[] result = unbufferedQuery(query, args, Evaluation.class);

        Log.i(TAG +".getTutorials", "Querying for all steps of tutorial " +
                Integer.toString(lessonNumber));

        // The cursor is empty, the table is empty
        if (result == null) {
            Log.e(TAG +".getTutorials", "Query result is empty!");
        }

        return result;
    }

}