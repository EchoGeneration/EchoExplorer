package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;

/**
 * Created by bmperez on 2/27/15.
 */
public class EvaluationStepDb {

    public static class EvaluationStepTable implements Databases.Packer<EvaluationStep> {

        // Tag for debugging
        private static final String tag = EvaluationStepTable.class.getName();

        // The name of the database and table
        private static final String dbName = "LessonDatabase";
        private static final String tableName = EvaluationStep.class.getName();

        // The columns of the table
        private static final String _idCol = "_id";
        private static final String lessonNumberCol = "lessonNumber";
        private static final String stepNumberCol = "stepNumber";
        private static final String audioDirCol = "directionsAudioFile";
        private static final String echoCol = "echoAudioFile";
        private static final String textDirCol = "textDirections";
        private static final String responseOptCol = "responseOptions";
        private static final String correctResponseCol = "correctResponse";

        // The database containing the tutorial step table
        private final SQLiteDatabase tutorialDb;

        // Constructor
        public EvaluationStepTable(Context context) throws SQLiteException, IOException {
            this.tutorialDb = Databases.StaticDatabase.openDatabase(context, dbName);
        }

        // Given a cursor, retrieves all the columns and packs them into a Tutorial Step structure
        public EvaluationStep pack(Cursor cursor) {

            int lessonNumber = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                    lessonNumberCol));
            int stepNumber = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                    stepNumberCol));
            String audioDirFile = Databases.CursorHelper.getColumnByName(cursor, audioDirCol);
            String echoFile = Databases.CursorHelper.getColumnByName(cursor, echoCol);
            String textDir = Databases.CursorHelper.getColumnByName(cursor, textDirCol);
            String responseOptions = Databases.CursorHelper.getColumnByName(cursor, responseOptCol);
            int correctResponse = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                    correctResponseCol));

            return new EvaluationStep(lessonNumber, stepNumber, audioDirFile, echoFile, textDir,
                responseOptions, correctResponse);
        }

        // Given a lesson number and a step, retrieves a given step of a tutorial
        public EvaluationStep getRow(int lessonNumber, int stepNumber) {
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
        public EvaluationStep[] getAllRows() {
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
        public EvaluationStep[] getAllRows(int lessonNumber) {
            String query = "select * from ? where ? = ? order by ? asc";

            String[] args = {tableName, lessonNumberCol, Integer.toString(lessonNumber),
                    stepNumberCol};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);
            Log.e(tag+".getTutorials", "Querying for all steps of tutorial " +
                    Integer.toString(lessonNumber));

            // The cursor is empty, the table is empty
            if (!cursor.moveToFirst()) {
                Log.e(tag+".getTutorials", "Query result is empty!");
                return null;
            }

            return Databases.CursorHelper.getAllEntries(cursor, this);
        }
    }

    public static class EvaluationStep {

        public int lessonNumber;
        public int stepNumber;
        public String directionsAudioFile;
        public String echoAudioFile;
        public String textDirections;
        public String responseOptions;
        public int correctResponse;

        public EvaluationStep(int lessonNumber, int stepNumber, String directionsAudioFile,
            String echoAudioFile, String textDirections, String responseOptions,
            int correctResponse)
        {
            this.lessonNumber = lessonNumber;
            this.stepNumber = stepNumber;
            this.directionsAudioFile = directionsAudioFile;
            this.echoAudioFile = echoAudioFile;
            this.textDirections = textDirections;
            this.responseOptions = responseOptions;
            this.correctResponse = correctResponse;
        }
    }
}
