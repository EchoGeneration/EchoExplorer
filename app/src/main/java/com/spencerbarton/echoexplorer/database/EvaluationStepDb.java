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
        private static final String dbName = "TutorialDatabase";
        private static final String tableName = EvaluationStep.class.getName();

        // The columns of the table
        private static final String _idCol = "_id";
        private static final String evalIdCol = "evaluationId";
        private static final String audioDirCol = "directionsAudioFile";
        private static final String echoCol = "echoAudioFile";
        private static final String textDirCol = "textDirections";
        private static final String orderCol = "order";
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

            int evaluationId = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                evalIdCol));
            String audioDirFile = Databases.CursorHelper.getColumnByName(cursor, audioDirCol);
            String echoFile = Databases.CursorHelper.getColumnByName(cursor, echoCol);
            String textDir = Databases.CursorHelper.getColumnByName(cursor, textDirCol);
            int order = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor, orderCol));
            String responseOptions = Databases.CursorHelper.getColumnByName(cursor, responseOptCol);
            int correctResponse = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor,
                    correctResponseCol));

            return new EvaluationStep(evaluationId, audioDirFile, echoFile, textDir, order,
                responseOptions, correctResponse);
        }

        public EvaluationStep getEvaluationData(int evaluationId) {
            String query = "select * from ? where ? = ?";

            String[] args = {tableName, evalIdCol, Integer.toString(evaluationId)};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);

            // The cursor is empty, the tutorial does not exist
            if (!cursor.moveToFirst()) {
                return null;
            }

            return this.pack(cursor);
        }

        public EvaluationStep[] getEvaluations() {
            String query = "select * from ? ORDER BY ? ASC";

            String[] args = {tableName, evalIdCol};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);
            Log.e(tag + ".getEvaluations", "Querying for all evaluations");

            if (!cursor.moveToFirst()) {
                Log.e(tag + ".getEvaluations", "Querying result is empty!");
                return null;
            }

            return Databases.CursorHelper.getAllEntries(cursor, this);
        }
    }

    public static class EvaluationStep {

        int evaluationId;
        String directionsAudioFile;
        String echoAudioFile;
        String textDirections;
        int order;
        String[] responseOptions;
        int correctResponse;

        public EvaluationStep(int evaluationId, String directionsAudioFile, String echoAudioFile,
            String textDirections, int order, String responseOptions, int correctResponse)
        {
            this.evaluationId = evaluationId;
            this.directionsAudioFile = directionsAudioFile;
            this.echoAudioFile = echoAudioFile;
            this.textDirections = textDirections;
            this.order = order;
            this.responseOptions = responseOptions.split(" ");
            this.correctResponse = correctResponse;
        }
    }
}
