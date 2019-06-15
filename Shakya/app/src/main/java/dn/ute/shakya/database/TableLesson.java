package dn.ute.shakya.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dn.ute.shakya.common.Format;
import dn.ute.shakya.models.Lesson;

public class TableLesson {
    Context context;
    MyDatabaseHelper myDatabaseHelper;

    public TableLesson(Context context){
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
    }

    public void addLesson(Lesson lesson){
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_LESSON_TITLE, Format.formatWord(lesson.getTitle()));

        db.insert(MyDatabaseHelper.TABLE_LESSON, null, values);

        db.close();
    }

    public Lesson getLesson(long id){
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_LESSON, new String[] { MyDatabaseHelper.COLUMN_LESSON_ID,
                        MyDatabaseHelper.COLUMN_LESSON_TITLE}, MyDatabaseHelper.COLUMN_LESSON_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return  null;
        }

        Lesson lesson = new Lesson(Long.parseLong(cursor.getString(0)), cursor.getString(1));

        return lesson;
    }

    public Lesson getLessonWithTitle(String title){
        title = Format.formatWord(title);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_LESSON, new String[] { MyDatabaseHelper.COLUMN_LESSON_ID,
                        MyDatabaseHelper.COLUMN_LESSON_TITLE}, MyDatabaseHelper.COLUMN_LESSON_TITLE + "=?",
                new String[] { String.valueOf(title) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return  null;
        }

        Lesson lesson = new Lesson(Long.parseLong(cursor.getString(0)), cursor.getString(1));

        return lesson;
    }

    public List<Lesson> getAllLessons(){
        List<Lesson> lessonList = new ArrayList<Lesson>();
        String selectQuery = "SELECT  * FROM " + MyDatabaseHelper.TABLE_LESSON;

        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Lesson lesson = new Lesson();
                lesson.setId(Long.parseLong(cursor.getString(0)));
                lesson.setTitle(cursor.getString(1));

                lessonList.add(lesson);
            } while (cursor.moveToNext());
        }

        return lessonList;
    }

    public int getLessonsCount(){
        String countQuery = "SELECT  * FROM " + MyDatabaseHelper.TABLE_LESSON;
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateLesson(Lesson lesson) {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_LESSON_TITLE, Format.formatWord(lesson.getTitle()));

        return db.update(MyDatabaseHelper.TABLE_LESSON, values, MyDatabaseHelper.COLUMN_LESSON_ID + " = ?",
                new String[]{String.valueOf(lesson.getId())});
    }

    public void deleteLesson(Lesson lesson) {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        db.delete(MyDatabaseHelper.TABLE_LESSON, MyDatabaseHelper.COLUMN_LESSON_ID + " = ?",
                new String[] { String.valueOf(lesson.getId()) });
        db.close();
    }
}
