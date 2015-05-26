package com.spencerbarton.echoexplorer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;

/* This class encapsulates the functionality for making queries on the TutorialStep table. The
 * Tutorial Step table contains metadata about a step of a tutorial lesson. A tutorial lesson is
 * a sequence of these steps. It contains the number of the lesson it belongs to, which is used to
 * search the table based on the lesson. It contains the step number, which provides a relative
 * ordering to other steps in the same lesson. It also contains the file that contains the audio
 * for directions for the step, the audio file that contains the echo for the step, and the
 * text version of the directions.
 */
public class TutorialTable extends Database<Tutorial> {

    // A TAG for logging messages
    private static final String TAG = TutorialTable.class.getName();

    // The name of the database and table
    private static final String DB_NAME = "LessonDatabase";
    private static final String TABLE_NAME = "TutorialStep";

    // The columns of the table
    private static final String _idCol = "_id";
    private static final String LESSON_NUMBER_COL = "lessonNumber";
    private static final String STEP_NUMBER_COL = "stepNumber";
    private static final String AUDIO_DIR_COL = "directionsAudioFile";
    private static final String ECHO_COL = "echoAudioFile";
    private static final String TEXT_DIRECTIONS_COL = "textDirections";

    // Constructor
    public TutorialTable(Context context)  throws SQLiteException, IOException {
        super(context, DB_NAME, true);
    }

    // Given a cursor, retrieves all the columns and packs them into a Tutorial Step structure
    public Tutorial packRow(Cursor cursor) {
        int lessonNumber = Integer.parseInt(getColumnByName(cursor, LESSON_NUMBER_COL));
        int stepNumber = Integer.parseInt(getColumnByName(cursor, STEP_NUMBER_COL));
        String audioDirFile = getColumnByName(cursor, AUDIO_DIR_COL);
        String echoFile = getColumnByName(cursor, ECHO_COL);
        String textDirections = getColumnByName(cursor, TEXT_DIRECTIONS_COL);

        return new Tutorial(lessonNumber, stepNumber, audioDirFile, echoFile, textDirections);
    }

    public ContentValues unpackRow(Tutorial tutorial)
    {
        ContentValues mapping = new ContentValues();

        mapping.put(LESSON_NUMBER_COL, Integer.toString(tutorial.lessonNumber));
        mapping.put(STEP_NUMBER_COL, Integer.toString(tutorial.stepNumber));
        mapping.put(AUDIO_DIR_COL, tutorial.audioDirFile);
        mapping.put(ECHO_COL, tutorial.echoFile);
        mapping.put(TEXT_DIRECTIONS_COL, tutorial.textDirections);

        return mapping;
    }

    // Given a lesson number and a step, retrieves a given step of a tutorial
    public Tutorial getRow(int lessonNumber, int stepNumber) {
        String query = "SELECT * FROM " + TABLE_NAME + " where " + LESSON_NUMBER_COL + " = ? and " +
                STEP_NUMBER_COL + " = ?";

        String[] args = {Integer.toString(lessonNumber), Integer.toString(stepNumber)};
        Tutorial[] result = unbufferedQuery(query, args, Tutorial.class);
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
    public Tutorial[] getAllRows() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + LESSON_NUMBER_COL + " ASC, "
                + " ASC";

        Tutorial[] result = unbufferedQuery(query, null, Tutorial.class);
        Log.i(TAG +".getTutorials", "Querying for all tutorial steps");

        // The cursor is empty, the table is empty
        if (result == null) {
            Log.e(TAG +".getTutorials", "Query result is empty!");
        }

        return result;
    }

    // Given a lesson number, returns the tutorials in order of their steps
    public Tutorial[] getAllRows(int lessonNumber) {
        String query = "SELECT * FROM " + TABLE_NAME + " where " + LESSON_NUMBER_COL + " = ? " +
                "ORDER BY " + STEP_NUMBER_COL + " ASC";

        String[] args = {Integer.toString(lessonNumber)};
        Tutorial[] result = unbufferedQuery(query, args, Tutorial.class);
        Log.i(TAG +".getTutorials", "Querying for all steps of tutorial " +
                Integer.toString(lessonNumber));

        // The cursor is empty, the table is empty
        if (result == null) {
            Log.e(TAG +".getTutorials", "Query result is empty!");
        }

        return result;
    }
}