package com.spencerbarton.echoexplorer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.IOException;


public class TutorialStepDb {

    static class TutorialStepTable implements Databases.Packer<TutorialStep> {

        // The name of the database and table
        private static final String dbName = "TutorialDatabase";
        private static final String tableName = TutorialStep.class.getName();

        // The columns of the table
        private static final String _idCol = "_id";
        private static final String tutorialIdCol = "tutorialId";
        private static final String audioDirCol = "audioDirectionsFile";
        private static final String echoCol = "echoAudioFile";
        private static final String textDirCol = "textDirections";
        private static final String nextStepCol = "nextStep";

        // The database containing the tutorial step table
        private final SQLiteDatabase tutorialDb;

        // Constructor
        public TutorialStepTable(Context context)  throws SQLiteException, IOException {
            this.tutorialDb = Databases.StaticDatabase.openDatabase(context, dbName);
        }

        // Given a cursor, retrieves all the columns and packs them into a Tutorial Step structure
        public TutorialStep pack(Cursor cursor) {
            int tutorialId = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor, tutorialIdCol));
            String audioDir = Databases.CursorHelper.getColumnByName(cursor, audioDirCol);
            String echoFile = Databases.CursorHelper.getColumnByName(cursor, echoCol);
            String textDir = Databases.CursorHelper.getColumnByName(cursor, echoCol);
            int nextStep = Integer.parseInt(Databases.CursorHelper.getColumnByName(cursor, nextStepCol));

            return new TutorialStep(tutorialId, audioDir, echoFile, textDir, nextStep);
        }

        public String getAudioFile(int tutorialId) {
            String query = "select ? from ? where ? = ?";

            // Query the database for the tutorial with tutorialId
            String[] args = {audioDirCol, tableName, tutorialIdCol, Integer.toString(tutorialId)};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);

            // The cursor is empty, the tutorial does not exist
            if (!cursor.moveToFirst()) {
                return null;
            }

            return cursor.getString(cursor.getColumnIndex(audioDirCol));
        }

        public TutorialStep getTutorialData(int tutorialId) {
            String query = "select * from ? where ? = ?";

            String[] args = {tableName, tutorialIdCol, Integer.toString(tutorialId)};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);

            // The cursor is empty, the tutorial does not exist
            if (!cursor.moveToFirst()) {
                return null;
            }

            return this.pack(cursor);
        }

        // Gets a list of all the tutorial names, sorted
        public TutorialStep[] getTutorials() {
            String query = "select * from ? ORDER BY ? ASC";

            String[] args = {tableName, tutorialIdCol};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);

            // The cursor is empty, the table is empty
            if (!cursor.moveToFirst()) {
                return null;
            }

            int i = 0;
            TutorialStep[] entries = new TutorialStep[cursor.getCount()];

            return Databases.CursorHelper.getAllEntries(cursor, this);
        }
    }

    static class TutorialStep {

        int tutorialId;
        String audioDirectionsFile;
        String echoAudioFile;
        String textDirections;
        int nextStep;

        public TutorialStep(int tutorialId, String audioDirectionsFile, String echoAudioFile,
            String textDirections, int nextStep)
        {
            this.tutorialId = tutorialId;
            this.audioDirectionsFile = audioDirectionsFile;
            this.echoAudioFile = echoAudioFile;
            this.textDirections = textDirections;
            this.nextStep = nextStep;
        }

        // TODO: Define Getters and Setters?
    }
}
