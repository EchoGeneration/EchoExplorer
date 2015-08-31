package com.spencerbarton.echoexplorer;

/**
 * Created by Susan on 8/25/15.
 */
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.spencerbarton.echoexplorer.database.UserStats;
import com.spencerbarton.echoexplorer.database.UserStatsTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecordsActivity extends ActionBarActivity {

    TableLayout table_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        Log.d("Reading: ", "Reading all contacts..");
//        List<UserStats> contacts = db.getAllResponse();
//
//        for (UserStats cn : contacts) {
//            String log = "Id: "+ Integer.toString(cn.getTimestamp())+" ,Name: " + cn.getResponse();
//            // Writing Contacts to log
//            Log.d("Name: ", log);
//        }
        table_layout = (TableLayout) findViewById(R.id.tableLayout1);
        UserStatsTable db = new UserStatsTable(this);
        List<UserStats> responses = db.getAllResponse();
        Cursor cursor = db.readEntry();
        int rows = db.getCount();
        int cols = db.getNumCol();
        cursor.moveToFirst();
        //set up the title of each column
        TableRow row = new TableRow(this);
        row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        for(int k = 0; k < cols; k++){
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(0, 5, 0, 5);
            if (k == 0) {tv.setText("Time");}
            else if(k == 1){tv.setText("Step");}
            else if(k == 2){tv.setText("Response");}
            row.addView(tv);
        }
        table_layout.addView(row);
        for (int i = 0; i < rows; i++) {

            row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            // inner for loop
            for (int j = 0; j < cols; j++) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                //tv.setBackgroundResource(R.drawable.cell_shape);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(18);
                tv.setPadding(0, 5, 0, 5);
                tv.setText(cursor.getString(j));
                row.addView(tv);
            }
            cursor.moveToNext();
            table_layout.addView(row);

        }
        cursor.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

