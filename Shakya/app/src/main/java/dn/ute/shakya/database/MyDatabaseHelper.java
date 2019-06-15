package dn.ute.shakya.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Shakya";

    public static final String TABLE_LESSON = "Lesson";
    public static final String COLUMN_LESSON_ID = "id";
    public static final String COLUMN_LESSON_TITLE = "title";

    public static final String TABLE_WORD = "Word";
    public static final String COLUMN_WORD_ID = "id";
    public static final String COLUMN_WORD_LESSON_ID = "lessonId";
    public static final String COLUMN_WORD_CONTENT = "content";

    public static final String TABLE_ANALYSIS = "Analysis";
    public static final String COLUMN_ANALYSIS_ID = "id";
    public static final String COLUMN_ANALYSIS_CONTENT = "content";
    public static final String COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_CORRECT = "numberOfTimeIsCorrect";
    public static final String COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_WRONG = "numberOfTimeIsWrong";


    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String scriptCreateTableLesson = "CREATE TABLE " + TABLE_LESSON
                + "(" + COLUMN_LESSON_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LESSON_TITLE + " TEXT" + ")";
        db.execSQL(scriptCreateTableLesson);

        String scriptCreateTableWord = "CREATE TABLE " + TABLE_WORD
                + "(" + COLUMN_WORD_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_WORD_LESSON_ID + " INTEGER,"
                + COLUMN_WORD_CONTENT + " TEXT" + ")";
        db.execSQL(scriptCreateTableWord);

        String scriptCreateTableAnalysis = "CREATE TABLE " + TABLE_ANALYSIS
                + "(" + COLUMN_ANALYSIS_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_ANALYSIS_CONTENT + " TEXT,"
                + COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_CORRECT + " INTEGER,"
                + COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_WRONG + " INTEGER" + ")";
        db.execSQL(scriptCreateTableAnalysis);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANALYSIS);

        // Recreate
        onCreate(db);
    }
}