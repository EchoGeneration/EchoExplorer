package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;

/* This class encapsulates the functionality for making queries on the Lesson table. The Lesson
 * table contains metadata about lessons, which are groups of tutorials or evaluations. It contains
 * the name of the tutorial, which lesson number it is, and whether the lesson is a set of
 * tutorials or evaluations. The lesson number is used to search the TutorialStep or Evaluation
 * step table, which is used to retrieve all the steps in a lesson.
 */
public class LessonTable {

    // The strings that are used to distinguish between tutorial lessons and evaluation lessons
    public static final String TYPE_TUTORIAL = "com.spencerbarton.echoexplorer.TYPE_TUTORIAL";
    public static final String TYPE_EVALUATION = "com.spencerbarton.echoexplorer.TYPE_EVALUATION";

    /* This class represents the Lesson table in the Tutorial Database, and implements opening
     * and querying the table for data.
     */
    public static class LessonTableHelp implements Databases.Packer<Lesson> {

        // A tag for logging messages
        private static final String tag = LessonTable.class.getName();

        // The name of the database and table
        private static final String dbName = "LessonDatabase";
        private static final String tableName = Lesson.class.getName();

        // The columns of the table
        private static final String _idCol = "_id";
        private static final String lessonNumberCol = "ordering";
        private static final String nameCol = "name";
        private static final String typeCol = "type";

        // The database containing the tutorial step table
        private final SQLiteDatabase tutorialDb;

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Constructor
        ////////////////////////////////////////////////////////////////////////////////////////////

        /* The constructor for the an instance of the TutorialEvaluations table. This simply opens
         * up the corresponding database, and keeps a reference around to it. This function will
         * throw an SQLiteException if the file on disk is not a database. An IOException will be
         * thrown if the database cannot be copied from the assets folder.
         */
        public LessonTableHelp(Context context) throws SQLiteException, IOException {
            this.tutorialDb = Databases.StaticDatabase.openDatabase(context, dbName);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Public Methods
        ////////////////////////////////////////////////////////////////////////////////////////////

        /* Given a cursor from a given query on the database, retrieves the row that the cursor
         * is currently pointing to, and packs the columns into a Lesson structure.
         * This function is used to conform to the Packer interface.
         */
        public Lesson pack(Cursor cursor) {

            int lessonNumber = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                    lessonNumberCol));
            String name = Databases.CursorHelper.getColumnByName(cursor, nameCol);
            String type = Databases.CursorHelper.getColumnByName(cursor, typeCol);

            return new Lesson(lessonNumber, name, type);
        }

        /* This function retrieves all the rows from the Lesson database, and returns it in an
         * array of Lesson structures, sorted by the lesson number field.
         */
        public Lesson[] getAllRows() {
            String query = "select * from ? ORDER BY ? ASC";

            String[] args = {tableName, lessonNumberCol};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);
            Log.e(tag + ".getAllEntries", "Querying for all Lesson entries");

            if (!cursor.moveToFirst()) {
                Log.e(tag + ".getAllEntries", "Query result is empty!");
                return null;
            }

            return Databases.CursorHelper.getAllEntries(cursor, this);
        }
    }

    /* This class is a structure that represents the data contained in a single row of the Lesson
     * database.
     */
    public static class Lesson {

        // The columns in a row of the Lesson database
        public int lessonNumber;
        public String name;
        public String type;

        // The constructor method
        public Lesson(int lessonNumber, String name, String type)
        {
            this.lessonNumber = lessonNumber;
            this.name = name;
            this.type = type;
        }
    }
}
