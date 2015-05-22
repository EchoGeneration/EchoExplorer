package com.spencerbarton.echoexplorer.database;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmperez on 5/21/15.
 */
public class Evaluation {

    private static final String TAG = Evaluation.class.getName();

    public int lessonNumber;
    public int stepNumber;
    public String directionsAudioFile;
    public String echoAudioFile;
    public String textDirections;
    public List<String> responseOptions;
    public int correctResponse;

    public Evaluation(int lessonNumber, int stepNumber, String directionsAudioFile,
                      String echoAudioFile, String textDirections, String responseOptions,
                      int correctResponse)
    {
        this.lessonNumber = lessonNumber;
        this.stepNumber = stepNumber;
        this.directionsAudioFile = directionsAudioFile;
        this.echoAudioFile = echoAudioFile;
        this.textDirections = textDirections;
        this.responseOptions = parseResponseOptions(responseOptions);
        this.correctResponse = correctResponse;
    }

    private List<String> parseResponseOptions(String responseOptions) {
        List<String> options = new ArrayList<>();

        // Parse json
        try {
            JSONArray json = new JSONArray(responseOptions);
            Log.i(TAG, "Parsed " + json.toString());

            for (int i = 0; i < json.length(); i++) {
                options.add(json.getString(i));
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return options;
    }
}
