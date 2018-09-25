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
import android.widget.Button;
import android.widget.TextView;

public class Timer extends AppCompatActivity {

    // Settings
    boolean inspection_enabled = true;
    boolean time_visible = true;

    boolean timer_running = false;
    boolean inspection_running = false;
    private CountDownTimer inspectiontimer;
    private long inspectiontimeleft = 15000;
    Button startstopbutton;
    TextView statustextview;
    Handler timeHandler = new Handler();
    long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            if(!time_visible){
                return;
            }
            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime/1000);
            int mins = secs/60;
            secs %= 60;
            int milliseconds = (int) (updateTime%1000);
            startstopbutton.setText("" + mins + ":" + String.format("%2d", secs) + ":" +
                                    String.format("%3d", milliseconds));
            timeHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        startstopbutton = (Button) findViewById(R.id.startstopbutton);
        statustextview = (TextView) findViewById(R.id.status_textview);
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
        if(!time_visible) {
            startstopbutton.setText("Timing");
        }
        startstopbutton.setTextColor(Color.BLACK);
        statustextview.setText("Timing");

    }

    public void stopTiming(){
        timer_running = false;
        inspection_running = false;
        timeSwapBuff += timeInMilliseconds;
        timeHandler.removeCallbacks(updateTimerThread);
        statustextview.setText("Timing stopped");

    }

    @Override
    public void onBackPressed(){
    }

}