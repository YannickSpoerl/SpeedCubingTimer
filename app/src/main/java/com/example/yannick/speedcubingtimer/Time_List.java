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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Time_List extends AppCompatActivity {

    private final static String TAG = "TimeListActivity";
    Database database;
    ListView timelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time__list);
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

        populateListView();
    }

    private void populateListView(){
        Log.d(TAG, "populateListView: Displaying data in the ListView.");
        Cursor data = database.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(0));
        }
        ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        timelist.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
    }
}
