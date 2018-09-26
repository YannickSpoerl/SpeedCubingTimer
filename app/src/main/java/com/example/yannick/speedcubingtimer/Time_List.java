package com.example.yannick.speedcubingtimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;

public class Time_List extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Database database;
    ListView timelist;
    private int selectedPuzzleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time__list);
        Spinner puzzleSpinner = (Spinner) findViewById(R.id.puzzle_spinner_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.puzzles_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        puzzleSpinner.setAdapter(adapter);
        timelist = (ListView) findViewById(R.id.timeList);
        database = new Database(this);

        //Bottom Menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_settings:
                        Intent intent3 = new Intent(Time_List.this, Settings.class);
                        startActivity(intent3);
                        break;
                    case R.id.ic_time_list:
                        break;
                    case R.id.ic_timer:
                        Intent intent = new Intent(Time_List.this, Timer.class);
                        startActivity(intent);
                        break;
                    case R.id.ic_statistics:
                        Intent intent2 = new Intent(Time_List.this, Statistics.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_about:
                        Intent intent4 = new Intent(Time_List.this, About.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });

        puzzleSpinner.setOnItemSelectedListener(this);
        populateListView();
    }

    private void populateListView(){
        // Load all selected Time-Objects into ListView
        ArrayList<TimeObject> listData = new ArrayList<>();
        Cursor cursor = database.getData(selectedPuzzleID);
        while(cursor.moveToNext()){
            int puzzleID = cursor.getInt(cursor.getColumnIndex(Database.COL0));
            long minutes = cursor.getLong(cursor.getColumnIndex(Database.COL1));
            long seconds = cursor.getLong(cursor.getColumnIndex(Database.COL2));
            long milliseconds = cursor.getLong(cursor.getColumnIndex(Database.COL3));
            int day = cursor.getInt(cursor.getColumnIndex(Database.COL4));
            int month = cursor.getInt(cursor.getColumnIndex(Database.COL5));
            int year = cursor.getInt(cursor.getColumnIndex(Database.COL6));
            listData.add(new TimeObject(minutes, seconds, milliseconds, puzzleID, day, month, year));
        }
        final ListAdapter adapter = new TimeListAdapter(this,R.layout.adapter_view_layout,listData);
        timelist.setAdapter(adapter);

        timelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final TimeObject clickedTime = (TimeObject) adapterView.getItemAtPosition(position);
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(Time_List.this);
                deleteAlert.setTitle("Delete Time?");
                deleteAlert.setMessage("You are about to delete " + clickedTime.toString() + "\n Are you sure?");
                deleteAlert.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.deleteData(clickedTime);
                        populateListView();
                        Toast.makeText(Time_List.this, "Deleted selected time", Toast.LENGTH_LONG).show();
                    }
                });
                deleteAlert.setNegativeButton("No, don't delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Time_List.this, "No time deleted", Toast.LENGTH_LONG).show();
                    }
                });

                deleteAlert.create().show();
            }
        });
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(position == 0){
            selectedPuzzleID = 18; // means all puzzle types are selected
        }
        else {
            selectedPuzzleID = position - 1;
        }
        populateListView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
