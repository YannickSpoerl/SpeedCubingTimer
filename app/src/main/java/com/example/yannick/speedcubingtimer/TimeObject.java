package com.example.yannick.speedcubingtimer;

import org.json.JSONException;
import org.json.JSONObject;

public class TimeObject {

    private long minutes, seconds, milliseconds;
    private int day, month, year, puzzleID;

    TimeObject(long minutes, long seconds, long milliseconds, int puzzleID, int day, int month, int year){
        this.day = day;
        this.milliseconds = milliseconds;
        this.minutes = minutes;
        this.puzzleID = puzzleID;
        this.seconds = seconds;
        this.month = month;
        this.year = year;
    }

    public String toString(){
        String time_string = "";
        if(minutes < 10){
            time_string += "0";
        }
        time_string += minutes + ":";
        if(seconds < 10){
            time_string += "0";
        }
        time_string += seconds + ".";
        if(milliseconds < 10){
            time_string += "00";
            time_string += String.valueOf(milliseconds).substring(0,1);
        }
        else if(milliseconds < 100){
            time_string += "0";
            time_string += String.valueOf(milliseconds).substring(0,2);
        }
        else {
            time_string += String.valueOf(milliseconds).substring(0,3);
        }
        String date_string = "";
        if(day < 10){
            date_string += "0";
        }
        date_string += day + ".";
        if(month < 10){
            date_string += "0";
        }
        date_string += month + "." + year;
        return this.getPuzzleString() + ", " + time_string + " from " + date_string;
    }

    long getMinutes() {
        return minutes;
    }

    long getSeconds() {
        return seconds;
    }

    long getMilliseconds() {
        return milliseconds;
    }

    int getDay() {
        return day;
    }

    int getMonth() {
        return month;
    }

    int getYear() {
        return year;
    }

    int getPuzzleID() {
        return puzzleID;
    }

    String getPuzzleString(){
        return getPuzzleByID(this.puzzleID);
    }

    JSONObject getJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("puzzletype",this.getPuzzleString());
        JSONObject jsonObjectTime = new JSONObject();
        jsonObjectTime.put("minutes", this.minutes);
        jsonObjectTime.put("seconds", this.seconds);
        jsonObjectTime.put("milliseconds", this.milliseconds);
        jsonObject.put("time", jsonObjectTime);
        JSONObject jsonObjectDate = new JSONObject();
        jsonObjectDate.put("day", this.day);
        jsonObjectDate.put("month", this.month);
        jsonObjectDate.put("year", this.year);
        jsonObject.put("date", jsonObjectDate);
        return jsonObject;
    }

    public static String getPuzzleByID(int id){
        switch(id){
            case 0:
                return "3x3";
            case 1:
                return "4x4";
            case 2:
                return "5x5";
            case 3:
                return "2x2";
            case 4:
                return "3x3 BLD";
            case 5:
                return "3x3 OH";
            case 6:
                return "3x3 FM";
            case 7:
                return "3x3 FT";
            case 8:
                return "Megaminx";
            case 9:
                return "Pyraminx";
            case 10:
                return "Square-1";
            case 11:
                return "Clock";
            case 12:
                return "Skewb";
            case 13:
                return "6x6";
            case 14:
                return "7x7";
            case 15:
                return "4x4 BLD";
            case 16:
                return "5x5 BLD";
            case 17:
                return "3x3 MBLD";
        }
        return "Not a puzzle";
    }

    long getTotalDuration(){
        return this.milliseconds + (this.minutes * 60 * 1000) + (this.seconds * 1000);
    }
}
