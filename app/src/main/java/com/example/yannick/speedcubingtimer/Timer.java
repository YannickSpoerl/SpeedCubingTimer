package com.example.yannick.speedcubingtimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Timer extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    boolean inspectionEnabled;
    boolean timerVisible;
    private int selectedpuzzleID = 0;
    boolean timerRunning = false;
    boolean inspectionRunning = false;
    private long inspectionTimeLeft = 15000;
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private long finalMinutes = 0L, finalSeconds = 0L, finalMilliseconds = 0L;

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
                startStopButton.setText("" + minutes + ":" + seconds + ":" +
                        milliseconds);
            }
            finalMinutes = minutes;
            finalSeconds = seconds;
            finalMilliseconds = milliseconds;
            timeHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        startStopButton = (Button) findViewById(R.id.startstopbutton);
        messageTextView = (TextView) findViewById(R.id.status_textview);
        Spinner puzzleSpinner = (Spinner) findViewById(R.id.puzzle_spinner);
        database = new Database(this);
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
                        Intent intent = new Intent(Timer.this, Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.ic_time_list:
                        Intent intent2 = new Intent(Timer.this, Time_List.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_timer:
                        break;
                    case R.id.ic_statistics:
                        Intent intent3 = new Intent(Timer.this, Statistics.class);
                        startActivity(intent3);
                        break;
                    case R.id.ic_about:
                        Intent intent4 = new Intent(Timer.this, About.class);
                        startActivity(intent4);
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
                if(motionEvent.getAction() == motionEvent.ACTION_DOWN &&!timerRunning){
                    startStopButton.setTextColor(Color.GREEN);
                }
                else if(motionEvent.getAction() == motionEvent.ACTION_UP){
                    startStopButton.setTextColor(Color.BLACK);
                }
                return false;
            }
        });

        puzzleSpinner.setOnItemSelectedListener(this);

        loadSettings();

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
                startStopButton.setText("DNF");
                timerRunning = false;
                inspectionRunning = false;
                messageTextView.setText("Timing Stopped");

            }
        }.start();
        startStopButton.setTextColor(Color.RED);
        messageTextView.setText("Inspecting");

    }

    public void startTiming(){
        if(inspectionEnabled) {
            inspectionCountDownTimer.cancel();
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updateTime = 0L;
            finalMinutes = 0L;
            finalSeconds = 0L;
            finalMilliseconds = 0L;
        }
        inspectionRunning = false;
        timerRunning = true;
        startTime = SystemClock.uptimeMillis();
        timeHandler.postDelayed(updateTimerThread,0);

        if(!timerVisible) {
            startStopButton.setText("Time hidden");
        }
        startStopButton.setTextColor(Color.BLACK);
        messageTextView.setText("Timing");

    }

    public void stopTiming(){
        timerRunning = false;
        inspectionRunning = false;
        timeSwapBuff += timeInMilliseconds;
        startStopButton.setTextColor(Color.BLACK);
        timeHandler.removeCallbacks(updateTimerThread);
        startStopButton.setText("" + finalMinutes + ":" + String.format("%2d", finalSeconds) + ":" +
                String.format("%3d", finalMilliseconds));
        Calendar c = Calendar.getInstance();
        TimeObject newTime = new TimeObject(finalMinutes, finalSeconds, finalMilliseconds,selectedpuzzleID,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
        saveTime(newTime);
        messageTextView.setText("Timing stopped");
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