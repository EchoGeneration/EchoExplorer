package com.spencerbarton.echoexplorer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class EvaluationActivity extends ActionBarActivity {

    private final static String TAG = "EvaluationActivity";
    private String mName = "";

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        setTitle(mName);
        return true;
    }

}
