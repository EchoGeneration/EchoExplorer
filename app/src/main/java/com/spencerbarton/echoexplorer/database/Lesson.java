package com.spencerbarton.echoexplorer.database;

/* This class is a structure that represents the data contained in a single row of the Lesson
 * database.
 */
public class Lesson {
    private static final String EVALUTATION = "evaluation";
    private static final String TUTORIAL = "tutorial";

    // The columns in a row of the Lesson database
    public int lessonNumber;
    public String name;
    public String type;
    public String description;

    // The constructor method
    public Lesson(int lessonNumber, String name, String type, String description)
    {
        this.lessonNumber = lessonNumber;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public boolean isEvaluation() {
        return type.equals(EVALUTATION);
    }

    public boolean isTutorial() {
        return type.equals(TUTORIAL);
    }

}

