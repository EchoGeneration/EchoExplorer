package com.spencerbarton.echoexplorer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.spencerbarton.echoexplorer.database.UserStats;
import com.spencerbarton.echoexplorer.database.UserStatsTable;

import java.lang.reflect.Array;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    //----------------------------------------------------------------------------------------------
    // State management
    //----------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserStatsTable db = new UserStatsTable(this);
        /**
         * CRUD Operations
         * */
        // Inserting Contacts
//        Log.d("Insert: ", "Inserting ..");
//        db.add(new UserStats(1, "9100000000"));
//        db.add(new UserStats(2, "9199999999"));
//        db.add(new UserStats(3, "9522222222"));
//        db.add(new UserStats(4, "9533333333"));

        // Reading all contacts

    }


    //----------------------------------------------------------------------------------------------
    // Activity bar
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.records:
                Intent intent = new Intent(this, RecordsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_settings:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------
    // Btn callbacks
    //----------------------------------------------------------------------------------------------

    public void startTutorialsActivity(View view) {
        Intent intent = new Intent(this, LessonsMenuActivity.class);
        startActivity(intent);
    }

}
