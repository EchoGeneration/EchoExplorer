package com.spencerbarton.echoexplorer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
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

import com.spencerbarton.echoexplorer.database.EvaluationStepDb;
import com.spencerbarton.echoexplorer.database.TutorialStepTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO add btn listener
// TODO abstract class with other lesson types?
public class EvaluationActivity extends ActionBarActivity implements SwipeGestureDetector.SwipeGestureHandler{

    private final static String TAG = "EvaluationActivity";
    private SwipeGestureDetector mSwipeGestureDetector;
    private LessonManager mLessonManager;
    private String mEvaluationName = "";
    private List<EvaluationStepDb.EvaluationStep> mStepsData;
    private List<EvaluationStepManager> mStepManagers;
    private int mCurStep = 0;

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
            EvaluationStepDb.EvaluationStepTable evaluationStepTable =
                    new EvaluationStepDb.EvaluationStepTable(this);

            // Get sorted steps
            mStepsData = evaluationStepTable.getAllRows(lessonNumber);

        } catch (IOException e) {

            // Leave tutorial on data loading error
            Log.e(TAG, e.getMessage());
            mLessonManager.goHome();
        }

        onCreateAudio();

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
    public boolean onTouchEvent(MotionEvent event) {
        mSwipeGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //----------------------------------------------------------------------------------------------
    // Handlers
    //----------------------------------------------------------------------------------------------

    public void onEchoBtn(View view) {
        mStepManagers.get(mCurStep).handleEchoBtn();
    }

    @Override
    public void onSwipeRight() {
        goToNextStep();
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

        mCurStep--;

        // Go to prev tutorial because done with steps
        if (mCurStep < 0) {
            mLessonManager.goPrev();
        } else {
            mStepManagers.get(mCurStep).play();
        }
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

            mStepManagers = new ArrayList<>();
            for (EvaluationStepDb.EvaluationStep step : mStepsData) {
                mStepManagers.add(new EvaluationStepManager(step, mService));
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

        public EvaluationStepManager(EvaluationStepDb.EvaluationStep stepData, PlayAudioService service) {
            mTextDirections = stepData.textDirections;
            mChoices = stepData.responseOptions;
            mCorrectChoice = stepData.correctResponse;
            mAudioService = service;

            // Get resource ids
            mDirectionsAudioFile = EvaluationActivity.this.getResources().getIdentifier(
                    stepData.directionsAudioFile, "raw", EvaluationActivity.this.getPackageName());
            mEchoAudioFile = EvaluationActivity.this.getResources().getIdentifier(
                    stepData.echoAudioFile, "raw", EvaluationActivity.this.getPackageName());

        }

        public void play() {
            postDirections();
            playDirections(); // Goes into echo mode immediately after
        }

        private void postDirections() {
            TextView textView = (TextView) EvaluationActivity.this.findViewById(R.id.evaluation_directions);
            textView.setText(mTextDirections);
        }

        private void playDirections() {
            mAudioService.playAudio(mDirectionsAudioFile, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mDirectionsPlayed = true;
                }
            });
        }

        public void handleEchoBtn() {
            if (mDirectionsPlayed) {
                mAudioService.playAudio(mEchoAudioFile);
            }
        }

        // TODO load in choices, parse
        private void installChoiceBtns() {
            RadioGroup targetView = (RadioGroup) findViewById(R.id.eval_choice_btn_grp);

            for (int i = 0; i < mChoices.size(); i++ ) {
                String choice = mChoices.get(i);

                Button btn = new Button(EvaluationActivity.this);
                btn.setText(choice);

                // Set to fill parent
                ViewGroup.LayoutParams params = btn.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                btn.setLayoutParams(params);

                // Install btn
                targetView.addView(btn);

                // Add on click handler
                final int curChoice = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (curChoice == mCorrectChoice) {
                            toast(CORRECT_ANSWER);
                            goToNextStep();
                        } else {
                            toast(INCORRECT_ANSWER);
                        }
                    }
                });
            }
        }

        private void toast(String msg) {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

    }

}
