package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.spencerbarton.echoexplorer.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Scanner;

/**
 * Created by bmperez on 5/21/15.
 */
public abstract class Database<T> {

    // The tag that identifies this class. Used for debugging
    private static final String TAG = Database.class.getName();

    // The directory where database files are stored
    private static final String DATA_DIR = Environment.getDataDirectory().getAbsolutePath();
    private static final String DB_DIR = DATA_DIR + "/data/com.spencerbarton.echoexplorer/databases/";

    // The file path that holds the previous version number, used to detect updates
    private static final String VERSION_FILE_BASE = "_version.txt";

    // Dynamic class members
    private final String mDatabaseName;             // Name of the database
    private final boolean mStaticDatabase;          // Whether the database is dynamic or static
    private final SQLiteDatabase mDatabase;         // Handle to the database connection
    private final String mVersionPath;              // The file path to the version of the DB
    private final SQLiteDatabase.CursorFactory mCursorFactory = null; // Cursor factory

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Database(Context context, String mDatabaseName, boolean mStaticDatabase) throws
            SQLiteException, IOException
    {
        this.mDatabaseName = mDatabaseName;
        this.mStaticDatabase = mStaticDatabase;
        this.mVersionPath = DB_DIR + mDatabaseName + VERSION_FILE_BASE;
        if (mStaticDatabase) {
            this.mDatabase = openStaticDatabase(context, mDatabaseName);
        } else {
            this.mDatabase = openDatabase(mDatabaseName);
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

        // Move to the first result in the cursor
        cursor.moveToFirst();

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
         * the parent directory exists, and create it if it does not exist. Otherwise, if the
         * application has been updated, then copy the new database in from the new package and
         * update the file on disk that tracks the current version.
         */
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            Log.i(TAG, "Database does not exist. Copying database in from the assets folder");

            File dbDirectory = new File(DB_DIR);
            if (!dbDirectory.exists()) {
                dbDirectory.mkdirs();
            }
            copyDbFromAssets(context, dbName, dbPath);
        } else if (applicationUpdated(mVersionPath)) {
            copyDbFromAssets(context, dbName, dbPath);
            writeVersionFile(mVersionPath);
        }

        // Open the database
        return SQLiteDatabase.openDatabase(dbPath, mCursorFactory, SQLiteDatabase.OPEN_READONLY);
    }

    private SQLiteDatabase openDatabase(String dbName) {
        Log.i(TAG, "Opening up database: " + dbName);
        String dbPath = DB_DIR + dbName;

        return SQLiteDatabase.openDatabase(dbPath, mCursorFactory, SQLiteDatabase.OPEN_READWRITE);
    }

    private static void copyDbFromAssets(Context context, String dbName, String destPath) throws
            IOException
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

    private static InputStream openDbFromAssets(Context context, String dbName) throws IOException {
        return context.getAssets().open(dbName);
    }

    private static boolean applicationUpdated(String versionPath) throws FileNotFoundException
    {
        File versionFile = new File(versionPath);

        // If the version file does not exist, then this is a new application, so it is updated
        if (!versionFile.exists()) {
            return true;
        }

        // Attempt to get the current version of the application
        int curVersion = BuildConfig.VERSION_CODE;

        // Read the application version that was last written to disk, and compare
        Scanner scanner = new Scanner(versionFile);
        int oldVersion = scanner.nextInt();

        return (oldVersion != curVersion);
    }

    private static void writeVersionFile(String versionPath) throws IOException
    {
        // Attempt to get the current version of the application
        int curVersion = BuildConfig.VERSION_CODE;

        // Write the new version out to the file
        FileWriter versionWriter = new FileWriter(versionPath);
        versionWriter.write(Integer.toString(curVersion));
        versionWriter.close();
    }
}
