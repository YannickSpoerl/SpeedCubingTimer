package com.example.yannick.speedcubingtimer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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

import java.util.ArrayList;

public class Time_List extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private final static String TAG = "TimeListActivity";
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
        ArrayList<TimeObject> listData = new ArrayList<>();
        TimeObject t1 = new TimeObject(1,12,12124,0,26,9,2018);
        TimeObject t2 = new TimeObject(0,2,142,1,26,9,2018);
        TimeObject t3 = new TimeObject(12,0,0,2,0,7,2018);
        TimeObject t4 = new TimeObject(1,12,1224,2,26,12,2008);
        TimeObject t5 = new TimeObject(1,12,1224,2,26,12,2008);

        listData.add(t1);
        listData.add(t2);
        listData.add(t3);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);
        listData.add(t4);

        ListAdapter adapter = new TimeListAdapter(this,R.layout.adapter_view_layout,listData);
        timelist.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(position == 0){
            selectedPuzzleID = 18;
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
