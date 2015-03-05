package com.spencerbarton.echoexplorer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.spencerbarton.echoexplorer.database.LessonTable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// TODO add support for database object
// TODO support tutorial desciption
// TODO show tutorial completion/best score on eval
// TODO checker to ensure all tutorial audio is present

public class LessonsMenuActivity extends ActionBarActivity {

    private static final String TAG = "LessonsMenuActivity";
    private static final int EVALUATION_COLOR = R.color.gray8;
    private static final int TUTORIAL_COLOR = R.color.gray7;

    //----------------------------------------------------------------------------------------------
    // Startup
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Populate layout and defaults
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_menu);

        try {

            // Load all tutorials and evaluation names
            LessonTable.LessonTableHelp lessonTableHelp = new LessonTable.LessonTableHelp(this);
            List<LessonTable.Lesson> lessons = lessonTableHelp.getAllRows();
            populateListView(lessons);

        } catch (IOException e) {

            // Error so go back
            Log.e(TAG, e.getMessage());
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------
    // ListView Details
    //----------------------------------------------------------------------------------------------

    private void populateListView(List<LessonTable.Lesson> lessons) {

        final LessonAdapter adapter = new LessonAdapter(this, lessons);

        final ListView listview = (ListView)findViewById(R.id.tutorialListView);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                // Get textview text
                TextView textView = (TextView) view;
                String text = textView.getText().toString();

                Log.i(TAG, "Tut clicked pos: " + position + " id:" + id + " name: " + text);

                // Create manager to handle loading lesson
                LessonManagerStarter starter = new LessonManager(LessonsMenuActivity.this);
                starter.goToLesson((int)id); // Can cast because was initially an int
            }

        });
    }


    private class LessonAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private final List<LessonTable.Lesson> mLessons;

        public LessonAdapter(Context context, final List<LessonTable.Lesson> lessons) {
            mInflater = LayoutInflater.from(context);
            mLessons = lessons;
        }

        @Override
        public int getCount() {
            return mLessons.size();
        }

        @Override
        public Object getItem(int position) {
            return mLessons.get(position);
        }

        @Override
        public long getItemId(int position) {
            LessonTable.Lesson lesson = (LessonTable.Lesson) getItem(position);
            return lesson.lessonNumber;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            // Create new
            if (convertView == null) {
                view = mInflater.inflate(R.layout.row_lesson_layout, parent, false);
                TextView textView = (TextView) view.findViewById(R.id.row_lesson_name);

                // Case on type of lesson
                LessonTable.Lesson lesson = mLessons.get(position);
                if (lesson.isTutorial()) {
                    textView.setTextColor(TUTORIAL_COLOR);
                } else {
                    textView.setTextColor(EVALUATION_COLOR);
                }
                textView.setText(lesson.name);
            } else {
                view = convertView;
            }

            return view;
        }

    }

}
