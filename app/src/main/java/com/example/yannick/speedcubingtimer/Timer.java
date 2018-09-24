package com.example.yannick.speedcubingtimer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class Timer extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

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
    }

    @Override
    public void onBackPressed(){
    }

}
