package com.example.yannick.speedcubingtimer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Database database;
    int selectedPuzzle = 0;
    TextView personalBest, avg5Current, avg5Best, avg12Current, avg12Best, avg50Current, avg50Best, avg100Current, avg100Best, avg1000Current, avg1000Best, overallAvg, numberOfSolves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        personalBest = (TextView) findViewById(R.id.personalBestTextView);
        avg5Current = (TextView) findViewById(R.id.avg5CurrentTextView);
        avg5Best = (TextView) findViewById(R.id.avg5BestTextView);
        avg12Current = (TextView) findViewById(R.id.avg12CurrentTextView);
        avg12Best = (TextView) findViewById(R.id.avg12BestTextView);
        avg50Current = (TextView) findViewById(R.id.avg50CurrentTextView);
        avg50Best = (TextView) findViewById(R.id.avg50BestTextView);
        avg100Current = (TextView) findViewById(R.id.avg100CurrentTextView);
        avg100Best = (TextView) findViewById(R.id.avg100BestTextView);
        avg1000Current = (TextView) findViewById(R.id.avg1000CurrentTextView);
        avg1000Best = (TextView) findViewById(R.id.avg1000BestTextView);
        overallAvg = (TextView) findViewById(R.id.overallAvgTextView);
        numberOfSolves = (TextView) findViewById(R.id.numberOfSolvesTextView);
        Spinner puzzleSpinner = (Spinner) findViewById(R.id.puzzleStatisticsSpinner);
        database = new Database(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.puzzles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        puzzleSpinner.setAdapter(adapter);
        puzzleSpinner.setOnItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_settings:
                        startActivity(new Intent(Statistics.this, Settings.class));
                        break;
                    case R.id.ic_time_list:
                        startActivity(new Intent(Statistics.this, Time_List.class));
                        break;
                    case R.id.ic_timer:
                        startActivity(new Intent(Statistics.this, Timer.class));
                        break;
                    case R.id.ic_about:
                        startActivity(new Intent(Statistics.this, About.class));
                        break;
                }
                return false;
            }
        });
        populateStatistics();
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedPuzzle = i;
        populateStatistics();
    }

    public String findFastestTime(ArrayList<TimeObject> timeList){
        TimeObject bestTime = timeList.get(0);
        for(TimeObject time : timeList){
            if(time.getTotalDuration() < bestTime.getTotalDuration()){
                bestTime = time;
            }
        }
        long totalDuration = bestTime.getTotalDuration();
        long mins = totalDuration / 60000;
        totalDuration = totalDuration - (mins * 60000);
        long secs = totalDuration / 1000;
        totalDuration = totalDuration - (secs * 1000);
        String time = "";
        if(mins < 10){
            time += "0";
        }
        time += mins + ":";
        if(secs < 10){
            time += "0";
        }
        time += secs + ".";
        if(totalDuration < 100 && totalDuration >= 10){
            time += "0";
        }
        else if(totalDuration < 10){
            time += "00";
        }
        time += totalDuration;
        return time;
    }

    public String calculateAverage(ArrayList<TimeObject> timeList){
        long totalDuration = 0;
        for(TimeObject time : timeList){
            totalDuration += time.getTotalDuration();
        }
        totalDuration = totalDuration / timeList.size();
        long mins = totalDuration / 60000;
        totalDuration = totalDuration - (mins * 60000);
        long secs = totalDuration / 1000;
        totalDuration = totalDuration - (secs * 1000);
        String time = "";
        if(mins < 10){
            time += "0";
        }
        time += mins + ":";
        if(secs < 10){
            time += "0";
        }
        time += secs + ".";
        if(totalDuration < 100 && totalDuration >= 10){
            time += "0";
        }
        else if(totalDuration < 10){
            time += "00";
        }
        time += totalDuration;
        return time;
    }

    public void populateStatistics(){
        resetTextViews();
        ArrayList<TimeObject> currentTimes = database.getTimeListArray(selectedPuzzle, 0);
        int numberOfTimes = currentTimes.size();
        if(numberOfTimes >= 1){
            overallAvg.setText(calculateAverage(currentTimes));
            personalBest.setText(findFastestTime(currentTimes));
        }
        if(numberOfTimes >= 1000 ){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 1000; i++){
                newList.add(currentTimes.get(i));
            }
            avg1000Current.setText(calculateAverage(newList));
        }
        if(numberOfTimes >= 100){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 100; i++){
                newList.add(currentTimes.get(i));
            }
            avg100Current.setText(calculateAverage(newList));
        }
        if(numberOfTimes >= 50){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 50; i++){
                newList.add(currentTimes.get(i));
            }
            avg50Current.setText(calculateAverage(newList));
        }
        if(numberOfTimes >= 12){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 12; i++){
                newList.add(currentTimes.get(i));
            }
            avg12Current.setText(calculateAverage(newList));
        }
        if(numberOfTimes >= 5){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 5; i++){
                newList.add(currentTimes.get(i));
            }
            avg5Current.setText(calculateAverage(newList));
        }
        numberOfSolves.setText(String.valueOf(numberOfTimes));
    }

    public void resetTextViews(){
        avg5Current.setText("00:00.000");
        avg5Best.setText("00:00.000");
        avg5Current.setText("00:00.000");
        avg12Current.setText("00:00.000");
        avg12Best.setText("00:00.000");
        avg50Current.setText("00:00.000");
        avg50Best.setText("00:00.000");
        avg100Current.setText("00:00.000");
        avg100Best.setText("00:00.000");
        avg1000Current.setText("00:00.000");
        avg1000Best.setText("00:00.000");
        overallAvg.setText("00:00.000");
        personalBest.setText("00:00.000");
        numberOfSolves.setText("0");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
