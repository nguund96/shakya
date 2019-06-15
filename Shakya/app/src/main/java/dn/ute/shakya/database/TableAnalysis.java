package dn.ute.shakya.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import dn.ute.shakya.common.Format;
import dn.ute.shakya.models.AnalysisItem;

public class TableAnalysis {
    Context context;
    MyDatabaseHelper myDatabaseHelper;

    public TableAnalysis(Context context){
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
    }

    public void addItem(AnalysisItem analysisItem){
        //Kiểm tra trùng
        if(getItemWithContent(analysisItem.getContent()) != null) return;

        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_ANALYSIS_CONTENT, Format.formatWord(analysisItem.getContent()));
        values.put(MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_CORRECT, analysisItem.getNumberOfTimeIsCorrect());
        values.put(MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_WRONG, analysisItem.getNumberOfTimeIsWrong());

        db.insert(MyDatabaseHelper.TABLE_ANALYSIS, null, values);

        db.close();
    }

    public AnalysisItem getItemWithContent(String content){
        content = Format.formatWord(content);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_ANALYSIS, new String[] { MyDatabaseHelper.COLUMN_ANALYSIS_ID,
                        MyDatabaseHelper.COLUMN_ANALYSIS_CONTENT, MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_CORRECT,
                        MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_WRONG},
                MyDatabaseHelper.COLUMN_ANALYSIS_CONTENT + "=?",
                new String[] { String.valueOf(content)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return  null;
        }

        AnalysisItem analysisItem = new AnalysisItem(Long.parseLong(cursor.getString(0)), cursor.getString(1),
                Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)));

        return analysisItem;
    }

    public AnalysisItem getItem(long id){
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_ANALYSIS, new String[] { MyDatabaseHelper.COLUMN_ANALYSIS_ID,
                        MyDatabaseHelper.COLUMN_ANALYSIS_CONTENT, MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_CORRECT,
                        MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_WRONG},
                MyDatabaseHelper.COLUMN_ANALYSIS_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) return  null;
        }

        AnalysisItem analysisItem = new AnalysisItem(Long.parseLong(cursor.getString(0)), cursor.getString(1),
                Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)));

        return analysisItem;
    }

    public List<AnalysisItem> getAllItem(){
        List<AnalysisItem> analysisItems = new ArrayList<AnalysisItem>();
        String selectQuery = "SELECT  * FROM " + MyDatabaseHelper.TABLE_ANALYSIS;

        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AnalysisItem analysisItem = new AnalysisItem(Long.parseLong(cursor.getString(0)), cursor.getString(1),
                        Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)));

                analysisItems.add(analysisItem);
            } while (cursor.moveToNext());
        }

        return analysisItems;
    }

    public List<AnalysisItem> getItemWithWrongRate(float percent){
        List<AnalysisItem> analysisItems = new ArrayList<AnalysisItem>();
        String selectQuery = "SELECT  * FROM " + MyDatabaseHelper.TABLE_ANALYSIS
                    + " WHERE " + MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_WRONG + " >= " + (percent/10);

        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AnalysisItem analysisItem = new AnalysisItem(Long.parseLong(cursor.getString(0)), cursor.getString(1),
                        Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)));
                analysisItems.add(analysisItem);
            } while (cursor.moveToNext());
        }

        return analysisItems;
    }

    public int getCount(){
        String countQuery = "SELECT  * FROM " + MyDatabaseHelper.TABLE_ANALYSIS;
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateItem(AnalysisItem analysisItem) {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_ANALYSIS_CONTENT, Format.formatWord(analysisItem.getContent()));
        values.put(MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_CORRECT, analysisItem.getNumberOfTimeIsCorrect());
        values.put(MyDatabaseHelper.COLUMN_ANALYSIS_NUMBER_OF_TIME_IS_WRONG, analysisItem.getNumberOfTimeIsWrong());

        return db.update(MyDatabaseHelper.TABLE_ANALYSIS, values, MyDatabaseHelper.COLUMN_ANALYSIS_ID + " = ?",
                new String[]{String.valueOf(analysisItem.getId())});
    }

    public void deleteItem(AnalysisItem analysisItem) {
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        db.delete(MyDatabaseHelper.TABLE_ANALYSIS, MyDatabaseHelper.COLUMN_ANALYSIS_ID + " = ?",
                new String[] { String.valueOf(analysisItem.getId()) });
        db.close();
    }
}
