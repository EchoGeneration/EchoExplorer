package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;

/**
 * Created by bmperez on 2/28/15.
 */
public class TutorialEvaluationsDb {

    public static final String TYPE_TUTORIAL = "com.spencerbarton.echoexplorer.TYPE_TUTORIAL";
    public static final String TYPE_EVALUATION = "com.spencerbarton.echoexplorer.TYPE_EVALUATION";

    public static class TutorialEvaluationsTable implements Databases.Packer<Lesson> {

        private static final String tag = TutorialEvaluationsTable.class.getName();

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

        // Constructor
        public TutorialEvaluationsTable(Context context) throws SQLiteException, IOException {
            this.tutorialDb = Databases.StaticDatabase.openDatabase(context, dbName);
        }

        // Given a cursor, retrieves all the columns and packs them into a Tutorial Evaluations structure
        public Lesson pack(Cursor cursor) {

            int lessonNumber = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                    lessonNumberCol));
            String name = Databases.CursorHelper.getColumnByName(cursor, nameCol);
            String type = Databases.CursorHelper.getColumnByName(cursor, typeCol);

            return new Lesson(lessonNumber, name, type);
        }

        // Get all ordered by 'order'
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

    public static class Lesson {

        public int lessonNumber;
        public String name;
        public String type;

        public Lesson(int lessonNumber, String name, String type)
        {
            this.lessonNumber = lessonNumber;
            this.name = name;
            this.type = type;
        }
    }
}
