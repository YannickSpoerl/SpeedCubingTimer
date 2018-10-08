package com.example.yannick.speedcubingtimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "times_table";
    private static final int TABLE_VERSION = 3;
    private static final String COL0 = "puzzle_id", COL1 = "minutes", COL2 = "seconds", COL3 = "milliseconds", COL4 = "day", COL5 = "month", COL6 = "year";

    Database(Context context){
        super(context, TABLE_NAME, null, TABLE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL0 + " INTEGER, " + COL1 + " INTEGER, " + COL2 + " INTEGER, " + COL3 + " INTEGER, " + COL4 + " INTEGER, " + COL5 + " INTEGER, " + COL6 + " TEXT)";
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
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    ArrayList<TimeObject> getTimeListArray(int selectedPuzzleID, int selectedSortBy){
        ArrayList<TimeObject> timeListArray = new ArrayList<>();
        Cursor cursor = this.getData(selectedPuzzleID, selectedSortBy);
        while(cursor.moveToNext()){
            int puzzleID = cursor.getInt(cursor.getColumnIndex(Database.COL0));
            long minutes = cursor.getLong(cursor.getColumnIndex(Database.COL1));
            long seconds = cursor.getLong(cursor.getColumnIndex(Database.COL2));
            long milliseconds = cursor.getLong(cursor.getColumnIndex(Database.COL3));
            int day = cursor.getInt(cursor.getColumnIndex(Database.COL4));
            int month = cursor.getInt(cursor.getColumnIndex(Database.COL5));
            int year = cursor.getInt(cursor.getColumnIndex(Database.COL6));
            timeListArray.add(new TimeObject(minutes, seconds, milliseconds, puzzleID, day, month, year));
        }
        return timeListArray;
    }

    Cursor getData(int selectedPuzzleID, int selectedSortBy){
        String orderBy = " ORDER BY ID DESC";
        switch (selectedSortBy){
            case 1:
                orderBy = " ORDER BY ID ASC";
                break;
            case 2:
                orderBy = " ORDER BY " + COL1 + " ASC, " + COL2 + " ASC, " + COL3 + " ASC";
                break;
            case 3:
                orderBy = " ORDER BY " + COL1 + " DESC, " + COL2 + " DESC, " + COL3 + " DESC";
        }
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query;
        // return all saved time, id = 18 equals all puzzles selected
        if(selectedPuzzleID == 18) {
            query = "SELECT * FROM " + TABLE_NAME + orderBy;
        }
        // return just the times associated with the selected puzzle
        else{
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL0 + " = " + selectedPuzzleID + orderBy;
        }
        return sqLiteDatabase.rawQuery(query, null);
    }

    void resetData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
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
