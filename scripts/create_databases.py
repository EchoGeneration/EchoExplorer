#!/usr/bin/python

import sqlite3
from argparse import ArgumentParser

def parseArguments():
    parser = ArgumentParser(description="This is a script to create empty " +
        "tables with the appropiate schema for the Echo Explorer app. " +
        "These are the Lesson, TutorialStep, and EvaluationStep tables." +
        "If these tables already exist in the specified database, then " +
        "this script has no effect")
    parser.add_argument("db_path", type=str, metavar="$DB_PATH", help="This " +
        "is the path to the database file that we want to add the tables to. " +
        "If this file does not exist, then it is created.")
    args = parser.parse_args()

    return args.db_path

def createLessonTable(cursor):
    create_command = (
    """CREATE TABLE IF NOT EXISTS Lesson(
        _id             INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
        lessonNumber    INTEGER NOT NULL UNIQUE DEFAULT -1,
        name            TEXT NOT NULL,
        type            TEXT NOT NULL DEFAULT 'tutorial',

        CHECK (type = 'tutorial' OR type = 'evaluation'));
    """)

    cursor.execute(create_command)

def createTutorialStepTable(cursor):
    create_command = (
    """CREATE TABLE IF NOT EXISTS TutorialStep(
        _id                 INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
        lessonNumber        INTEGER NOT NULL,
        stepNumber          INTEGER NOT NULL DEFAULT -1,
        directionsAudioFile TEXT NOT NULL,
        echoAudioFile       TEXT NOT NULL,
        textDirections      TEXT NOT NULL,

        UNIQUE(lessonNumber, stepNumber),
        FOREIGN KEY(lessonNumber) REFERENCES Lesson(lessonNumber)
            ON DELETE CASCADE ON UPDATE CASCADE);
    """
    )

    cursor.execute(create_command)

def createEvaluationStepTable(cursor):
    create_command = (
    """CREATE TABLE IF NOT EXISTS EvaluationStep(
        _id                 INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
        lessonNumber        INTEGER NOT NULL,
        stepNumber          INTEGER NOT NULL DEFAULT -1,
        directionsAudioFile TEXT NOT NULL,
        echoAudioFile       TEXT NOT NULL,
        textDirections      TEXT NOT NULL,
        responseOptions     BLOB NOT NULL,
        correctResponse     INTEGER NOT NULL,

        UNIQUE(lessonNumber, stepNumber),
        FOREIGN KEY(lessonNumber) REFERENCES Lesson(lessonNumber)
            ON DELETE CASCADE ON UPDATE CASCADE);
        """)

    cursor.execute(create_command)

def main():
    db_path = parseArguments()

    db_conn = sqlite3.connect(db_path)
    cursor = db_conn.cursor()

    createLessonTable(cursor)
    createTutorialStepTable(cursor)
    createEvaluationStepTable(cursor)


if (__name__ == '__main__'):
    main()
