package dn.ute.shakya.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import dn.ute.shakya.common.Format;
import dn.ute.shakya.models.AnalysisItem;
import dn.ute.shakya.models.Word;

public class TableWord {
    Context context;
    MyDatabaseHelper myDatabaseHelper;

    public TableWord(Context context){
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
    }

    public void addWord(Word word){
        //Kiểm tra trùng
        if(getWordWithContent(word.getLessonId(), word.getContent()) != null) return;

        TableAnalysis tableAnalysis = new TableAnalysis(context);
        AnalysisItem analysisItem = tableAnalysis.getItemWithContent(Format.formatWord(word.getContent()));
        if(analysisItem == null) {
            analysisItem = new AnalysisItem(System.currentTimeMillis(), Format.formatWord(word.getContent()));
            tableAnalysis.addItem(analysisItem);
        }

        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_WORD_LESSON_ID, word.getLessonId());
        values.put(MyDatabaseHelper.COLUMN_WORD_CONTENT, Format.formatWord(word.getContent()));

        db.insert(MyDatabaseHelper.TABLE_WORD, null, values);

        db.close();
    }

    public Word getWord(long id){
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_WORD, new String[] { MyDatabaseHelper.COLUMN_WORD_ID,
                        MyDatabaseHelper.COLUMN_WORD_LESSON_ID, MyDatabaseHelper.COLUMN_WORD_CONTENT},
                MyDatabaseHelper.COLUMN_WORD_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return  null;
        }

        Word word = new Word(Long.parseLong(cursor.getString(0)), Long.parseLong(cursor.getString(1)), cursor.getString(2));

        return word;
    }

    public Word getWordWithContent(long lessonId, String content){
        content = Format.formatWord(content);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_WORD, new String[] { MyDatabaseHelper.COLUMN_WORD_ID,
                        MyDatabaseHelper.COLUMN_WORD_LESSON_ID, MyDatabaseHelper.COLUMN_WORD_CONTENT},
                MyDatabaseHelper.COLUMN_WORD_CONTENT + "=?" + " AND " + MyDatabaseHelper.COLUMN_WORD_LESSON_ID + "=?",
                new String[] { String.valueOf(content), String.valueOf(lessonId) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return  null;
        }

        Word word = new Word(Long.parseLong(cursor.getString(0)), Long.parseLong(cursor.getString(1)), cursor.getString(2));

        return word;
    }

    public List<Word> getWordsWithLessonId(long lessonId){
        List<Word> wordList = new ArrayList<Word>();
        String selectQuery = "SELECT  * FROM " + MyDatabaseHelper.TABLE_WORD
                            + " WHERE " + MyDatabaseHelper.COLUMN_WORD_LESSON_ID + "=" + lessonId;

        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Word word = new Word();
                word.setId(Long.parseLong(cursor.getString(0)));
                word.setLessonId(Long.parseLong(cursor.getString(1)));
                word.setContent(cursor.getString(2));

                wordList.add(word);
            } while (cursor.moveToNext());
        }

        return wordList;
    }

    public int getWordsCountWithLessonId(long lessonId){
        String countQuery = "SELECT  * FROM " + MyDatabaseHelper.TABLE_WORD
                        + " WHERE " + MyDatabaseHelper.COLUMN_WORD_LESSON_ID + "=" + lessonId;;
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateWord(Word word) {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_WORD_LESSON_ID, word.getLessonId());
        values.put(MyDatabaseHelper.COLUMN_WORD_CONTENT, Format.formatWord(word.getContent()));

        return db.update(MyDatabaseHelper.TABLE_WORD, values, MyDatabaseHelper.COLUMN_WORD_ID + " = ?",
                new String[]{String.valueOf(word.getId())});
    }

    public void deleteWord(Word word) {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        db.delete(MyDatabaseHelper.TABLE_WORD, MyDatabaseHelper.COLUMN_WORD_ID + " = ?",
                new String[] { String.valueOf(word.getId()) });
        db.close();
    }

    public void deleteWordWithLessonId(long lessonId) {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        db.delete(MyDatabaseHelper.TABLE_WORD, MyDatabaseHelper.COLUMN_WORD_LESSON_ID + " = ?",
                new String[] { String.valueOf(lessonId) });
        db.close();
    }
}
