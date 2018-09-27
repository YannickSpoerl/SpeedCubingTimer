package com.example.yannick.speedcubingtimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeEvent;

public class Settings extends AppCompatActivity {

    Database database;

    CheckBox inspectionTimeEnabledCheckBox, timeShownEnabledCheckBox;

    public static final String INSPECTION_ENABLED = "inspection";
    public static final String TIME_SHOWN_ENABLED = "time_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button resetAllTimesButton = (Button) findViewById(R.id.resetButton);
        Button exportDataButton = (Button) findViewById(R.id.exportDataButton);

        inspectionTimeEnabledCheckBox = (CheckBox) findViewById(R.id.inspectionEnabledCheckbox);
        timeShownEnabledCheckBox = (CheckBox) findViewById(R.id.timeVisibleCheckbox);

        // Bottom menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_settings:
                        break;
                    case R.id.ic_time_list:
                        Intent intent2 = new Intent(Settings.this, Time_List.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_timer:
                        Intent intent = new Intent(Settings.this, Timer.class);
                        startActivity(intent);
                        break;
                    case R.id.ic_statistics:
                        Intent intent3 = new Intent(Settings.this, Statistics.class);
                        startActivity(intent3);
                        break;
                    case R.id.ic_about:
                        Intent intent4 = new Intent(Settings.this, About.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });

        inspectionTimeEnabledCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                refreshSettings();
            }
        });

        timeShownEnabledCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                refreshSettings();
            }
        });

        resetAllTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(Settings.this);
                deleteAlert.setTitle("Reset times?");
                deleteAlert.setMessage("You are about to reset all saved times\n Are you sure?");
                deleteAlert.setPositiveButton("Yes, reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database = new Database(Settings.this);
                        database.resetData();
                        Toast.makeText(Settings.this, "Reseted times", Toast.LENGTH_LONG).show();
                    }
                });
                deleteAlert.setNegativeButton("No, don't reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                deleteAlert.create().show();
            }
        });

        refreshSettings();
    }

    public void saveSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(INSPECTION_ENABLED, inspectionTimeEnabledCheckBox.isChecked());
        editor.putBoolean(TIME_SHOWN_ENABLED, timeShownEnabledCheckBox.isChecked());
        editor.commit();
    }

    public void refreshSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        inspectionTimeEnabledCheckBox.setChecked(sharedPreferences.getBoolean(INSPECTION_ENABLED, true));
        timeShownEnabledCheckBox.setChecked(sharedPreferences.getBoolean(TIME_SHOWN_ENABLED, true));
    }

    @Override
    public void onBackPressed(){
    }
}
