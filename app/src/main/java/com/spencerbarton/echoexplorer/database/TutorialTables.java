package com.spencerbarton.echoexplorer.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class TutorialTables {

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

        public TutorialStepTable(SQLiteDatabase tutorialDb) {
            this.tutorialDb = tutorialDb;
        }

        public String getAudioFile(int tutorialId) {
            String query = "select ? from ? where ? = ?";

            // Query the database for the tutorial with tutorialId
            String[] args = {audioDirCol, tableName, tutorialIdCol, Integer.toString(tutorialId)};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);

            // The tutorial does not exist
            if (cursor == null) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(audioDirCol));
        }

        public TutorialStep getTutorialData(int tutorialId) {
            String query = "select * from ? where ? = ?";

            String[] args = {tableName, tutorialIdCol, Integer.toString(tutorialId)};
            Cursor cursor = this.tutorialDb.rawQuery(query, args);

            // THe tutorial does not exist
            if (cursor == null) {
                return null;
            }

            cursor.moveToFirst();
            int nextStep = Integer.parseInt(cursor.getString(cursor.getColumnIndex(nextStepCol)));
            return new TutorialStep(tutorialId, cursor.getString(cursor.getColumnIndex(audioDirCol)),
                cursor.getString(cursor.getColumnIndex(echoCol)),
                cursor.getString(cursor.getColumnIndex(textDirCol)),
                nextStep);
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
