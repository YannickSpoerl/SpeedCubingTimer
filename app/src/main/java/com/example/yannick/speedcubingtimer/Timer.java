package com.example.yannick.speedcubingtimer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class Timer extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private boolean inspectionEnabled, timerVisible, timerRunning = false, inspectionRunning = false;
    private int selectedpuzzleID = 0;
    private long inspectionTimeLeft = 15000, startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L, finalMinutes = 0L, finalSeconds = 0L, finalMilliseconds = 0L, personalBest = 0L;
    private CountDownTimer inspectionCountDownTimer;
    private Database database;
    private Button startStopButton;
    private TextView messageTextView;
    private Handler timeHandler = new Handler();

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int seconds = (int) (updateTime/1000);
            int minutes = seconds/60;
            seconds %= 60;
            int milliseconds = (int) (updateTime%1000);
            if(timerVisible) {
                startStopButton.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + ":" +
                        String.format("%03d", milliseconds));
            }
            finalMinutes = minutes;
            finalSeconds = seconds;
            finalMilliseconds = milliseconds;
            timeHandler.postDelayed(this, 0);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        startStopButton = (Button) findViewById(R.id.startstopbutton);
        messageTextView = (TextView) findViewById(R.id.status_textview);
        Spinner puzzleSpinner = (Spinner) findViewById(R.id.puzzle_spinner);
        database = new Database(this);
        loadSettings();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.puzzles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        puzzleSpinner.setAdapter(adapter);
        //Bottom Menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_settings:
                        startActivity(new Intent(Timer.this, Settings.class));
                        break;
                    case R.id.ic_time_list:
                        startActivity(new Intent(Timer.this, Time_List.class));
                        break;
                    case R.id.ic_statistics:
                        startActivity(new Intent(Timer.this, Statistics.class));
                        break;
                    case R.id.ic_about:
                        startActivity(new Intent(Timer.this, About.class));
                        break;
                }
                return false;
            }
        });

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerRunning){
                    stopTiming();
                }
                else if(inspectionRunning){
                    startTiming();
                }
                else if (inspectionEnabled){
                    startInspection();
                }
                else {
                    startTiming();
                }
            }
        });

        startStopButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN &&!timerRunning){
                    startStopButton.setTextColor(Color.GREEN);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    startStopButton.setTextColor(Color.BLACK);
                }
                return false;
            }
        });

        puzzleSpinner.setOnItemSelectedListener(this);
    }

    public void startInspection(){
        timerRunning = false;
        inspectionRunning = true;
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updateTime = 0L;
        inspectionCountDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long l) {
                inspectionTimeLeft = l;
                startStopButton.setText(String.valueOf((int) inspectionTimeLeft /1000));
            }
            @Override
            public void onFinish() {
                startStopButton.setText(R.string.dnf);
                timerRunning = false;
                inspectionRunning = false;
                messageTextView.setText(R.string.timing_stopped);

            }
        }.start();
        startStopButton.setTextColor(Color.RED);
        messageTextView.setText(R.string.inspecting);

    }

    public void startTiming(){
        if(inspectionEnabled) {
            inspectionCountDownTimer.cancel();
        }
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updateTime = 0L;
        finalMinutes = 0L;
        finalSeconds = 0L;
        finalMilliseconds = 0L;
        inspectionRunning = false;
        timerRunning = true;
        startTime = SystemClock.uptimeMillis();
        timeHandler.postDelayed(updateTimerThread,0);

        if(!timerVisible) {
            startStopButton.setText(R.string.time_hidden);
        }
        startStopButton.setTextColor(Color.BLACK);
        messageTextView.setText(R.string.timing);

    }

    public void stopTiming(){
        timerRunning = false;
        inspectionRunning = false;
        timeSwapBuff += timeInMilliseconds;
        startStopButton.setTextColor(Color.BLACK);
        timeHandler.removeCallbacks(updateTimerThread);
        startStopButton.setText(String.format("%02d", finalMinutes) + ":" + String.format("%02d", finalSeconds) + ":" +
                String.format("%03d", finalMilliseconds));
        Calendar c = Calendar.getInstance();
        TimeObject newTime = new TimeObject(finalMinutes, finalSeconds, finalMilliseconds,selectedpuzzleID,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
        saveTime(newTime);
        messageTextView.setText(R.string.timing_stopped);
        if(personalBest == 0|| newTime.getTotalDuration() < personalBest){
            messageTextView.setText(R.string.new_personal_best_notification);
            personalBest = newTime.getTotalDuration();
        }
    }

    public void saveTime(TimeObject newTime){
        boolean addTime = database.addData(newTime);
        if(!addTime){
            Toast.makeText(this,"Couldnt save time", Toast.LENGTH_LONG).show();
        }
    }

    public void loadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        inspectionEnabled = sharedPreferences.getBoolean(Settings.INSPECTION_ENABLED, true);
        timerVisible = sharedPreferences.getBoolean(Settings.TIME_SHOWN_ENABLED, true);
        ArrayList<TimeObject> currentTimes = database.getTimeListArray(selectedpuzzleID, 0);
        if(currentTimes.size() >= 1) {
            personalBest = Statistics.findFastestTime(currentTimes);
        }
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        selectedpuzzleID = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}