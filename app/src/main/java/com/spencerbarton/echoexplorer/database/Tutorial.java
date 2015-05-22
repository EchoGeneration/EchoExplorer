package com.spencerbarton.echoexplorer.database;

/**
 * Created by bmperez on 5/21/15.
 */
public class Tutorial {

    public int lessonNumber;
    public int stepNumber;
    public String audioDirFile;
    public String echoFile;
    public String textDirections;

    public Tutorial(int lessonNumber, int stepNumber, String audioDirFile, String echoFile,
                    String textDirections)
    {
        this.lessonNumber = lessonNumber;
        this.stepNumber = stepNumber;
        this.audioDirFile = audioDirFile;
        this.echoFile = echoFile;
        this.textDirections = textDirections;
    }

}
