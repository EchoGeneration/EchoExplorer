package com.spencerbarton.echoexplorer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;

/**
 * LessonTable is a class that provides the interface to interacting with the Lesson table in
 * the LessonDatabase. The Lesson table stores all of the data about Lessons in the application.
 * Lessons are either Tutorials or Evaluations, which consist of a series of steps that are used
 * to teach or evaluate a particular type of echo.
 *
 * The Lesson table has the following schema:
 *
 * Lesson(_id, lessonNumber, name, type, description)
 *
 * @author Brandon Perez (bmperez)
 */
public class LessonTable extends Database<Lesson> {

    // A tag for logging messages
    private static final String TAG = LessonTable.class.getName();

    // The name of the database and table
    private static final String DB_NAME = "LessonDatabase";
    private static final String TABLE_NAME = "Lesson";

    // The columns of the table
    private static final String _idCol = "_id";
    private static final String LESSON_NUMBER_COL = "lessonNumber";
    private static final String NAME_COL = "name";
    private static final String TYPE_COL = "type";
    private static final String DESCRIPTION_COL = "description";

    ////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////////////////////////////////////////////

    /* The constructor for the an instance of the TutorialEvaluations table. This simply opens
     * up the corresponding database, and keeps a reference around to it. This function will
     * throw an SQLiteException if the file on disk is not a database. An IOException will be
     * thrown if the database cannot be copied from the assets folder.
     */
    public LessonTable(Context context) throws SQLiteException, IOException {
        super(context, DB_NAME, true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////

    /* Given a cursor from a given query on the database, retrieves the row that the cursor
     * is currently pointing to, and packs the columns into a Lesson structure.
     * This function is used to conform to the Packer interface.
     */
    public Lesson packRow(Cursor cursor) {

        int lessonNumber = Integer.parseInt(getColumnByName(cursor, LESSON_NUMBER_COL));
        String name = getColumnByName(cursor, NAME_COL);
        String type = getColumnByName(cursor, TYPE_COL);
        String description = getColumnByName(cursor, DESCRIPTION_COL);

        return new Lesson(lessonNumber, name, type, description);
    }

    public ContentValues unpackRow(Lesson lesson)
    {
        ContentValues mapping = new ContentValues();

        mapping.put(LESSON_NUMBER_COL, Integer.toString(lesson.lessonNumber));
        mapping.put(NAME_COL, lesson.name);
        mapping.put(TYPE_COL, lesson.type);
        mapping.put(DESCRIPTION_COL, lesson.description);

        return mapping;
    }

    /* This function retrieves all the rows from the Lesson database, and returns it in an
     * array of Lesson structures, sorted by the lesson number field.
     */
    public Lesson[] getAllRows() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + LESSON_NUMBER_COL + " ASC";

        Lesson[] result = unbufferedQuery(query, null, Lesson.class);
        Log.i(TAG + ".getAllEntries", "Querying for all Lesson entries");

        if (result == null) {
            Log.i(TAG + ".getAllEntries", "Query result is empty!");
        }

        return result;
    }
}