package com.example.yannick.speedcubingtimer;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
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

    // Settings
    boolean inspection_enabled = true;
    boolean timer_visible = true;

    /* Puzzle-IDs
    0 THREE_BY_THREE
    1 FOUR_BY_FOUR
    2 FIVE_BY_FIVE,
    3 TWO_BY_TWO,
    4 THREE_BY_THREE_BLD,
    5 THREE_BY_THREE_OH,
    6 THREE_BY_THREE_FM,
    7 THREE_BY_THREE_FT,
    8 MEGAMINX,
    9 PYRAMINX,
    10 SQ1,
    11 CLOCK,
    12 SKEWB,
    13 SIX_BY_SIX,
    14 SEVEN_BY_SEVEN,
    15 FOUR_BY_FOUR_BLD,
    16FIVE_BY_FIVE_BLD,
    17 THREE_BY_THREE_MBLD;*/

    private int selectedpuzzleID = 0;

    boolean timer_running = false, inspection_running = false;
    private CountDownTimer inspectiontimer;
    private long inspectiontimeleft = 15000;
    Button startstopbutton;
    TextView statustextview;
    Handler timeHandler = new Handler();
    long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    long finalminutes, finalseconds, finalmilliseconds;
    Database database;


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
        inspection_running = false;
        timer_running = true;
        startTime = SystemClock.uptimeMillis();
        timeHandler.postDelayed(updateTimerThread,0);
        inspectiontimer.cancel();
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
        boolean addTime = database.addData(newTime);
        if(!addTime){
            Toast.makeText(this,"Couldnt save time", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}