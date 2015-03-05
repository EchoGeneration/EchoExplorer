package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/* This class encapsulates the functionality for making queries on the TutorialStep table. The
 * Tutorial Step table contains metadata about a step of a tutorial lesson. A tutorial lesson is
 * a sequence of these steps. It contains the number of the lesson it belongs to, which is used to
 * search the table based on the lesson. It contains the step number, which provides a relative
 * ordering to other steps in the same lesson. It also contains the file that contains the audio
 * for directions for the step, the audio file that contains the echo for the step, and the
 * text version of the directions.
 */
public class TutorialStepTable {

    public static class TutorialStepTableHelp implements Databases.Packer<TutorialStep> {

        // A tag for logging messages
        private static final String tag = TutorialStepTableHelp.class.getName();

        // The name of the database and table
        private static final String dbName = "LessonDatabase";
        private static final String tableName = TutorialStep.class.getName();

        // The columns of the table
        private static final String _idCol = "_id";
        private static final String lessonNumberCol = "lessonNumber";
        private static final String stepNumberCol = "stepNumber";
        private static final String audioDirCol = "directionsAudioFile";
        private static final String echoCol = "echoAudioFile";
        private static final String textDirectionsCol = "textDirections";

        // The database containing the tutorial step table
        private final SQLiteDatabase tutorialDb;

        // Constructor
        public TutorialStepTableHelp(Context context)  throws SQLiteException, IOException {
            this.tutorialDb = Databases.StaticDatabase.openDatabase(context, dbName);
        }

        // Given a cursor, retrieves all the columns and packs them into a Tutorial Step structure
        public TutorialStep pack(Cursor cursor) {
            int lessonNumber = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                lessonNumberCol));
            int stepNumber = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                stepNumberCol));
            String audioDirFile = Databases.CursorHelper.getColumnByName(cursor, audioDirCol);
            String echoFile = Databases.CursorHelper.getColumnByName(cursor, echoCol);
            String textDirections = Databases.CursorHelper.getColumnByName(cursor, textDirectionsCol);

            return new TutorialStep(lessonNumber, stepNumber, audioDirFile, echoFile, textDirections);
        }

        // Given a lesson number and a step, retrieves a given step of a tutorial
        public TutorialStep getRow(int lessonNumber, int stepNumber) {
            String query = "select * from ? where ? = ? and ? = ?";

            String[] args = {tableName, lessonNumberCol, Integer.toString(lessonNumber),
                stepNumberCol, Integer.toString(stepNumber)};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);
            Log.e(tag+".getRow", "Querying for row with lessonId=" + Integer.toString(lessonNumber)+
                " and stepNumber=" + Integer.toString(stepNumber));

            // The cursor is empty, then the tutorial does not exist
            if (!cursor.moveToFirst()) {
                Log.e(tag+".getRow", "Query result is empty!");
                return null;
            }

            return this.pack(cursor);
        }

        // Gets a list of all the tutorials, sorted by lessonId, then by step number
        public List<TutorialStep> getAllRows() {
            String query = "select * from ? order by ? asc, ? asc";

            String[] args = {tableName, lessonNumberCol, stepNumberCol};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);
            Log.e(tag+".getTutorials", "Querying for all tutorial steps");

            // The cursor is empty, the table is empty
            if (!cursor.moveToFirst()) {
                Log.e(tag+".getTutorials", "Query result is empty!");
                return null;
            }

            return Databases.CursorHelper.getAllEntries(cursor, this);
        }

        // Given a lesson number, returns the tutorials in order of their steps
        public List<TutorialStep> getAllRows(int lessonNumber) {

            /*
            String query = "select * from ? where ? = ? order by ? asc";

            String[] args = {tableName, lessonNumberCol, Integer.toString(lessonNumber),
                stepNumberCol};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);
            */
            // TODO Query bug
            String query = "select * from TutorialStep where lessonNumber = "
                    + Integer.toString(lessonNumber) + " order by stepNumber asc";
            Cursor cursor = this.tutorialDb.rawQuery(query, null);

            Log.i(tag+".getTutorials", "Querying for all steps of tutorial " +
                Integer.toString(lessonNumber));

            // The cursor is empty, the table is empty
            if (!cursor.moveToFirst()) {
                Log.e(tag+".getTutorials", "Query result is empty!");
                return null;
            }

            return Databases.CursorHelper.getAllEntries(cursor, this);
        }
    }

    public static class TutorialStep {

        public int lessonNumber;
        public int stepNumber;
        public String audioDirFile;
        public String echoFile;
        public String textDirections;

        public TutorialStep(int lessonNumber, int stepNumber, String audioDirFile, String echoFile,
            String textDirections)
        {
            this.lessonNumber = lessonNumber;
            this.stepNumber = stepNumber;
            this.audioDirFile = audioDirFile;
            this.echoFile = echoFile;
            this.textDirections = textDirections;
        }

        // TODO: Define Getters and Setters?
    }
}
