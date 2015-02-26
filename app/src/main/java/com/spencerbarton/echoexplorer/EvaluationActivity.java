package com.spencerbarton.echoexplorer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

// TODO add btn listener
public class EvaluationActivity extends ActionBarActivity {

    private final static String TAG = "EvaluationActivity";
    private String mName = "";
    private String[] mChoices = {"True", "False"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        // Extract info on which tutorial was instantiated
        Intent intent = getIntent();
        // TODO extract tutorial id and then look-up info in DB
        // TODO replace name with id
        mName = intent.getStringExtra(TutorialsMenuActivity.EXTRA_TUTORIAL_NAME);
        long id = intent.getLongExtra(TutorialsMenuActivity.EXTRA_TUTORIAL_ID, 0);
        Log.i(TAG, "Id: " + id + ", Name: " + mName);

        // Install choice buttons
        installChoiceBtns(mChoices);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        setTitle(mName);
        return true;
    }

    private void installChoiceBtns(String[] choices) {
        RadioGroup targetView = (RadioGroup) findViewById(R.id.eval_choice_btn_grp);

        for (String choice : choices) {
            Button btn = new Button(this);
            btn.setText(choice);

            // Set to fill parent
            ViewGroup.LayoutParams params = btn.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            btn.setLayoutParams(params);

            // Install btn
            targetView.addView(btn);

            // TODO handler
        }
    }

    //----------------------------------------------------------------------------------------------
    // Handlers
    //----------------------------------------------------------------------------------------------

    public void onEchoBtn(View view) {
    }
}
