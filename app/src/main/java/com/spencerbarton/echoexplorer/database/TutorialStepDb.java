package com.spencerbarton.echoexplorer.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class TutorialStepDb {

    static class TutorialStepTable {

        // The name of the table
        private static final String tableName = TutorialStep.class.getName();

        // The columns of the table
        private static final String _idCol = "_id";
        private static final String tutorialIdCol = "tutorialId";
        private static final String audioDirCol = "audioDirectionsFile";
        private static final String echoCol = "echoAudioFile";
        private static final String textDirCol = "textDirections";
        private static final String nextStepCol = "nextStep";

        private final SQLiteDatabase tutorialDb;

        // Constructor
        public TutorialStepTable(SQLiteDatabase tutorialDb) {
            this.tutorialDb = tutorialDb;
        }

        // Given a cursor, retrieves all the columns and packs them into a Tutorial Step structure
        private static TutorialStep packTutorialStep(Cursor cursor) {
            int tutorialId = Integer.parseInt(Databases.getColumnByName(cursor, tutorialIdCol));
            String audioDir = Databases.getColumnByName(cursor, audioDirCol);
            String echoFile = Databases.getColumnByName(cursor, echoCol);
            String textDir = Databases.getColumnByName(cursor, echoCol);
            int nextStep = Integer.parseInt(Databases.getColumnByName(cursor, nextStepCol));

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

            return packTutorialStep(cursor);
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

            // Iterate over the entries, and collect them into an array
            while (!cursor.isAfterLast()) {
                entries[i] = packTutorialStep(cursor);
                cursor.moveToNext();
                i += 1;
            }

            return entries;
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
