package com.spencerbarton.echoexplorer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.spencerbarton.echoexplorer.database.Evaluation;
import com.spencerbarton.echoexplorer.database.EvaluationTable;
import com.spencerbarton.echoexplorer.database.UserStats;
import com.spencerbarton.echoexplorer.database.UserStatsTable;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

// TODO abstract class with other lesson types?
public class EvaluationActivity extends ActionBarActivity implements SwipeGestureDetector.SwipeGestureHandler {

    private final static String TAG = "EvaluationActivity";
    private SwipeGestureDetector mSwipeGestureDetector;
    private LessonManager mLessonManager;
    private String mEvaluationName = "";
    private Evaluation[] mStepsData;
    private List<EvaluationStepManager> mStepManagers;
    private int mCurStep = 0;
    private UserStatsTable db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        // Extract info on which tutorial was instantiated
        Intent intent = getIntent();
        mEvaluationName = intent.getStringExtra(LessonManager.EXTRA_LESSON_NAME);
        int lessonNumber = intent.getIntExtra(LessonManager.EXTRA_LESSON_NUMBER, -1);

        // Add gesture recognition
        mSwipeGestureDetector = new SwipeGestureDetector(this, this);

        // Add lesson movement
        mLessonManager = new LessonManager(this, lessonNumber);

        // Get tutorial steps
        try {
            EvaluationTable evaluationTable =
                    new EvaluationTable(this);

            // Get sorted steps
            mStepsData = evaluationTable.getAllRows(lessonNumber);

            if (mStepsData == null) {
                Log.e(TAG, "No steps for the evaluation");
                mLessonManager.goHome();
            }
        } catch (IOException e) {
            // Leave tutorial on data loading error
            Log.e(TAG, e.getMessage());
            mLessonManager.goHome();
        }

        onCreateAudio();
        db = new UserStatsTable(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onStartAudio();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopAudio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        setTitle(mEvaluationName);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mSwipeGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    //----------------------------------------------------------------------------------------------
    // Handlers
    //----------------------------------------------------------------------------------------------

    public void onEchoBtn(View view) {
        mStepManagers.get(mCurStep).handleEchoBtn();
    }

    @Override
    public void onSwipeRight() {
        goToPrevStep();
    }

    private void goToPrevStep() {
        mCurStep--;

        // Go to prev tutorial because done with steps
        if(mCurStep<0) {
            mLessonManager.goPrev();
        } else {
            mStepManagers.get(mCurStep).play();
        }
    }

    private void goToNextStep() {
        mCurStep++;

        // Go to next tutorial because done with steps
        if (mCurStep >= mStepManagers.size()) {
            mLessonManager.goNext();
        } else {
            mStepManagers.get(mCurStep).play();
        }
    }

    @Override
    public void onSwipeLeft() {
        goToNextStep();
    }

    @Override
    public void onSwipeUp() {

        // Do nothing
        Log.i(TAG, "UP");
    }

    @Override
    public void onSwipeDown() {
        mLessonManager.goHome();
    }

    public void onPrevBtn(View view) {
        goToPrevStep();
    }

    public void onNextBtn(View view) {
        goToNextStep();
    }

    //----------------------------------------------------------------------------------------------
    // PlayAudioService
    //----------------------------------------------------------------------------------------------

    // TODO put into separate audio manager
    private PlayAudioService mService;
    private boolean mIsBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayAudioService.PlayAudioBinder binder = (PlayAudioService.PlayAudioBinder) service;
            mService = binder.getService();
            mIsBound = true;
            initSteps();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBound = false;
        }
    };

    private void onCreateAudio() {
        // Setup audio volume controls, note only called once
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void onStartAudio() {

        // Bind audio service
        Intent intent = new Intent(this, PlayAudioService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void onStopAudio() {
        // Unbind audio service (will kill it as this is the only binding activity)
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    //----------------------------------------------------------------------------------------------
    // Step Object - handles all step specific activities
    //----------------------------------------------------------------------------------------------

    // TODO assumes audio binding follows DB callback
    public void initSteps() {

        // Requires bound audio service
        if (mIsBound) {

            // Randomize steps order
            Collections.shuffle(Arrays.asList(mStepsData));

            mStepManagers = new ArrayList<>();
            for (Evaluation step : mStepsData) {
                mStepManagers.add(new EvaluationStepManager(this, step, mService));
            }

            // Begin first step
            mStepManagers.get(0).play();
            mCurStep = 0;
        }
    }

    // TODO step manager interface
    class EvaluationStepManager {

        private static final String CORRECT_ANSWER = "Correct";
        private static final String INCORRECT_ANSWER = "Incorrect - try again";
        private int mDirectionsAudioFile;
        private int mEchoAudioFile;
        private String mTextDirections;
        private List<String> mChoices;
        private int mCorrectChoice;
        private boolean mDirectionsPlayed = false;
        private PlayAudioService mAudioService;
        private Context mContext;
        private int mCurStepTest;
        public EvaluationStepManager(Context context, Evaluation stepData, PlayAudioService service) {
            mContext = context;
            mTextDirections = stepData.textDirections;
            mChoices = stepData.responseOptions;
            mCorrectChoice = stepData.correctResponse;
            mAudioService = service;
            mCurStepTest = stepData.stepNumber;

            // Get resource ids
            mDirectionsAudioFile = EvaluationActivity.this.getResources().getIdentifier(
                    stepData.directionsAudioFile, "raw", EvaluationActivity.this.getPackageName());
            mEchoAudioFile = EvaluationActivity.this.getResources().getIdentifier(
                    stepData.echoAudioFile, "raw", EvaluationActivity.this.getPackageName());

        }

        public void play() {
            postDirections();
            installChoiceBtns();
            playDirections(); // Goes into echo mode immediately after
        }

        private void postDirections() {
            TextView textView = (TextView) EvaluationActivity.this.findViewById(R.id.evaluation_directions);
            textView.setText(mTextDirections);
        }

        private void playDirections() {
            if (audioDirEnabled() && !mDirectionsPlayed) {
                mAudioService.playAudio(mDirectionsAudioFile, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mDirectionsPlayed = true;
                    }
                });
            } else {
                mDirectionsPlayed = true;
                playEcho();
            }
        }

        public void handleEchoBtn() {
            playEcho();
        }

        private void playEcho() {
            if (mDirectionsPlayed) {
                mAudioService.playAudio(mEchoAudioFile);
            }
        }

        private void installChoiceBtns() {
            RadioGroup targetView = (RadioGroup) findViewById(R.id.eval_choice_btn_grp);
            targetView.removeAllViews(); // Remove current buttons

            for (int i = 0; i < mChoices.size(); i++ ) {
                String choice = mChoices.get(i);

                Button btn = new Button(mContext);

                // Install btn, NOTE important that this happen before setting params
                targetView.addView(btn);

                // Set text
                btn.setText(choice);

                // Set to fill parent
                ViewGroup.LayoutParams params = btn.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                btn.setLayoutParams(params);

                // Add on click handler
                final int curChoice = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDirectionsPlayed) {
                            int time = (int) (System.currentTimeMillis());
                            UserStats response = new UserStats(time,mCurStepTest,Integer.toString(curChoice));
                            db.add(response);
                            if (curChoice == mCorrectChoice) {
                                toast(CORRECT_ANSWER);
                                goToNextStep();
                            } else {
                                toast(INCORRECT_ANSWER);
                            }
                        }
                    }
                });
            }
        }

        private void toast(String msg) {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        private boolean audioDirEnabled() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            return sharedPref.getBoolean(SettingsActivity.KEY_PREF_AUDIO_DIR, false);
        }

    }

}
