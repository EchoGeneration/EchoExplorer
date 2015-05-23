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
     * @throws IOException The database file is not writeable or readable, or the dbName does
     *                     does not exist in the assets folder.
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
     **/
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
     * @param dbCursor The Cursor object with the results of a query.
     * @param colName The name of the column to access.
     * @return The value of the given column.
     **/
    public static String getColumnByName(Cursor dbCursor, String colName) {
        return dbCursor.getString(dbCursor.getColumnIndex(colName));
    }

    /**
     * Given a Cursor object, retrieves all of the entries in the Cursor object and stores them
     * into a generic array of type T. This function retrieves all of the entries in the Cursor
     * object, regardless of its current position (the function moves to the first entry).
     *
     * @param cursor The Cursor object to retrieve the entries from.
     * @param cls The class object of the type T. Used to create an generic array of type T.
     * @return All of the entries in the Cursor object collected into the array.
     **/
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

    /**
     * The abstract method which takes a Cursor object from the result of query, and packs the
     * columns from a query result into the generic type T, which represents the schema of the
     * table. The subclasses must implement this method.
     *
     * @param cursor The Cursor object to pack an entry into type T.
     * @return The next entry in the cursor, with the columns of the result packed into the type T.
     **/
    public abstract T packCursorEntry(Cursor cursor);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Opens up the static, readonly database corresponding to dbName. If the database does not
     * exist on disk or the application has been updated, then it is retrieved from the assets
     * folder, copied to the database directory, and opened. Otherwise, the database is simply
     * opened.
     *
     * @param context The application's context. Used to access the assets folder of the
     *                application.
     * @param dbName The name of the database to open.
     * @return A handle to the SQLiteDatabase object representing the connection to the database.
     * @throws SQLiteException The database file is not properly formatted.
     * @throws IOException The database file is not writeable or readable, or the dbName does
     *                     does not exist in the assets folder.
     **/
    private SQLiteDatabase openStaticDatabase(Context context, String dbName) throws
            SQLiteException, IOException
    {
        Log.i(TAG, "Opening up database: " + dbName);
        String dbPath = DB_DIR + dbName;

        /* If the database does not exist or the application has been updated, then copy it in from
         * the assets folder of the application. Then, update the version file on disk, which tracks
         * the version of application this database corresponds to.
         */
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            Log.i(TAG, "Database does not exist. Copying database in from the assets folder");

            // Create the database directory if it does not exist
            File dbDirectory = new File(DB_DIR);
            if (!dbDirectory.exists()) {
                dbDirectory.mkdirs();
            }
            copyDbFromAssets(context, dbName, dbPath);
            writeVersionFile(mVersionPath);
        } else if (applicationUpdated(mVersionPath)) {
            copyDbFromAssets(context, dbName, dbPath);
            writeVersionFile(mVersionPath);
        }

        // Open the database
        return SQLiteDatabase.openDatabase(dbPath, mCursorFactory, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     * Opens up a dynamic, writeable database corresponding to dbName. If the database does not
     * exist on disk, then it is created.
     *
     * @param dbName The name of the database to open.
     * @return A handle to the SQLiteDatabase object representing the connection to the database.
     **/
    private SQLiteDatabase openDatabase(String dbName) {
        Log.i(TAG, "Opening up database: " + dbName);
        String dbPath = DB_DIR + dbName;

        return SQLiteDatabase.openDatabase(dbPath, mCursorFactory, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Copies the database corresponding to dbName from the assets folder to the destination path
     * destPath. If the destPath already exists, then it is overwritten with the file in the assets
     * folder of the application.
     *
     * @param context The application's context. Used to access the assets folder of the
     *                application.
     * @param dbName The name of the database to copy from the assets folder.
     * @param destPath The place to copy the database to.
     * @throws IOException The destination file is not writeable or readable, or the dbName does
     *                     does not exist in the assets folder.
     **/
    private static void copyDbFromAssets(Context context, String dbName, String destPath) throws
            IOException
    {
        // Open the database in assets and the destination path
        InputStream source = openDbFromAssets(context, dbName);
        OutputStream dest = new FileOutputStream(destPath);

        final int bufSize = 1024;
        byte[] buffer = new byte[bufSize];

        // Copy the database to the destination one segment at a time.
        int bytesRead = source.read(buffer);
        while (bytesRead != -1) {
            dest.write(buffer, 0, bufSize);
            bytesRead = source.read(buffer);
        }

        // Flush the destination file, and close both files
        dest.flush();
        dest.close();
        source.close();
    }


    /**
     * Opens the database in the assets folder corresponding to dbName.
     *
     * @param context The application's context. Used to access the assets folder of the
     *                application.
     * @param dbName The name of the database to open from the assets folder.
     * @return A handle to the open file corresponding to the dbName file in the assests folder.
     * @throws IOException The file dbName does not exist in the assets folder.
     */
    private static InputStream openDbFromAssets(Context context, String dbName) throws IOException {
        return context.getAssets().open(dbName);
    }

    /**
     * Checks if the application has been updated since the application was last run. The
     * version is kept track of on an on-disk file that simply holds the version code of the last
     * time the application was run. This is necessary because when the application is updated (or
     * reloaded from Android Studio), the database may have been updated. Without the version file,
     * we would not know when to update the database with a new one from the assets folder.
     *
     * @param versionPath The path to the version file, which tracks the version of the application
     *                    the last time it was run.
     * @return True if the application has been updated (or is new), false otherwise.
     * @throws FileNotFoundException This exception will never be thrown.
     */
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

    /**
     * Updates the version file on disk with the current version of the application.
     *
     * @param versionPath The path to the version file.
     * @throws IOException The version file is not readable or writeable.
     */
    private static void writeVersionFile(String versionPath) throws IOException
    {
        // Get the current application version.
        int curVersion = BuildConfig.VERSION_CODE;

        // Write the new version out to the file
        FileWriter versionWriter = new FileWriter(versionPath);
        versionWriter.write(Integer.toString(curVersion));
        versionWriter.close();
    }
}
