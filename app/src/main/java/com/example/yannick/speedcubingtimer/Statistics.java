package com.example.yannick.speedcubingtimer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Statistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);


        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_settings:
                        Intent intent3 = new Intent(Statistics.this, Settings.class);
                        startActivity(intent3);
                        break;
                    case R.id.ic_time_list:
                        Intent intent2 = new Intent(Statistics.this, Time_List.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_timer:
                        Intent intent = new Intent(Statistics.this, Timer.class);
                        startActivity(intent);
                        break;
                    case R.id.ic_statistics:
                        break;
                    case R.id.ic_about:
                        Intent intent4 = new Intent(Statistics.this, About.class);
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
