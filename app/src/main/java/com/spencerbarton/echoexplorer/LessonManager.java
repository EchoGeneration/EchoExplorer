package com.spencerbarton.echoexplorer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spencerbarton.echoexplorer.database.TutorialEvaluationsDb;
import com.spencerbarton.echoexplorer.database.TutorialEvaluationsDb.TutorialEvaluationsTable;
import com.spencerbarton.echoexplorer.database.TutorialEvaluationsDb.TutorialEvaluations;

import java.io.IOException;

/**
 * This object is intended to aid with moving between tutorials and evaluations in order to abstract
 *  this structure away from the activities
 *
 * TODO deal with invalid ids
 *
 * Created by Spencer on 2/28/2015.
 */
public class LessonManager implements LessonManagerStarter{

    public static final String EXTRA_LESSON_ID = "com.spencerbarton.echoexplorer.EXTRA_LESSON_ID";
    public static final String EXTRA_LESSON_NAME = "com.spencerbarton.echoexplorer.EXTRA_LESSON_NAME";
    private static final String TAG = "ActivityManager";
    private TutorialEvaluationsTable mTutorialEvaluationsTable;
    private Context mContext;
    private long mId;
    private TutorialEvaluations[] mLessons;

    public LessonManager(Context context) {
        this(context, -1);
    }

    public LessonManager(Context context, long activityId) {
        mContext = context;
        mId = activityId;

        // Load table data
        try {
            mTutorialEvaluationsTable = new TutorialEvaluationsTable(context);
            mLessons = mTutorialEvaluationsTable.getAllEntries();
        } catch (IOException e) {

            // Error so go home
            // TODO better idea
            Log.e(TAG, e.getMessage());
            goHome();
        }
    }

    public void goHome() {
        Intent intent = new Intent(mContext, TutorialsMenuActivity.class);
        intent.putExtra(EXTRA_LESSON_ID, mId);
        mContext.startActivity(intent);
    }

    public void goNext() {
        goToLesson(findNextLesson());
    }

    public void goPrev() {
        goToLesson(findPrevLesson());
    }

    public void goToId(long id) {
        int i = findLessonIndex();
        if (i < 0) {
            goHome();
        } else {
            goToLesson(mLessons[i]);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Helpers
    //----------------------------------------------------------------------------------------------

    // TODO need lesson id
    private int findLessonIndex() {
        for (int i = 0; i < mLessons.length; i++) {
            if (mLessons[i].id == mId) {
                return i;
            }
        }
        return -1;
    }

    private TutorialEvaluations findNextLesson() {
        int i = findLessonIndex();
        if ((0 <= i) && (i < (mLessons.length-1))) {
            return mLessons[i+1];
        }
        return null;
    }

    private TutorialEvaluations findPrevLesson() {
        int i = findLessonIndex();
        if (0 < i) {
            return mLessons[i-1];
        }
        return null;
    }

    private void goToLesson(TutorialEvaluations lesson) {
        if (lesson != null) {

            // Pick which activity type to instantiate
            Intent intent;
            if (lesson.name.equals(TutorialEvaluationsDb.TYPE_TUTORIAL)) {
                intent = new Intent(mContext, TutorialActivity.class);
            } else {
                intent = new Intent(mContext, EvaluationActivity.class);
            }

            // Add some activity info
            intent.putExtra(EXTRA_LESSON_ID, mId);
            intent.putExtra(EXTRA_LESSON_NAME, lesson.name);
            mContext.startActivity(intent);

        } else {
            goHome();
        }
    }

}

//----------------------------------------------------------------------------------------------
// Interface for non-lessons
//----------------------------------------------------------------------------------------------

interface LessonManagerStarter {
    public void goToId(long id);
}
