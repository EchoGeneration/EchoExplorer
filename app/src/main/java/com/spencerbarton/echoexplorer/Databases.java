package com.spencerbarton.echoexplorer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Databases {

    static class TutorialDatabase  {

        // Parameters representing the state of the database
        private static final SQLiteDatabase.CursorFactory cursorFactory = null;

        // The path to the database file
        private static final String packageName = TutorialDatabase.class.getPackage().getName();
        private static final String dbName = TutorialDatabase.class.getName();
        private static final String dbPath = "/data/data" + packageName + "/databases/" + dbName;

        // FIXME
        private final Context context;
        private SQLiteDatabase tutorialDB;

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Constructor
        ////////////////////////////////////////////////////////////////////////////////////////////

        public TutorialDatabase(Context context) {
            this.context = context;
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        // Private Methods
        ////////////////////////////////////////////////////////////////////////////////////////////

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

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Public Methods
        ///////////////////////////////////////////////////////////////////////////////////////////////

        public void openDatabase() throws IOException, SQLiteException {

            /* If the database is not present, then copy it from the assets/ folder.
             * This is a one-time operation. */
            if (!dbExists(dbPath)) {
                fetchDatabase(context, dbName, dbPath);
            }

            this.tutorialDB = SQLiteDatabase.openDatabase(dbPath, cursorFactory,
                                                          SQLiteDatabase.OPEN_READONLY);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Abstract Implemented Methods
        ////////////////////////////////////////////////////////////////////////////////////////////

        // TODO: add specific queries

        // Given tutorial id, get it's name
        public String idToName(int id) {
            return null;
        }

        // Given a group identifier, get all tutorial ids
        public String [] getAllIds(String tutorialGroup) {
            return null;
        }

    }
}

