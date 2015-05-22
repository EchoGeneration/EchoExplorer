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

/**
 * Created by bmperez on 5/21/15.
 */
public abstract class Database<T> {

    // The tag that identifies this class. Used for debugging
    private static final String TAG = Database.class.getName();

    // The directory where database files are stored
    private static final String DATA_DIR = Environment.getDataDirectory().getAbsolutePath();
    private static final String DB_DIR = DATA_DIR + "/data/com.spencerbarton.echoexplorer/databases/";

    private final String mDatabaseName;             // Name of the database
    private final boolean mStaticDatabase;          // Whether the database is dynamic or static
    private final SQLiteDatabase mDatabase;         // Handle to the database connection
    private static final SQLiteDatabase.CursorFactory cursorFactory = null; // Cursor factory

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Database(Context context, String mDatabaseName, boolean mStaticDatabase) throws
        SQLiteException, IOException
    {
        this.mDatabaseName = mDatabaseName;
        this.mStaticDatabase = mStaticDatabase;
        if (mStaticDatabase) {
            this.mDatabase = openStaticDatabase(context, mDatabaseName);
        } else {
            this.mDatabase = openDatabase(context, mDatabaseName);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Cursor bufferedQuery(String query, String[] args)  {
        return mDatabase.rawQuery(query, args);
    }

    public T[] unbufferedQuery(String query, String[] args, Class<T> cls)
    {
        Cursor result = bufferedQuery(query, args);

        if (result.getCount() == 0) {
            return null;
        } else {
            return getAllEntries(result, cls);
        }
    }

    // Provides a convenient wrapper to access a column
    public static String getColumnByName(Cursor dbCursor, String colName) {
        return dbCursor.getString(dbCursor.getColumnIndex(colName));
    }

    public T[] getAllEntries(Cursor cursor, Class<T> cls)
    {
        T[] results = (T[]) Array.newInstance(cls, cursor.getCount());

        // Iterate over the entries, and collect them into an array
        int i = 0;
        while (!cursor.isAfterLast()) {
            results[i] = packCursorEntry(cursor);
            cursor.moveToNext();
            i++;
        }

        return results;
    }

    public abstract T packCursorEntry(Cursor cursor);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private SQLiteDatabase openStaticDatabase(Context context, String dbName) throws
        SQLiteException, IOException
    {
        Log.i(TAG, "Opening up database: " + dbName);
        String dbPath = DB_DIR + dbName;

        /* If the database does not exist, then copy it in from the assets folder. Make sure that
         * the parent directory exists, and create it if it does not exist.
         */
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            Log.i(TAG, "Database does not exist. Copying database in from the assets folder");

            File dbDirectory = new File(DB_DIR);
            if (!dbDirectory.exists()) {
                dbDirectory.mkdirs();
            }
        }

        // FIXME: Detect application version upgrade, and copy the database only then
        // else if (applicationUpdated(..))
        // For now, always copy the database in from the assets folder, so updates are seen
        copyDbFromAssets(context, dbName, dbPath);

        // Open the database
        return SQLiteDatabase.openDatabase(dbPath, cursorFactory, SQLiteDatabase.OPEN_READONLY);
    }

    private SQLiteDatabase openDatabase(Context context, String dbName) {
        Log.i(TAG, "Opening up database: " + dbName);
        String dbPath = DB_DIR + dbName;

        return SQLiteDatabase.openDatabase(dbPath, cursorFactory, SQLiteDatabase.OPEN_READWRITE);
    }

    private void copyDbFromAssets(Context context, String dbName, String destPath)
        throws IOException
    {
        InputStream source = openDbFromAssets(context, dbName);
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

    private InputStream openDbFromAssets(Context context, String dbName) throws IOException {
        return context.getAssets().open(dbName);
    }

}
