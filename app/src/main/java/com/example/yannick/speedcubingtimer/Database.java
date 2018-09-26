package com.example.yannick.speedcubingtimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    private static final String TABLE_NAME = "times_table";
    private static final int TABLE_VERSION = 2;
    static final String COL0 = "puzzle_id";
    static final String COL1 = "minutes";
    static final String COL2 = "seconds";
    static final String COL3 = "milliseconds";
    static final String COL4 = "day";
    static final String COL5 = "month";
    static final String COL6 = "year";

    public Database(Context context){
        super(context, TABLE_NAME, null, TABLE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL0 +" INTEGER, " +
                COL1 + " INTEGER, " +
                COL2 + " INTEGER, " +
                COL3 + " INTEGER, " +
                COL4 + " INTEGER, " +
                COL5 + " INTEGER, " +
                COL6 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    boolean addData(TimeObject time){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, String.valueOf(time.getPuzzleID()));
        contentValues.put(COL1, String.valueOf(time.getMinutes()));
        contentValues.put(COL2, String.valueOf(time.getSeconds()));
        contentValues.put(COL3, String.valueOf(time.getMilliseconds()));
        contentValues.put(COL4, String.valueOf(time.getDay()));
        contentValues.put(COL5, String.valueOf(time.getMonth()));
        contentValues.put(COL6, String.valueOf(time.getYear()));
        Log.d(TAG, "addData: Adding " + time.toString() + " to " + TABLE_NAME);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            return false;
        }
        return true;
    }

    Cursor getData(int selectedPuzzleID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query;
        // return all saved time, id = 18 equals all puzzles selected
        if(selectedPuzzleID == 18) {
            query = "SELECT * FROM " + TABLE_NAME;
        }
        // return just the times associated with the selected puzzle
        else{
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL0 + " = " + selectedPuzzleID;
        }
        return sqLiteDatabase.rawQuery(query, null);
    }

    void deleteData(TimeObject time){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL0 + " = " + time.getPuzzleID() +
                " AND " + COL1 + " = " + time.getMinutes() + " AND " + COL2 + " = " + time.getSeconds() +
                " AND " + COL3 + " = " + time.getMilliseconds() + " AND " + COL4 + " = " +
                time.getDay() + " AND " + COL5 + " = " + time.getMonth() + " AND " + COL6 + " = " +
                time.getYear();
        sqLiteDatabase.execSQL(query);
    }
}
