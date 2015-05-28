package com.spencerbarton.echoexplorer.database;

/**
 * The Lesson class represents the schema of the Lesson table. An instance of this class represents
 * a single row of the Lesson table, and contains all of the fields/columns in that table.
 *
 * Lessons consist of a unique identifier, the lesson number. This also establishes an ordering for
 * the list view used in the lessons menu. The lessons also consist of a name and description which
 * describe to the user the purpose of each lesson. These are also displayed in the list view in
 * lessons menu, as the title and body, respectively.
 *
 * There are two types of lessons: evaluation and tutorial lessons, which is encapsulated in the
 * type field. Tutorial lessons consist of simple examples that teach the user about a particular
 * type of echo (e.g. distance). Evaluation lessons consists of tests that verify the user's
 * understanding of a particular type of echo.
 *
 * @author Brandon Perez (bmperez)
 **/
public class Lesson {

    /** The tag that identifies this class. Used for debugging. */
    private static final String TAG = Lesson.class.getName();

    /** The value of the type field that indicates the lesson is a lesson of evaluations. */
    private static final String EVALUATION = "evaluation";
    /** The value of the type field that indicates the lesson is a lesson of tutorials. */
    private static final String TUTORIAL = "tutorial";

    /** Uniquely identifies the lesson. Also provides the ordering in the list view with respect
     * to other lessons. */
    public int lessonNumber;
    /** The name of the lesson, displayed in the list view. */
    public String name;
    /** The description of the lesson, displayed in the list view under the name. */
    public String description;
    /** The type of the lesson. One of {'evaluation', 'tutorial'}. Tutorial lessons contain only
     * tutorials, and evaluation lessons contains only evaluations (tests). */
    public String type;

    /**
     * Constructs a new Lesson object using the parameters passed in by the user.
     *
     * @param lessonNumber The lesson number of lesson.
     * @param name The name of the lesson.
     * @param description A short description about the lesson.
     * @param type The type of the lesson. Must be one of {'tutorial', 'evaluation'}
     **/
    public Lesson(int lessonNumber, String name, String description, String type)
    {
        this.lessonNumber = lessonNumber;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    /**
     * Determines if the Lesson object corresponds to an evaluation lesson.
     *
     * @return True if the Lesson object is an evaluation lesson, false otherwise.
     **/
    public boolean isEvaluation() {
        return type.equals(EVALUATION);
    }

    /**
     * Determines if the Lesson object corresponds to a tutorial lesson.
     *
     * @return True if the Lesson object is a tutorial lesson, false otherwise.
     **/
    public boolean isTutorial() {
        return type.equals(TUTORIAL);
    }

}

