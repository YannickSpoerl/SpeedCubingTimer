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
}
