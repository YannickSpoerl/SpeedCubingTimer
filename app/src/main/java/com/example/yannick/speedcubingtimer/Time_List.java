package com.example.yannick.speedcubingtimer;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private DatePickerDialog.OnDateSetListener dateSetListener;
    FloatingActionButton menuFAB, shareFAB, deleteFAB, addFAB;
    Animation fabOpen, fabClose, fabRotateClockwise, fabRotateAnticlockwise;
    private int selectedPuzzleID;
    private int selectedSortBy = 0; // 0 = latest, 1 = oldest, 2 = fastest, 3 = slowest
    private int floatingActionButtonMode = 0; // 0 = share, 1 = delete
    boolean menuFABopen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time__list);
        menuFAB = (FloatingActionButton) findViewById(R.id.menuFAB);
        shareFAB = (FloatingActionButton) findViewById(R.id.shareFAB);
        deleteFAB = (FloatingActionButton) findViewById(R.id.deleteFAB);
        addFAB = (FloatingActionButton) findViewById(R.id.addFAB);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRotateAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        menuFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClicked();
            }
        });

        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switchFABmode();
            }
        });

        deleteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFABmode();
            }
        });

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePuzzleType();
            }
        });

        Spinner puzzleSpinner = (Spinner) findViewById(R.id.puzzle_spinner_list);
        ArrayAdapter<CharSequence> puzzleSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.puzzles_list, android.R.layout.simple_spinner_item);
        puzzleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        puzzleSpinner.setAdapter(puzzleSpinnerAdapter);

        Spinner sortBySpinner = (Spinner) findViewById(R.id.sortby_spinner);
        ArrayAdapter<CharSequence> sortBySpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.sortBySpinnerArray, android.R.layout.simple_spinner_item);
        sortBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortBySpinnerAdapter);

        timeListListView = (ListView) findViewById(R.id.timeList);
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
                switch (floatingActionButtonMode){
                    case 0:
                        shareTime(clickedTime);
                        break;
                    case 1:
                        deleteTime(clickedTime);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
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
        intent.putExtra(Intent.EXTRA_TEXT,"Hey, I want to share this time with you: " + clickedTime.toString());
        startActivity(Intent.createChooser(intent, "Share your time via:"));
    }

    public void deleteTime(final TimeObject clickedTime){
        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(Time_List.this);
        deleteAlert.setTitle("Delete Time?");
        deleteAlert.setMessage("You are about to delete " + clickedTime.toString() + "\n Are you sure?");
        deleteAlert.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.deleteData(clickedTime);
                populateListView();
                Toast.makeText(Time_List.this, "Deleted selected time", Toast.LENGTH_SHORT).show();
            }
        });
        deleteAlert.setNegativeButton("No, don't delete", new DialogInterface.OnClickListener() {
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

    public static void setButtonTint(FloatingActionButton button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            ((TintableBackgroundView) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }

    public void switchFABmode(){
        switch (floatingActionButtonMode){
            case 0:
                setButtonTint(shareFAB,getResources().getColorStateList(R.color.colorGrey));
                setButtonTint(deleteFAB,getResources().getColorStateList(R.color.colorPrimary));
                floatingActionButtonMode = 1;
                Toast.makeText(this, "Tap time to delete",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                shareFAB.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                deleteFAB.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrey));
                Toast.makeText(this,"Tap time to share", Toast.LENGTH_SHORT).show();
                floatingActionButtonMode = 0;
                break;
        }
    }

    public void choosePuzzleType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose the type of puzzle");
        String[] puzzleTypes = {"3x3", "4x4", "5x5", "2x2", "3x3 BLD", "3x3 OH", "3x3 FM", "3x3 FT", "Megaminx", "Pyraminx", "Square-1", "Clock", "Skewb", "6x6", "7x7", "4x4 BLD", "5x5 BLD", "3x3 MBLD"};
         builder.setItems(puzzleTypes, new DialogInterface.OnClickListener() {
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
        addTimeDialog.setTitle("Enter new time");
        addTimeDialog.setView(dialogView);
        final NumberPicker minutesPicker = (NumberPicker) dialogView.findViewById(R.id.minutesPicker);
        final NumberPicker secondsPicker = (NumberPicker) dialogView.findViewById(R.id.secondsPicker);
        final NumberPicker millisecondsPicker = (NumberPicker) dialogView.findViewById(R.id.millisecondsPicker);
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
        addTimeDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chooseDate(puzzleType, minutesPicker.getValue(), secondsPicker.getValue(), millisecondsPicker.getValue());
            }
        });
        addTimeDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Time_List.this,"No time created", Toast.LENGTH_SHORT).show();
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
        addTimeDialog.setTitle("Enter a new date");
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
        addTimeDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveTime(puzzleType, minutes, seconds, milliseconds, dayPicker.getValue(), monthPicker.getValue(), yearPicker.getValue());
            }
        });
        addTimeDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Time_List.this,"No time created", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this,"Time added", Toast.LENGTH_SHORT).show();
        populateListView();
    }

    public void menuClicked(){
        if(menuFABopen){
            shareFAB.startAnimation(fabClose);
            deleteFAB.startAnimation(fabClose);
            menuFAB.startAnimation(fabRotateAnticlockwise);
            shareFAB.setClickable(false);
            deleteFAB.setClickable(false);
            menuFABopen = false;
        }
        else{
            shareFAB.startAnimation(fabOpen);
            deleteFAB.startAnimation(fabOpen);
            menuFAB.startAnimation(fabRotateClockwise);
            shareFAB.setClickable(true);
            deleteFAB.setClickable(true);
            menuFABopen = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // do nothing
    }
}
