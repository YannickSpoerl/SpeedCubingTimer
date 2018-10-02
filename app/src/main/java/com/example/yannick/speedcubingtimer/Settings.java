package com.example.yannick.speedcubingtimer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    Database database;
    CheckBox inspectionTimeEnabledCheckBox, timeShownEnabledCheckBox;
    public static final String INSPECTION_ENABLED = "inspection";
    public static final String TIME_SHOWN_ENABLED = "time_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Button resetAllTimesButton = (Button) findViewById(R.id.resetButton);
        Button exportDataButton = (Button) findViewById(R.id.exportDataButton);
        Button exportDataJSONButton = (Button) findViewById(R.id.exportJSONButton);
        inspectionTimeEnabledCheckBox = (CheckBox) findViewById(R.id.inspectionEnabledCheckbox);
        timeShownEnabledCheckBox = (CheckBox) findViewById(R.id.timeVisibleCheckbox);
        database = new Database(this);
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
                        startActivity( new Intent(Settings.this, Time_List.class));
                        break;
                    case R.id.ic_timer:
                        startActivity(new Intent(Settings.this, Timer.class));
                        break;
                    case R.id.ic_statistics:
                        startActivity(new Intent(Settings.this, Statistics.class));
                        break;
                    case R.id.ic_about:
                        startActivity(new Intent(Settings.this, About.class));
                        break;
                }
                return false;
            }
        });

        inspectionTimeEnabledCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });

        timeShownEnabledCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });

        resetAllTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAllTimes();
            }
        });

        exportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportTxtData();
            }
        });

        exportDataJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportJSONData();
            }
        });

        refreshSettings();
    }

    public void exportTxtData() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            ArrayList<TimeObject> timeList = database.getTimeListArray(18, 0);
            StringBuilder stringBuilder = new StringBuilder();
            for(TimeObject time : timeList){
                stringBuilder.append("\n");
                stringBuilder.append(time.toString());
            }
            File txtFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "exportedTimes.txt");
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(txtFile);
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.close();
            } catch(IOException e){
                Toast.makeText(this,"Couldn't export time.", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this,"Exported times to Downloads directory", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"Permission for writing to external storage not granted. Couldn't save file.",Toast.LENGTH_SHORT).show();
    }

    public void exportJSONData() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            ArrayList<TimeObject> timeList = database.getTimeListArray(18, 0);
            JSONArray jsonArray = new JSONArray();
            for(TimeObject time : timeList){
                try {
                    jsonArray.put(time.getJSON());
                } catch (JSONException j){
                    //ignore lol
                }
            }
            File jsonFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "exportedTimes.json");
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(jsonFile);
                fileOutputStream.write(jsonArray.toString().getBytes());
                fileOutputStream.close();
            } catch(IOException e){
                Toast.makeText(this,"Couldn't export time.", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this,"Exported times to Downloads directory", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"Permission for writing to external storage not granted. Couldn't save file.",Toast.LENGTH_SHORT).show();
    }

    public void resetAllTimes(){
        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(Settings.this);
        deleteAlert.setTitle("Reset times?");
        deleteAlert.setMessage("You are about to reset all saved times\n Are you sure?");
        deleteAlert.setPositiveButton("Yes, reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.resetData();
                Toast.makeText(Settings.this, "Reseted times", Toast.LENGTH_LONG).show();
            }
        });
        deleteAlert.setNegativeButton("No, don't reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        final AlertDialog alertDialog = deleteAlert.create();
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
    }

    public void saveSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(INSPECTION_ENABLED, inspectionTimeEnabledCheckBox.isChecked());
        editor.putBoolean(TIME_SHOWN_ENABLED, timeShownEnabledCheckBox.isChecked());
        editor.apply();
    }

    public void refreshSettings(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        inspectionTimeEnabledCheckBox.setChecked(sharedPreferences.getBoolean(INSPECTION_ENABLED, true));
        timeShownEnabledCheckBox.setChecked(sharedPreferences.getBoolean(TIME_SHOWN_ENABLED, true));
    }

    @Override
    public void onBackPressed(){
        //disable backButton
    }
}
