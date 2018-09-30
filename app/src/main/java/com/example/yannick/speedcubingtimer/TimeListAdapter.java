package com.example.yannick.speedcubingtimer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// this class is responsible for building the ListView in the Time_List activity
public class TimeListAdapter extends ArrayAdapter<TimeObject> {

    private Context context;
    int resource;
    private int lastPosition = -1;

    static class ViewHolder {
        TextView time;
        TextView date;
        TextView puzzle;
    }

    public TimeListAdapter(Context context, int resource, ArrayList<TimeObject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
     }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String puzzlestring = getItem(position).getPuzzleString();
        String timeString = "";
        if(getItem(position).getMinutes() < 10){
            timeString += "0" + getItem(position).getMinutes() + ":";
        }
        else {
            timeString += getItem(position).getMinutes() + ":";
        }
        if(getItem(position).getSeconds() < 10){
            timeString += "0" + getItem(position).getSeconds() + ".";
        }
        else {
            timeString +=getItem(position).getSeconds() + ".";
        }
        if(getItem(position).getMilliseconds()<10){
            timeString += "00" + getItem(position).getMilliseconds();
        }
        else if(getItem(position).getMilliseconds() < 100){
            timeString += "0" + getItem(position).getMilliseconds();
        }
        else {
            timeString += String.valueOf(getItem(position).getMilliseconds()).substring(0,3);
        }
        String dateString = "";
        if(getItem(position).getDay() < 10){
            dateString += "0" + getItem(position).getDay() + ".";
        }
        else {
            dateString += getItem(position).getDay() + ".";
        }
        if(getItem(position).getMonth()<10){
            dateString += "0" + getItem(position).getMonth() + ".";
        }
        else {
            dateString +=  getItem(position).getMonth() + ".";
        }
        dateString += getItem(position).getYear();
        final View result;
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time_tv);
            holder.date = (TextView) convertView.findViewById(R.id.date_tv);
            holder.puzzle = (TextView) convertView.findViewById(R.id.puzzle_tv);
            result = convertView;
            convertView.setTag(holder);
        }
        else {
           holder = (ViewHolder) convertView.getTag();
           result = convertView;
        }
        // scrolling animation when loading items further up or down
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.loading_down_anim : R.anim.loading_up_anim);
        result.startAnimation(animation);
        lastPosition = position;
        holder.time.setText(timeString);
        holder.date.setText(dateString);
        holder.puzzle.setText(puzzlestring);
        return convertView;
    }
}
