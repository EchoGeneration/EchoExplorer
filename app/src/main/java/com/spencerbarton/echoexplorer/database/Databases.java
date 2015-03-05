package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class Databases {

    // This is to insure that Databases is a static class
    private Databases() {}

    // Defines an interface for a class that accesses a database containing types T
    public interface Packer<T> {
        T pack(Cursor cursor);
    }

    // We can suppress the unchecked warning because the compiler statically enforces that entry.cls
    // is the class corresponding to type T
    @SuppressWarnings("unchecked")
    static class CursorHelper {

        private CursorHelper() {}

        // Provides a convenient wrapper to access a column
        public static String getColumnByName(Cursor dbCursor, String colName) {
            return dbCursor.getString(dbCursor.getColumnIndex(colName));
        }

        public static <T> List<T> getAllEntries(Cursor cursor, Packer<T> dbHelp) {
            List<T> entries = new ArrayList<>();

            // Iterate over the entries, and collect them into an array
            while (!cursor.isAfterLast()) {
                entries.add(dbHelp.pack(cursor));
                cursor.moveToNext();
            }

            return entries;
        }
    }

    static final class StaticDatabase  {

        // Tag for debugging
        private static final String tag = StaticDatabase.class.getName();

        // Parameters representing the state of the database
        private static final SQLiteDatabase.CursorFactory cursorFactory = null;

        // The directory containing the database file
        private static final String packageName = StaticDatabase.class.getPackage().getName(); // TODO use instead of hard coding
        private static final String dataDir = Environment.getDataDirectory().getAbsolutePath();
        private static final String dbDir = dataDir + "/data/com.spencerbarton.echoexplorer/databases/";
        private static final String TAG = "StaticDatabase";

        // Ensures that this class is static (cannot be instantiated)
        private StaticDatabase() {}

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

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Public Methods
        ////////////////////////////////////////////////////////////////////////////////////////////


        public static SQLiteDatabase openDatabase(Context context, String dbName)
            throws IOException, SQLiteException
        {
            String path = dbDir + dbName;
            Log.i(tag, "Opening up database: " + path);

            /* If the database is not present, then copy it from the assets/ folder.
             * This is a one-time operation. */
            /* TODO loading db every time because sometimes db file updated
               TODO need to change this after debugging phase
            if (!dbExists(path)) {
                Log.i(tag, "Copying database from the assets/ folder");

                // Create database directory
                // TODO move elsewhere
                File dbDirectory = new File(dbDir);
                boolean success = dbDirectory.mkdir();
                Log.i(TAG, "Created new database " + success);

                fetchDatabase(context, dbName, path);
            }
            */
            fetchDatabase(context, dbName, path);

            Log.i(tag, "Opening the database as read only");
            return SQLiteDatabase.openDatabase(path, cursorFactory, SQLiteDatabase.OPEN_READONLY);
        }

    }
}

