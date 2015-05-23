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
 * Database is an abstract base class for all database objects that implements basic functionality
 * needed to interact with an SQLite database. This class is inherited by table subclasses, which
 * organize the queries to the database, and provide the schema definition via packCursorEntry(),
 * which defines how to extra columns from a cursor object. The user also provides the definition
 * of a object representing a row of the database via the type paraemter T.
 *
 * The database classes handles two types of database static (read-only) databases, and dynamic
 * databases. This class performs the operations necessary to access a static database that is
 * provided with the application in the assets folder. It also can handle dynamic database, which
 * require creating databases in the appropriate directory.
 *
 * The database object is completely opaque, and contains all of the information necessary for
 * accessing a database: the database name, whether or not it is static, and a handle to the
 * SQLiteDatabase object that represents the connection to the database.
 *
 * @author Brandon Perez (bmperez)
 **/
public abstract class Database<T> {

    // The tag that identifies this class. Used for debugging
    private static final String TAG = Database.class.getName();

    // The directory where database files are stored
    private static final String DATA_DIR = Environment.getDataDirectory().getAbsolutePath();
    private static final String DB_DIR = DATA_DIR +
                                         "/data/com.spencerbarton.echoexplorer/databases/";

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

    /**
     * Constructs a new database object using the application's context and the given database
     * name. If the database is static, then it will be copied from the assets folder if it does
     * not exist. Otherwise, the database will be created on disk if it does not exist.
     *
     * @param context The application's context. Used to access the assets folder of the
     *                application.
     * @param databaseName The name of the database file (not the full path, only the basename).
     *                     This is used to access the assets folder and the local storage.
     * @param staticDatabase Indicates whether or not this database is static.
     * @throws SQLiteException The database file is not properly formatted.
     * @throws IOException The database file is not writeable or readable.
     **/
    public Database(Context context, String databaseName, boolean staticDatabase) throws
            SQLiteException, IOException
    {
        mDatabaseName = databaseName;
        mStaticDatabase = staticDatabase;
        mVersionPath = DB_DIR + mDatabaseName + VERSION_FILE_BASE;

        if (mStaticDatabase) {
            this.mDatabase = openStaticDatabase(context, mDatabaseName);
        } else {
            this.mDatabase = openDatabase(mDatabaseName);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs the specified query on the database, and buffers the result of the query in a Cursor
     * object, which will pull the results from disk as needed. Returns a handle to this Cursor
     * object.
     *
     * @param query The SQL query string.
     * @param args Arguments passed into the 'WHERE' clause of the query. Each argument must match
     *             with a corresponding '?' in query.
     * @return A handle to a cursor object representing the result of the query.
     **/
    public Cursor bufferedQuery(String query, String[] args)  {
        return mDatabase.rawQuery(query, args);
    }

    /**
     * Runs the specified query on the database, and returns the results of the query in an array
     * of the appropriate type representing a row of the table. If the query returns no results,
     * then null is returned.
     *
     * @param query The SQL query string.
     * @param args Arguments passed into the 'WHERE' clause of the query. Each argument must match
     *             with a corresponding '?' in query.
     * @param cls The class object of the type T. Used to create an generic array of type T.
     * @return The results of the query in an array, or null if the query returns no results.
     */
    public T[] unbufferedQuery(String query, String[] args, Class<T> cls)
    {
        Cursor result = bufferedQuery(query, args);

        if (result.getCount() == 0) {
            return null;
        } else {
            return getAllEntries(result, cls);
        }
    }

    /**
     * Provides a convenient wrapper for a Cursor object to access the contents of a column by its
     * name. Given a cursor object, and a column name, returns the value stored in that column of
     * the current row pointed to by the cursor.
     *
     * @param dbCursor The cursor object with the results of a query.
     * @param colName The name of the column to access.
     * @return The value of the given column.
     */
    // Provides a convenient wrapper to access a column
    public static String getColumnByName(Cursor dbCursor, String colName) {
        return dbCursor.getString(dbCursor.getColumnIndex(colName));
    }

    /**
     * Given a Cursor object, retrieves all of the entries in the Cursor object and stores them
     * into a generic array of type T.
     *
     * @param cursor The Cursor object to retrieve the entries from.
     * @param cls The class object of the type T. Used to create an generic array of type T.
     * @return All of the entries in the Cursor object collected into the array.
     */
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
