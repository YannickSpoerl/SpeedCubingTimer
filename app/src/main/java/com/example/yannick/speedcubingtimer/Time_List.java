package com.example.yannick.speedcubingtimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

public class Time_List extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Database database;
    ListView timeListListView;
    FloatingActionButton menuFAB, shareFAB, deleteFAB;
    Animation fabOpen, fabClose, fabRotateClockwise, fabRotateAnticlockwise;
    private int selectedPuzzleID;
    private int selectedSortBy = 0; // 0 = latest, 1 = oldest, 2 = fastest, 3 = slowest
    private int floatingActionButtonMode = 0; // 0 = share, 1 = delete
    boolean menuFABopen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time__list);

        menuFAB = (FloatingActionButton) findViewById(R.id.menuFAB);
        shareFAB = (FloatingActionButton) findViewById(R.id.shareFAB);
        deleteFAB = (FloatingActionButton) findViewById(R.id.deleteFAB);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRotateAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        menuFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClicked();
            }
        });

        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switchFABmode();
            }
        });

        deleteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFABmode();
            }
        });

        Spinner puzzleSpinner = (Spinner) findViewById(R.id.puzzle_spinner_list);
        ArrayAdapter<CharSequence> puzzleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.puzzles_list, android.R.layout.simple_spinner_item);
        puzzleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        puzzleSpinner.setAdapter(puzzleSpinnerAdapter);

        Spinner sortBySpinner = (Spinner) findViewById(R.id.sortby_spinner);
        ArrayAdapter<CharSequence> sortBySpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.sortBySpinnerArray, android.R.layout.simple_spinner_item);
        sortBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortBySpinnerAdapter);

        timeListListView = (ListView) findViewById(R.id.timeList);
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
                        startActivity(new Intent(Time_List.this, Settings.class));
                        break;
                    case R.id.ic_timer:
                        startActivity(new Intent(Time_List.this, Timer.class));
                        break;
                    case R.id.ic_statistics:
                        startActivity(new Intent(Time_List.this, Statistics.class));
                        break;
                    case R.id.ic_about:
                        startActivity(new Intent(Time_List.this, About.class));
                        break;
                }
                return false;
            }
        });

        puzzleSpinner.setOnItemSelectedListener(this);
        sortBySpinner.setOnItemSelectedListener(this);
        populateListView();
    }

    private void populateListView(){
        ArrayList<TimeObject> timeListArray = database.getTimeListArray(selectedPuzzleID, selectedSortBy);
        timeListListView.setAdapter(new TimeListAdapter(this,R.layout.adapter_view_layout,timeListArray));
        // delete time dialoge
        timeListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final TimeObject clickedTime = (TimeObject) adapterView.getItemAtPosition(position);
                switch (floatingActionButtonMode){
                    case 0:
                        shareTime(clickedTime);
                        break;
                    case 1:
                        deleteTime(clickedTime);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getId() == R.id.puzzle_spinner_list){
            if(position == 0){
              selectedPuzzleID = 18; // means all puzzle types are selected
            }
            else {
             selectedPuzzleID = position - 1;
            }
        }
        else if(adapterView.getId() == R.id.sortby_spinner){
            selectedSortBy = position;
        }
        populateListView();
    }

    public void shareTime(TimeObject clickedTime){
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Hey, I want to share this time with you: " + clickedTime.toString());
        startActivity(Intent.createChooser(intent, "Share your time via:"));
    }

    public void deleteTime(final TimeObject clickedTime){
        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(Time_List.this);
        deleteAlert.setTitle("Delete Time?");
        deleteAlert.setMessage("You are about to delete " + clickedTime.toString() + "\n Are you sure?");
        deleteAlert.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.deleteData(clickedTime);
                populateListView();
                Toast.makeText(Time_List.this, "Deleted selected time", Toast.LENGTH_SHORT).show();
            }
        });
        deleteAlert.setNegativeButton("No, don't delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        deleteAlert.create().show();
    }

    public void switchFABmode(){
        switch (floatingActionButtonMode){
            case 0:
                shareFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGrey)));
                deleteFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                floatingActionButtonMode = 1;
                Toast.makeText(this, "Tap time to delete",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                shareFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                deleteFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGrey)));
                Toast.makeText(this,"Tap time to share", Toast.LENGTH_SHORT).show();
                floatingActionButtonMode = 0;
                break;
        }
    }

    public void menuClicked(){
        if(menuFABopen){
            shareFAB.startAnimation(fabClose);
            deleteFAB.startAnimation(fabClose);
            menuFAB.startAnimation(fabRotateAnticlockwise);
            shareFAB.setClickable(false);
            deleteFAB.setClickable(false);
            menuFABopen = false;
        }
        else{
            shareFAB.startAnimation(fabOpen);
            deleteFAB.startAnimation(fabOpen);
            menuFAB.startAnimation(fabRotateClockwise);
            shareFAB.setClickable(true);
            deleteFAB.setClickable(true);
            menuFABopen = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // do nothing
    }
}
