package com.example.yannick.speedcubingtimer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Database database;
    int selectedPuzzle = 0, numberOfTimes = 0;
    TextView personalBest, avg5Current, avg5Best, avg12Current, avg12Best, avg50Current, avg50Best, avg100Current, avg100Best, avg1000Current, avg1000Best, overallAvg, numberOfSolves;
    FloatingActionButton shareFloatingActionButton;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        shareFloatingActionButton = (FloatingActionButton) findViewById(R.id.shareFloatingActionButton);
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
        graph = findViewById(R.id.graph);
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
        shareFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareStatistics();
            }
        });
    }

    public void plotGraph(){
        ArrayList<TimeObject> times = database.getTimeListArray(selectedPuzzle, 1);
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> averageData = new LineGraphSeries<DataPoint>();
        averageData.setColor(getResources().getColor(R.color.colorRed));
        double x = -1;
        double y;
        for(int i  = numberOfTimes; i > 0; i--){
            x  += 1;
            y = calculateAverage(times) / 1000;
            averageData.appendData(new DataPoint(x,y), true, numberOfTimes);
        }
        LineGraphSeries<DataPoint> graphData = new LineGraphSeries<DataPoint>();
        graphData.setColor(getResources().getColor(R.color.colorPrimary));
        x = -1;
        for(TimeObject time : times){
            x += 1;
            y = time.getTotalDuration() / 1000;
            graphData.appendData(new DataPoint(x,y), true, numberOfTimes);
        }
        graph.addSeries(graphData);
        graph.addSeries(averageData);
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedPuzzle = i;
        populateStatistics();
    }

    public void shareStatistics(){
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String sharetext = getResources().getString(R.string.my_personal_best_with) + TimeObject.getPuzzleByID(selectedPuzzle) + ":";
        if(numberOfTimes >= 1){
            sharetext += "\nPersonal Best: " + personalBest.getText();
        }
        if(numberOfTimes >= 5){
            sharetext += "\nAverage of 5: " + avg5Best.getText();
        }
        if(numberOfTimes >= 12){
            sharetext += "\nAverage of 12: " + avg12Best.getText();
        }
        if(numberOfTimes >= 50){
            sharetext += "\nAverage of 50: " + avg50Best.getText();
        }
        if(numberOfTimes >= 100){
            sharetext += "\nAverage of 100: " + avg100Best.getText();
        }
        if(numberOfTimes >= 1000){
            sharetext += "\nAverage of 1000: " + avg1000Best.getText();
        }
        intent.putExtra(Intent.EXTRA_TEXT,sharetext);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_your_time_with)));
    }

    public static long findFastestTime(ArrayList<TimeObject> timeList){
        TimeObject bestTime = timeList.get(0);
        for(TimeObject time : timeList){
            if(time.getTotalDuration() < bestTime.getTotalDuration()){
                bestTime = time;
            }
        }
        return bestTime.getTotalDuration();
    }

    public static String millisecondsToString(long milliseconds){
        long mins = milliseconds / 60000;
        milliseconds = milliseconds - (mins * 60000);
        long secs = milliseconds / 1000;
        milliseconds = milliseconds - (secs * 1000);
        String time = "";
        if(mins < 10){
            time += "0";
        }
        time += mins + ":";
        if(secs < 10){
            time += "0";
        }
        time += secs + ".";
        if(milliseconds < 100 && milliseconds >= 10){
            time += "0";
        }
        else if(milliseconds < 10){
            time += "00";
        }
        time += milliseconds;
        return time;
    }

    public static long calculateAverage(ArrayList<TimeObject> timeList){
        long totalDuration = 0;
        for(TimeObject time : timeList){
            totalDuration += time.getTotalDuration();
        }
        totalDuration = totalDuration / timeList.size();
        return totalDuration;
    }

    public static ArrayList<TimeObject> removeFastestSlowestTime(ArrayList<TimeObject> timeList){
        int index = 0;
        long time = Long.MAX_VALUE;
        for (int i = 0; i < timeList.size(); i++){
            if(timeList.get(i).getTotalDuration() < time){
                time = timeList.get(i).getTotalDuration();
                index = i;
            }
        }
        timeList.remove(index);
        index = 0;
        time = 0;
        for(int i = 0; i < timeList.size(); i++){
            if(timeList.get(i).getTotalDuration() > time){
                time = timeList.get(i).getTotalDuration();
                index = i;
            }
        }
        timeList.remove(index);
        return timeList;
    }

    public static long findFastestAverage(ArrayList<TimeObject> timeList, int numberOfTimes){
        long bestAverage = Long.MAX_VALUE;
        for(int i = 0; i <= timeList.size() - numberOfTimes; i++){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int j = 0; j < numberOfTimes; j++){
                newList.add(timeList.get(i+j));
            }
            long newAverage = calculateAverage(removeFastestSlowestTime(newList));
            if(newAverage < bestAverage){
                bestAverage = newAverage;
            }
        }
        return bestAverage;
    }


    public void populateStatistics(){
        resetTextViews();
        ArrayList<TimeObject> currentTimes = database.getTimeListArray(selectedPuzzle, 0);
        numberOfTimes = currentTimes.size();
        if(numberOfTimes >= 1){
            overallAvg.setText(millisecondsToString(calculateAverage(currentTimes)));
            personalBest.setText(millisecondsToString(findFastestTime(currentTimes)));
        }
        if(numberOfTimes >= 1000 ){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 1000; i++){
                newList.add(currentTimes.get(i));
            }
            avg1000Current.setText(millisecondsToString(calculateAverage(removeFastestSlowestTime(newList))));
            avg1000Best.setText(millisecondsToString(findFastestAverage(currentTimes, 1000)));
        }
        if(numberOfTimes >= 100){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 100; i++){
                newList.add(currentTimes.get(i));
            }
            avg100Current.setText(millisecondsToString(calculateAverage(removeFastestSlowestTime(newList))));
            avg100Best.setText(millisecondsToString(findFastestAverage(currentTimes, 100)));
        }
        if(numberOfTimes >= 50){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 50; i++){
                newList.add(currentTimes.get(i));
            }
            avg50Current.setText(millisecondsToString(calculateAverage(removeFastestSlowestTime(newList))));
            avg50Best.setText(millisecondsToString(findFastestAverage(currentTimes, 50)));
        }
        if(numberOfTimes >= 12){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 12; i++){
                newList.add(currentTimes.get(i));
            }
            avg12Current.setText(millisecondsToString(calculateAverage(removeFastestSlowestTime(newList))));
            avg12Best.setText(millisecondsToString(findFastestAverage(currentTimes, 12)));
        }
        if(numberOfTimes >= 5){
            ArrayList<TimeObject> newList = new ArrayList<>();
            for(int i = 0; i < 5; i++){
                newList.add(currentTimes.get(i));
            }
            avg5Current.setText(millisecondsToString(calculateAverage(removeFastestSlowestTime(newList))));
            avg5Best.setText(millisecondsToString(findFastestAverage(currentTimes, 5)));
        }
        numberOfSolves.setText(String.valueOf(numberOfTimes));
        plotGraph();
    }

    public void resetTextViews(){
        avg5Current.setText(R.string._00_00_000);
        avg5Best.setText(R.string._00_00_000);
        avg5Current.setText(R.string._00_00_000);
        avg12Current.setText(R.string._00_00_000);
        avg12Best.setText(R.string._00_00_000);
        avg50Current.setText(R.string._00_00_000);
        avg50Best.setText(R.string._00_00_000);
        avg100Current.setText(R.string._00_00_000);
        avg100Best.setText(R.string._00_00_000);
        avg1000Current.setText(R.string._00_00_000);
        avg1000Best.setText(R.string._00_00_000);
        overallAvg.setText(R.string._00_00_000);
        personalBest.setText(R.string._00_00_000);
        numberOfSolves.setText("0");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
