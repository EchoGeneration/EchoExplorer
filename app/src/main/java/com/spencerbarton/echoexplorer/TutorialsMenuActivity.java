package com.spencerbarton.echoexplorer;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO add support for database object
// TODO support tutorial desciption
// TODO show tutorial completion/best score on eval

public class TutorialsMenuActivity extends ActionBarActivity {

    private static final String TAG = "TutorialsMenuActivity";
    public static final String EXTRA_TUTORIAL_NAME = "com.spencerbarton.echoexplorer.EXTRA_TUTORIAL_NAME";

    //----------------------------------------------------------------------------------------------
    // Startup
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials_menu);

        final ListView listview = (ListView) findViewById(R.id.tutorialListView);
        String[] values = new String[] { "Right/Left Sounds", "Right/Left Echos",
                "Right/Left 2nd Echo", "Moving Closer", "Moving Away"};
        final ArrayList<String> list = new ArrayList<String>();
        for(String s: values) {
            list.add(s);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Log.i(TAG, "pos: " + position + " id:" + id);
                // TODO start new activity
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------
    // ListView Adapter
    //----------------------------------------------------------------------------------------------

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
