package com.example.yannick.speedcubingtimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class Time_List extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Database database;
    private ListView timeListListView;
    FloatingActionButton addFAB;
    private int selectedPuzzleID;
    private int selectedSortBy = 0; // 0 = latest, 1 = oldest, 2 = fastest, 3 = slowest

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time__list);
        addFAB = findViewById(R.id.addFAB);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePuzzleType();
            }
        });

        Spinner puzzleSpinner = findViewById(R.id.puzzle_spinner_list);
        ArrayAdapter<CharSequence> puzzleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.puzzles_list, android.R.layout.simple_spinner_item);
        puzzleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        puzzleSpinner.setAdapter(puzzleSpinnerAdapter);

        Spinner sortBySpinner = findViewById(R.id.sortby_spinner);
        ArrayAdapter<CharSequence> sortBySpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.sortBySpinnerArray, android.R.layout.simple_spinner_item);
        sortBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortBySpinnerAdapter);

        timeListListView = findViewById(R.id.timeList);
        database = new Database(this);

        //Bottom Menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_settings:
                        startActivity(new Intent(Time_List.this, Settings.class));
                        break;
                    case R.id.ic_timer:
                        startActivity(new Intent(Time_List.this, Timer.class));
                        break;
                    case R.id.ic_statistics:
                        startActivity(new Intent(Time_List.this, Statistics.class));
                        break;
                    case R.id.ic_about:
                        startActivity(new Intent(Time_List.this, About.class));
                        break;
                }
                return false;
            }
        });

        puzzleSpinner.setOnItemSelectedListener(this);
        sortBySpinner.setOnItemSelectedListener(this);
        populateListView();
    }

    private void populateListView(){
        ArrayList<TimeObject> timeListArray = database.getTimeListArray(selectedPuzzleID, selectedSortBy);
        timeListListView.setAdapter(new TimeListAdapter(this,R.layout.adapter_view_layout,timeListArray));
        // delete time dialoge
        timeListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final TimeObject clickedTime = (TimeObject) adapterView.getItemAtPosition(position);
               deleteShareMenu(clickedTime);
            }
        });
    }

    @Override
    public void onBackPressed(){
    }

    public void deleteShareMenu(final TimeObject time){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_action);
        builder.setItems(R.array.options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                switch (i){
                    case 0:
                        shareTime(time);
                        break;
                    case 1:
                        deleteTime(time);
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getId() == R.id.puzzle_spinner_list){
            if(position == 0){
              selectedPuzzleID = 18; // means all puzzle types are selected
            }
            else {
             selectedPuzzleID = position - 1;
            }
        }
        else if(adapterView.getId() == R.id.sortby_spinner){
            selectedSortBy = position;
        }
        populateListView();
    }

    public void shareTime(TimeObject clickedTime){
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.hey_i_want_to_share_this_time) + clickedTime.toString());
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_your_time_with_dialog)));
    }

    public void deleteTime(final TimeObject clickedTime){
        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(Time_List.this);
        deleteAlert.setTitle(R.string.delete_time);
        deleteAlert.setMessage(R.string.you_are_about_to_delete + clickedTime.toString() + "\n" + R.string.are_you_sure);
        deleteAlert.setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.deleteData(clickedTime);
                populateListView();
                Toast.makeText(Time_List.this, R.string.delete_selected_time, Toast.LENGTH_SHORT).show();
            }
        });
        deleteAlert.setNegativeButton(R.string.no_dont_delete, new DialogInterface.OnClickListener() {
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


    public void choosePuzzleType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_puzzle_type);
         builder.setItems(R.array.puzzles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                chooseTime(i);
            }
        });
        builder.show();
    }

    public void chooseTime(final int puzzleType){
        final AlertDialog.Builder addTimeDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        addTimeDialog.setTitle(R.string.enter_new_time);
        addTimeDialog.setView(dialogView);
        final NumberPicker minutesPicker = dialogView.findViewById(R.id.minutesPicker);
        final NumberPicker secondsPicker = dialogView.findViewById(R.id.secondsPicker);
        final NumberPicker millisecondsPicker;
        millisecondsPicker = (NumberPicker) dialogView.findViewById(R.id.millisecondsPicker);
        minutesPicker.setMaxValue(1339);
        minutesPicker.setMinValue(0);
        minutesPicker.setWrapSelectorWheel(true);
        minutesPicker.setValue(0);
        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setWrapSelectorWheel(true);
        secondsPicker.setValue(0);
        millisecondsPicker.setMaxValue(999);
        millisecondsPicker.setMinValue(0);
        millisecondsPicker.setWrapSelectorWheel(true);
        millisecondsPicker.setValue(0);
        addTimeDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chooseDate(puzzleType, minutesPicker.getValue(), secondsPicker.getValue(), millisecondsPicker.getValue());
            }
        });
        addTimeDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Time_List.this,R.string.no_time_created, Toast.LENGTH_SHORT).show();
            }
        });
        final AlertDialog alertDialog = addTimeDialog.create();
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
    }

    public void chooseDate(final int puzzleType, final long minutes, final long seconds, final long milliseconds){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) ;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final AlertDialog.Builder addTimeDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogdate, null);
        addTimeDialog.setTitle(R.string.enter_a_new_date);
        addTimeDialog.setView(dialogView);
        final NumberPicker dayPicker = (NumberPicker) dialogView.findViewById(R.id.dayPicker);
        final NumberPicker monthPicker = (NumberPicker) dialogView.findViewById(R.id.monthPicker);
        final NumberPicker yearPicker = (NumberPicker) dialogView.findViewById(R.id.yearPicker);
        dayPicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        dayPicker.setMinValue(1);
        dayPicker.setWrapSelectorWheel(true);
        dayPicker.setValue(day);
        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(1);
        monthPicker.setWrapSelectorWheel(true);
        monthPicker.setValue(month + 1);
        yearPicker.setMaxValue(year);
        yearPicker.setMinValue(1974);
        yearPicker.setWrapSelectorWheel(true);
        yearPicker.setValue(year);
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int newMonth) {
                calendar.set(Calendar.MONTH, newMonth -1);
                dayPicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        });
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int newYear) {
                calendar.set(Calendar.YEAR, newYear);
                dayPicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        });
        addTimeDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveTime(puzzleType, minutes, seconds, milliseconds, dayPicker.getValue(), monthPicker.getValue(), yearPicker.getValue());
            }
        });
        addTimeDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Time_List.this,R.string.no_time_created, Toast.LENGTH_SHORT).show();
            }
        });
        final AlertDialog alertDialog = addTimeDialog.create();
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
    }

    public void saveTime(int puzzleType, long minutes, long seconds, long milliseconds, int day, int month, int year){
        TimeObject newTime = new TimeObject(minutes, seconds, milliseconds, puzzleType, day, month, year);
        database.addData(newTime);
        Toast.makeText(this,R.string.time_added, Toast.LENGTH_SHORT).show();
        populateListView();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // do nothing
    }
}
