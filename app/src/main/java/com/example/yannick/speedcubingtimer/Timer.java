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

    boolean inspection_enabled = false;
    boolean timer_visible = true;

    private int selectedpuzzleID = 0;
    boolean timer_running = false;
    boolean inspection_running = false;
    private long inspectiontimeleft = 15000;
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private long finalminutes = 0L, finalseconds = 0L, finalmilliseconds = 0L;

    private CountDownTimer inspectiontimer;
    private Database database;
    private Button startstopbutton;
    private TextView statustextview;
    private Handler timeHandler = new Handler();

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime/1000);
            int mins = secs/60;
            secs %= 60;
            int milliseconds = (int) (updateTime%1000);
            if(timer_visible) {
                startstopbutton.setText("" + mins + ":" + String.format("%2d", secs) + ":" +
                        String.format("%3d", milliseconds));
            }
            finalminutes = mins;
            finalseconds = secs;
            finalmilliseconds = milliseconds;
            timeHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        startstopbutton = (Button) findViewById(R.id.startstopbutton);
        statustextview = (TextView) findViewById(R.id.status_textview);
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

        startstopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer_running){
                    stopTiming();
                }
                else if(inspection_running){
                    startTiming();
                }
                else if (inspection_enabled){
                    startInspection();
                }
                else {
                    startTiming();
                }
            }
        });

        startstopbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == motionEvent.ACTION_DOWN &&!timer_running){
                    startstopbutton.setTextColor(Color.GREEN);
                }
                else if(motionEvent.getAction() == motionEvent.ACTION_UP){
                    startstopbutton.setTextColor(Color.BLACK);
                }
                return false;
            }
        });

        puzzleSpinner.setOnItemSelectedListener(this);

        loadSettings();

    }

    public void startInspection(){
        timer_running = false;
        inspection_running = true;
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updateTime = 0L;
        inspectiontimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long l) {
                inspectiontimeleft = l;
                startstopbutton.setText(String.valueOf((int) inspectiontimeleft/1000));
            }
            @Override
            public void onFinish() {
                startstopbutton.setText("DNF");
                timer_running = false;
                inspection_running = false;
                statustextview.setText("Timing Stopped");

            }
        }.start();
        startstopbutton.setTextColor(Color.RED);
        statustextview.setText("Inspecting");

    }

    public void startTiming(){
        if(inspection_enabled) {
            inspectiontimer.cancel();
        }
        inspection_running = false;
        timer_running = true;
        startTime = SystemClock.uptimeMillis();
        timeHandler.postDelayed(updateTimerThread,0);

        if(!timer_visible) {
            startstopbutton.setText("Time hidden");
        }
        startstopbutton.setTextColor(Color.BLACK);
        statustextview.setText("Timing");

    }

    public void stopTiming(){
        timer_running = false;
        inspection_running = false;
        timeSwapBuff += timeInMilliseconds;
        startstopbutton.setTextColor(Color.BLACK);
        timeHandler.removeCallbacks(updateTimerThread);
        startstopbutton.setText("" + finalminutes + ":" + String.format("%2d", finalseconds) + ":" +
                String.format("%3d", finalmilliseconds));
        Calendar c = Calendar.getInstance();
        TimeObject newTime = new TimeObject(finalminutes,finalseconds,finalmilliseconds,selectedpuzzleID,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
        saveTime(newTime);
    }

    public void saveTime(TimeObject newTime){
        boolean addTime = database.addData(newTime);
        if(!addTime){
            Toast.makeText(this,"Couldnt save time", Toast.LENGTH_LONG).show();
        }
    }

    public void loadSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        inspection_enabled = sharedPreferences.getBoolean(Settings.INSPECTION_ENABLED, true);
        timer_visible = sharedPreferences.getBoolean(Settings.TIME_SHOWN_ENABLED, true);
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