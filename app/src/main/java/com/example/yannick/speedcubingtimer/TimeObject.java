package com.example.yannick.speedcubingtimer;

public class TimeObject {

    private long minutes, seconds, milliseconds;
    private int day, month, year;
    private int puzzleID;

    public TimeObject(long minutes, long seconds, long milliseconds, int puzzleID, int day, int month, int year){
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
        }
        else if(milliseconds < 100){
            time_string += "0";
        }
        time_string += String.valueOf(milliseconds).substring(0,3);
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

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getPuzzleID() {
        return puzzleID;
    }

    public String getPuzzleString(){
        return getPuzzleByID(this.puzzleID);
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
}
