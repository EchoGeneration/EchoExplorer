package com.spencerbarton.echoexplorer.database;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * The Evaluation class represents the schema of the Evaluation table. An instance of this class
 * represents a single row of the Evaluation table, and contains all of the fields/columns of that
 * table.
 *
 * Evaluations consist of two numbers that uniquely identify them. The first is the lesson number,
 * which is the lesson that this particular evaluation belongs to. The second is the stepNumber,
 * which identifies which step of the lesson this evaluation is, providing an ordering with respect
 * to other evaluations.
 *
 * Evaluations consist of both audio and text directions, which indicate to the user what to expect.
 * They also contain a filename for the echo to use for the evaluation. As evaluations are tests,
 * they contain a set of responses to choose from, and which of those responses is the correct one.
 *
 * @author Brandon Perez (bmperez)
 **/
public class Evaluation {

    /** The tag that identifies this class. Used for debugging. */
    private static final String TAG = Evaluation.class.getName();

    /** The lesson number (id) of the lesson that this evaluation belongs to. */
    public int lessonNumber;
    /** The ordering with respect to other evaluations in the same lesson. */
    public int stepNumber;
    /** The name of the file used to provide audio directions for the evaluation. */
    public String directionsAudioFile;
    /** The name of the file that contains the echo for this evaluation. */
    public String echoAudioFile;
    /** The text directions to display on screen. */
    public String textDirections;
    /** The possible options that the user can choose from. */
    public List<String> responseOptions;
    /** The correct response from the responseOptions. */
    public int correctResponse;

    /**
     * Constructs a new Evaluation object using the parameters passed in by the user.
     *
     * @param lessonNumber The lesson number that the evaluation belongs to.
     * @param stepNumber The ordering of this evaluation with respect to others in the lesson.
     * @param directionsAudioFile The file to use for audio direction.
     * @param echoAudioFile The file to use for the echo.
     * @param textDirections The directions to display on screen for the evaluation.
     * @param responseOptions The responses that the user can choose from. This should be formatted
     *                        as a JSON array, able to be parsed.
     * @param correctResponse The correct response from response options.
     **/
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

    /**
     * Parses the provided responseOptions string as a JSON array, then converts this to a string
     * list with each possible response.
     *
     * @param responseOptions The responses that the user can choose from. This should be formatted
     *                        as a JSON array, able to be parsed.
     * @return A string list corresponding to possible options that the user can choose from.
     **/
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