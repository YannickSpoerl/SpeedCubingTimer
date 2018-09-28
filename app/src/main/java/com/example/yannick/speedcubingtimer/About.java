package com.example.yannick.speedcubingtimer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        ImageView iconImageView = (ImageView) findViewById(R.id.iconImageView);
        iconImageView.setImageResource(R.drawable.ic_launcher_web);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_settings:
                        Intent intent = new Intent(About.this, Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.ic_time_list:
                        Intent intent2 = new Intent(About.this, Time_List.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_timer:
                        Intent intent4 = new Intent(About.this, Timer.class);
                        startActivity(intent4);
                        break;
                    case R.id.ic_statistics:
                        Intent intent3 = new Intent(About.this, Statistics.class);
                        startActivity(intent3);
                        break;
                    case R.id.ic_about:

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
