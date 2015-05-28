package com.spencerbarton.echoexplorer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;

/**
 * LessonTable is a class that provides the interface to interacting with the Lesson table in
 * the LessonDatabase. The Lesson table stores all of the data about Lessons in the application,
 * with the appropriate information to list each Lesson out in a ListView in the UI. See the Lesson
 * class for a more detailed description of the fields.
 *
 * The Lesson table has the following schema:
 *     Lesson(_id, lessonNumber, name, type, description)
 *
 * @author Brandon Perez (bmperez)
 */
public class LessonTable extends Database<Lesson> {

    /** The tag that identifies this class. Used for debugging. */
    private static final String TAG = LessonTable.class.getName();

    /** The name of the database that contains the Lesson table. */
    private static final String DB_NAME = "LessonDatabase";
    /** The name of the lesson table. */
    private static final String TABLE_NAME = "Lesson";

    /** The name of the column that corresponds to the row number in table. Used by Cursor objects
     *  to order the results of a query. */
    private static final String _idCol = "_id";
    /** The name of the columns that contains the lesson number. */
    private static final String LESSON_NUMBER_COL = "lessonNumber";
    /** The name of the column that contains the lesson name. */
    private static final String NAME_COL = "name";
    /** The name of the column that contains the lesson description. */
    private static final String DESCRIPTION_COL = "description";
    /** The name of the column that contains lesson type. */
    private static final String TYPE_COL = "type";

    //----------------------------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------------------------

    /**
     * Constructs a new LessonTable object using the application's current context, and constructs
     * the corresponding database object.
     *
     * @param context The application's context. Used by the Database base class.
     * @throws SQLiteException The database file is not properly formatted.
     * @throws IOException The database file is not writeable or readable, or the dbName does
     *                     does not exist in the assets folder (if the database is static).
     **/
    public LessonTable(Context context) throws SQLiteException, IOException {
        super(context, DB_NAME, true);
    }

    //----------------------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------------------

    /**
     * Given a Cursor object from a query, extracts all of the columns in the Lesson table from the
     * next entry in the Cursor object, and packs it into a new Lesson object. Returns a handle to
     * this new object.
     *
     * This implements the abstract method in the Database superclass.
     *
     * @param cursor The Cursor object to pack the next entry into the Lesson object.
     * @return A new Lesson object, with the information from the next cursor entry.
     **/
    public Lesson packRow(Cursor cursor) {

        int lessonNumber = Integer.parseInt(getColumnByName(cursor, LESSON_NUMBER_COL));
        String name = getColumnByName(cursor, NAME_COL);
        String type = getColumnByName(cursor, TYPE_COL);
        String description = getColumnByName(cursor, DESCRIPTION_COL);

        return new Lesson(lessonNumber, name, type, description);
    }

    /**
     * Given a Lesson object, converts it into a ContentValues object (dictionary), effectively
     * unpacking the row. The name of each column in the Lesson table maps to the corresponding
     * value provided Lesson object.
     *
     * @param lesson The Lesson object to unpack.
     * @return A new ContentValues object (dictionary), where the column names maps to their values.
     **/
    public ContentValues unpackRow(Lesson lesson)
    {
        ContentValues mapping = new ContentValues();

        mapping.put(LESSON_NUMBER_COL, Integer.toString(lesson.lessonNumber));
        mapping.put(NAME_COL, lesson.name);
        mapping.put(TYPE_COL, lesson.type);
        mapping.put(DESCRIPTION_COL, lesson.description);

        return mapping;
    }

    /**
     * Retrieves all of the rows from the Lesson table, returning it as a an array of Lesson
     * objects, ordered by the lesson number.
     *
     * This implements the abstract method in the Database superclass.
     *
     * @return An array of Lesson objects, ordered by lesson number.
     **/
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