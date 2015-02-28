package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.IOException;

/**
 * Created by bmperez on 2/28/15.
 */
public class TutorialEvaluationsDb {

    static class EvaluationStepTable implements Databases.Packer<TutorialEvaluations> {

        // The name of the database and table
        private static final String dbName = "TutorialDatabase";
        private static final String tableName = TutorialEvaluations.class.getName();

        // The columns of the table
        private static final String _idCol = "_id";
        private static final String nameCol = "name";
        private static final String typeCol = "type";
        private static final String orderingCol = "ordering";

        // The database containing the tutorial step table
        private final SQLiteDatabase tutorialDb;

        // Constructor
        public EvaluationStepTable(Context context) throws SQLiteException, IOException {
            this.tutorialDb = Databases.StaticDatabase.openDatabase(context, dbName);
        }

        // Given a cursor, retrieves all the columns and packs them into a Tutorial Evaluations structure
        public TutorialEvaluations pack(Cursor cursor) {

            String name = Databases.CursorHelper.getColumnByName(cursor, nameCol);
            String type = Databases.CursorHelper.getColumnByName(cursor, typeCol);
            int ordering = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                orderingCol));


            return new TutorialEvaluations(name, type, ordering);
        }

        public TutorialEvaluations[] getAllEntries() {
            String query = "select * from ? ORDER BY ? ASC";

            String[] args = {tableName, orderingCol};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);

            if (!cursor.moveToFirst()) {
                return null;
            }

            return Databases.CursorHelper.getAllEntries(cursor, this);
        }
    }

    static class TutorialEvaluations {

        String name;
        String type;
        int ordering;

        public TutorialEvaluations(String name, String type, int ordering)
        {
            this.name = name;
            this.type = type;
            this.ordering = ordering;
        }
    }
}
