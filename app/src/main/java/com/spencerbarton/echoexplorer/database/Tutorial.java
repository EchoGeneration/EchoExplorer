package com.spencerbarton.echoexplorer.database;

/**
 * The Tutorial class represents the schema of the Tutorial table. An instance of this class
 * represents a single row of the Tutorial table, and contains all of the fields/columns of that
 * table.
 *
 * Tutorials consist of two numbers that uniquely identify them. The first is the lesson number,
 * which is the lesson that this particular tutorial belongs to. The second is the stepNumber,
 * which identifies which step of the lesson this evaluation is, providing an ordering with respect
 * to other tutorials.
 *
 * Tutorials consist of both audio and text directions, which indicate to the user what to expect.
 * They also contain a filename for the echo to use for the tutorial.
 *
 * @author Brandon Perez (bmperez)
 **/
public class Tutorial {

    /** The tag that identifies this class. Used for debugging. */
    private static final String TAG = Tutorial.class.getName();

    /** The lesson number (id) of the lesson that this tutorial belongs to. */
    public int lessonNumber;
    /** The ordering with respect to other tutorials in the same lesson. */
    public int stepNumber;
    /** The name of the file used to provide audio directions for the evaluation. */
    public String audioDirFile;
    /** The name of the file that contains the echo for this evaluation. */
    public String echoFile;
    /** The text directions to display on screen. */
    public String textDirections;

    /**
     * Constructs a new Tutorial object using the parameters passed in by the user.
     *
     * @param lessonNumber The lesson number that the tutorial belongs to.
     * @param stepNumber The ordering of this tutorial with respect to others in the lesson.
     * @param audioDirFile The file to use for audio direction.
     * @param echoFile The file to use for the echo.
     * @param textDirections The directions to display on screen for the evaluation.
     **/
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
