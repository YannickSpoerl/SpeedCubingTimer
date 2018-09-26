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
    private static final String COL1 = "puzzle_id";
    private static final String COL2 = "minutes";
    private static final String COL3 = "seconds";
    private static final String COL4 = "milliseconds";
    private static final String COL5 = "day";
    private static final String COL6 = "month";
    private static final String COL7 = "year";

    public Database(Context context){
        super(context, TABLE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 +" TEXT, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5  +
                " TEXT, " + COL6 + " TEXT, " + COL7 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(TimeObject time){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, String.valueOf(time.getPuzzleID()));
        contentValues.put(COL2, String.valueOf(time.getMinutes()));
        contentValues.put(COL3, String.valueOf(time.getSeconds()));
        contentValues.put(COL4, String.valueOf(time.getMilliseconds()));
        contentValues.put(COL5, String.valueOf(time.getDay()));
        contentValues.put(COL6, String.valueOf(time.getMonth()));
        contentValues.put(COL7, String.valueOf(time.getYear()));
        Log.d(TAG, "addData: Adding " + time.toString() + " to " + TABLE_NAME);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            return false;
        }
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = sqLiteDatabase.rawQuery(query, null);
        return data;
    }
}
