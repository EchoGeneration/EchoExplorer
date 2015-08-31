package com.spencerbarton.echoexplorer.database;

import android.content.ContentValues;
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

/**
 * EvaluationTable is a class that provides the interface to interacting with the Evaluation table
 * in the LessonDatabase. The Evaluation table stores all of the data about an Evaluation step
 * within a lesson necessary to draw the UI and provide the appropriate sound. See the Evaluation
 * class for a more detailed description of the fields.
 *
 * The Evaluation table has the following schema:
 *     Evaluation(_id, lessonNumber, stepNumber, directionsAudioFile, echoAudioFile, textDirections,
 *                responseOptions, correctResponse)
 *
 * @author Brandon Perez (bmperez)
 **/
public class EvaluationTable extends Database<Evaluation> {

    /** The tag that identifies this class. Used for debugging. */
    private static final String TAG = EvaluationTable.class.getName();

    /** The name of the database that contains the Evaluation table. */
    private static final String DB_NAME = "LessonDatabase";
    /** The name of the evaluation table. */
    private static final String TABLE_NAME = "EvaluationStep";

    /** The name of the column that corresponds to the row number in table. Used by Cursor objects
     *  to order the results of a query. */
    private static final String _idCol = "_id";
    /** The name of the column that contains the number of the lesson the evaluation belongs to. */
    private static final String LESSON_NUMBER_COL = "lessonNumber";
    /** The name of the column that contains the step number of the evaluation. This provides an
     *  ordering of the evaluations in a lesson. */
    private static final String STEP_NUMBER_COL = "stepNumber";
    /** The name of the column that contains the audio directions file name. */
    private static final String AUDIO_DIR_COL = "directionsAudioFile";
    /** The name of the column that contains the echo file name. */
    private static final String ECHO_COL = "echoAudioFile";
    /** The name of the column that contains the text directions for the evaluation. */
    private static final String TEXT_DIR_COL = "textDirections";
    /** The name of the column that contains the response options for the evaluation. */
    private static final String RESPONSE_OPT_COL = "responseOptions";
    /** The name of the column that contains the correct response from the response options. */
    private static final String CORRECT_RESPONSE_COL = "correctResponse";

    //----------------------------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------------------------

    /**
     * Constructs an EvaluationTable object using the application's current context, and constructs
     * the corresponding database object.
     *
     * @param context The application's context. Used by the Database base class.
     * @throws SQLiteException The database file is not properly formatted.
     * @throws IOException The database file is not writeable or readable, or the dbName does
     *                     does not exist in the assets folder (if the database is static).
     **/
    public EvaluationTable(Context context) throws SQLiteException, IOException {
        super(context, DB_NAME, true);
    }

    //----------------------------------------------------------------------------------------------
    // Public Constructor
    //----------------------------------------------------------------------------------------------

    /**
     * Given a Cursor object from a query, extracts all of the columns in the Evaluation table from
     * the next entry in the Cursor object, and packs it into a new Evaluation object. Returns a
     * handle to this new object.
     *
     * This implements the abstract method in the Database superclass.
     *
     * @param cursor The Cursor object to pack the next entry into the Evaluation object.
     * @return A new Evaluation object, with the information from the next cursor entry.
     **/
    public Evaluation packRow(Cursor cursor) {

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

    /**
     * Given an Evaluation object, converts it into a ContentValues object (dictionary), effectively
     * unpacking the row. The name of each column in the Lesson table maps to the corresponding
     * value provided Lesson object.
     *
     * This implements the abstract method in the Database superclass.
     *
     * @param evaluation The Evaluation object to unpack.
     * @return A new ContentValues object (dictionary), where the column names maps to their values.
     **/
    public ContentValues unpackRow(Evaluation evaluation)
    {
        ContentValues mapping = new ContentValues();

        mapping.put(LESSON_NUMBER_COL, Integer.toString(evaluation.lessonNumber));
        mapping.put(STEP_NUMBER_COL, Integer.toString(evaluation.stepNumber));
        mapping.put(AUDIO_DIR_COL, evaluation.directionsAudioFile);
        mapping.put(ECHO_COL, evaluation.echoAudioFile);
        mapping.put(TEXT_DIR_COL, evaluation.textDirections);
        // FIXME: Need to convert response options back to a JSON string
        mapping.put(RESPONSE_OPT_COL, "");
        mapping.put(CORRECT_RESPONSE_COL, evaluation.correctResponse);

        return mapping;
    }

    /**
     * Given a lesson number and a step number, retrieves the corresponding evaluation from the
     * Evaluation table if it exists. Returns null if the evaluation cannot be found.
     *
     * @param lessonNumber The lesson number of the lesson that the evaluation belongs to.
     * @param stepNumber The step number of the evaluation in the given lesson.
     * @return The Evaluation object corresponding to the entry in the Evaluation table if it
     *         exists, and null otherwise.
     **/
    public Evaluation getRow(int lessonNumber, int stepNumber) {
        String query = "SELECT * FROM " + TABLE_NAME + " where " + LESSON_NUMBER_COL + " = ? and " +
                STEP_NUMBER_COL + " = ?";

        String[] args = {Integer.toString(lessonNumber), Integer.toString(stepNumber)};
        Evaluation[] result = unbufferedQuery(query, args, Evaluation.class);
        Log.e(TAG + ".getRow", "Querying for row with lessonId=" + Integer.toString(lessonNumber) +
                " and stepNumber=" + Integer.toString(stepNumber));

        // The cursor is empty, then the evaluation does not exist
        if (result == null) {
            Log.e(TAG + ".getRow", "Query result is empty!");
            return null;
        }

        return result[0];
    }

    /**
     * Given a lesson number, returns all of the evaluations for that lesson, sorted by their
     * step number.
     *
     * @param lessonNumber The number of the lesson to retrieve the evaluations for.
     * @return An array of Evaluation objects, sorted by step number.
     **/
    public Evaluation[] getAllRows(int lessonNumber) {
        String query = "SELECT * FROM " + TABLE_NAME + " where " + LESSON_NUMBER_COL + " = ? " +
                "ORDER BY " + STEP_NUMBER_COL + " ASC";

        String[] args = {Integer.toString(lessonNumber)};
        Evaluation[] result = unbufferedQuery(query, args, Evaluation.class);

        Log.i(TAG +".getevaluations", "Querying for all steps of evaluation " +
                Integer.toString(lessonNumber));

        // The cursor is empty, the table is empty
        if (result == null) {
            Log.e(TAG +".getevaluations", "Query result is empty!");
        }

        return result;
    }

    /**
     * Retrieves all of the rows from the Evaluation table, returning it as an array of Evaluation
     * objects, sorted by the lesson number, then the step number (ascending).
     *
     * @return An array of Evaluation objects, sorted by lesson number, then step number
     *         (ascending).
     **/
    public Evaluation[] getAllRows() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + LESSON_NUMBER_COL + " ASC, "
                + " ASC";

        Evaluation[] result = unbufferedQuery(query, null, Evaluation.class);
        Log.e(TAG +".getevaluations", "Querying for all evaluation steps");

        // The cursor is empty, the table is empty
        if (result == null) {
            Log.e(TAG +".getevaluations", "Query result is empty!");
        }

        return result;
    }
}