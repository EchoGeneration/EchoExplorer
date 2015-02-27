package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Databases {

    // Provides a convenient wrapper to access a column
    public static String getColumnByName(Cursor dbCursor, String colName) {
        return dbCursor.getString(dbCursor.getColumnIndex(colName));
    }


    static class TutorialDatabase  {

        // Parameters representing the state of the database
        private static final SQLiteDatabase.CursorFactory cursorFactory = null;

        // The path to the database file
        private static final String packageName = TutorialDatabase.class.getPackage().getName();
        private static final String dbName = TutorialDatabase.class.getName();
        private static final String dbPath = "/data/data/" + packageName + "/databases/" + dbName;

        private final Context context;
        private SQLiteDatabase tutorialDb;

        public TutorialDatabase(Context context) throws SQLiteException, IOException {
            this.context = context;
            this.tutorialDb = openDatabase(context);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Private Methods
        ////////////////////////////////////////////////////////////////////////////////////////////

        private static SQLiteDatabase openDatabase(Context context) throws IOException, SQLiteException {
            /* If the database is not present, then copy it from the assets/ folder.
             * This is a one-time operation. */
            if (!dbExists(dbPath)) {
                fetchDatabase(context, dbName, dbPath);
            }

            return SQLiteDatabase.openDatabase(dbPath, cursorFactory, SQLiteDatabase.OPEN_READONLY);
        }

        private static boolean dbExists(String path) {
            File dbFile;

            dbFile = new File(path);
            if (!dbFile.exists()) {
                return false;
            } else if (!dbFile.canRead()) {
                return false;
            } else if (!dbFile.isFile()) {
                return false;
            }

            return true;
        }

        private static void fetchDatabase(Context context, String dbName, String destPath) throws IOException {
            InputStream source = context.getAssets().open(dbName);
            OutputStream dest = new FileOutputStream(destPath);

            final int bufSize = 1024;
            byte[] buffer = new byte[bufSize];

            int bytesRead = source.read(buffer);
            while (bytesRead != -1) {
                dest.write(buffer, 0, bufSize);
                bytesRead = source.read(buffer);
            }

            dest.flush();
            dest.close();
            source.close();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Public Methods
        ////////////////////////////////////////////////////////////////////////////////////////////


        public SQLiteDatabase getDatabase() {
            return tutorialDb;
        }

        public void closeDatabase() {
            tutorialDb.close();
        }

    }
}

