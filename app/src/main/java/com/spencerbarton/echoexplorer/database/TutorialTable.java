package com.spencerbarton.echoexplorer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;

/**
 * TutorialTable is a class that provides the interface to interacting with the Tutorial table in
 * the LessonDatabase. The Tutorial table stores all of the data about a Tutorial step within a
 * lesson necessary to draw the UI and provide the appropriate sound. See the Tutorial class for a
 * more detailed description of the fields.
 *
 * The Tutorial table has the following schema:
 *     Tutorial(_id, lessonNumber, stepNumber, directionsAudioFile, echoAudioFile, textDirections)
 *
 * @author Brandon Perez (bmperez)
 **/
public class TutorialTable extends Database<Tutorial> {

    /** The tag that identifies this class. Used for debugging. */
    private static final String TAG = TutorialTable.class.getName();

    /** The name of the database that contains the Tutorial table. */
    private static final String DB_NAME = "LessonDatabase";
    /** The name of the tutorial table. */
    private static final String TABLE_NAME = "TutorialStep";

    /** The name of the column that corresponds to the row number in table. Used by Cursor objects
     *  to order the results of a query. */
    private static final String _idCol = "_id";
    /** The name of the column that contains the number of the lesson the tutorial belongs to. */
    private static final String LESSON_NUMBER_COL = "lessonNumber";
    /** The name of the column that contains the step number of the tutorial. This provides an
     *  ordering of the tutorials in a lesson. */
    private static final String STEP_NUMBER_COL = "stepNumber";
    /** The name of the column that contains the audio directions file name. */
    private static final String AUDIO_DIR_COL = "directionsAudioFile";
    /** The name of the column that contains the echo file name. */
    private static final String ECHO_COL = "echoAudioFile";
    /** The name of the column that contains the text directions for the tutorial. */
    private static final String TEXT_DIRECTIONS_COL = "textDirections";

    //----------------------------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------------------------

    /**
     * Constructs a TutorialTable object using the application's current context, and constructs
     * the corresponding database object.
     *
     * @param context The application's context. Used by the Database base class.
     * @throws SQLiteException The database file is not properly formatted.
     * @throws IOException The database file is not writeable or readable, or the dbName does
     *                     does not exist in the assets folder (if the database is static).
     **/
    public TutorialTable(Context context)  throws SQLiteException, IOException {
        super(context, DB_NAME, true);
    }

    //----------------------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------------------

    /**
     * Given a Cursor object from a query, extracts all of the columns in the Tutorial table from
     * the next entry in the Cursor object, and packs it into a new Tutorial object. Returns a
     * handle to this new object.
     *
     * This implements the abstract method in the Database superclass.
     *
     * @param cursor The Cursor object to pack the next entry into the Tutorial object.
     * @return A new Tutorial object, with the information from the next cursor entry.
     **/
    public Tutorial packRow(Cursor cursor) {
        int lessonNumber = Integer.parseInt(getColumnByName(cursor, LESSON_NUMBER_COL));
        int stepNumber = Integer.parseInt(getColumnByName(cursor, STEP_NUMBER_COL));
        String audioDirFile = getColumnByName(cursor, AUDIO_DIR_COL);
        String echoFile = getColumnByName(cursor, ECHO_COL);
        String textDirections = getColumnByName(cursor, TEXT_DIRECTIONS_COL);

        return new Tutorial(lessonNumber, stepNumber, audioDirFile, echoFile, textDirections);
    }

    /**
     * Given an Tutorial object, converts it into a ContentValues object (dictionary), effectively
     * unpacking the row. The name of each column in the Lesson table maps to the corresponding
     * value provided Lesson object.
     *
     * This implements the abstract method in the Database superclass.
     *
     * @param tutorial The Tutorial object to unpack.
     * @return A new ContentValues object (dictionary), where the column names maps to their values.
     **/
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

    /**
     * Given a lesson number and a step number, retrieves the corresponding tutorial from the
     * Tutorial table if it exists. Returns null if the tutorial cannot be found.
     *
     * @param lessonNumber The lesson number of the lesson that the tutorial belongs to.
     * @param stepNumber The step number of the tutorial in the given lesson.
     * @return The Tutorial object corresponding to the entry in the Tutorial table if it
     *         exists, and null otherwise.
     **/
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

    /**
     * Given a lesson number, returns all of the tutorials for that lesson, sorted by their
     * step number.
     *
     * @param lessonNumber The number of the lesson to retrieve the tutorials for.
     * @return An array of Tutorial objects, sorted by step number.
     **/
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

    /**
     * Retrieves all of the rows from the Tutorial table, returning it as an array of Tutorial
     * objects, sorted by the lesson number, then the step number (ascending).
     *
     * @return An array of Evaluation objects, sorted by lesson number, then step number
     *         (ascending).
     **/
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
}