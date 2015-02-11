package com.spencerbarton.echoexplorer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TutorialActivity extends ActionBarActivity {

    private final static String TAG = "TutorialActivity";
    private String mName = "";

    //----------------------------------------------------------------------------------------------
    // Startup
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Extract info on which tutorial was instantiated
        Intent intent = getIntent();
        // TODO extract tutorial id and then look-up info in DB
        // TODO replace name with id
        mName = intent.getStringExtra(TutorialsMenuActivity.EXTRA_TUTORIAL_NAME);
        TextView tutTitle = (TextView) findViewById(R.id.tutorial_title);
        tutTitle.setText(mName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        Button title = (Button) menu.findItem(R.id.action_bar_title);
        title.setText(mName);
        return true;
    }

    //----------------------------------------------------------------------------------------------
    // Buttons
    //----------------------------------------------------------------------------------------------

    public void onEchoBtn(View view) {
        Log.i(TAG, "Echo btn clicked");
    }
}
