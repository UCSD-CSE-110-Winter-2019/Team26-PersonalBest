package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

public class Settings {
    private SharedPreferences sharedPreferences;
    private TimeStamper timeStamper;
    private int defGoal = 5000;
    private IDataAdapter dataAdapter;

    public Settings (Context context, TimeStamper timeStamper, boolean DEBUG) {
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE );
        this.timeStamper = timeStamper;
        this.dataAdapter = IDatabaseAdapterFactory.create(DEBUG, context);
    }

    public void saveHeight( int feet, int inches ) {
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putInt("height", feet*12 + inches);
        editor.apply();
    }

    public int getHeight(){
        return sharedPreferences.getInt("height", 0);
    }

    public void saveGoal( int goal ) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch( timeStamper.getDayOfWeek() ) {
            case SUNDAY:
                editor.putInt("goal_sunday", goal); break;
            case MONDAY:
                editor.putInt("goal_monday", goal); break;
            case TUESDAY:
                editor.putInt("goal_tuesday", goal); break;
            case WEDNESDAY:
                editor.putInt("goal_wednesday", goal); break;
            case THURSDAY:
                editor.putInt("goal_thursday", goal); break;
            case FRIDAY:
                editor.putInt("goal_friday", goal); break;
            case SATURDAY:
                editor.putInt("goal_saturday", goal); break;
                default:
                    break;
        }
    }

    public int getGoal() {
        switch( timeStamper.getDayOfWeek() ) {
            case SUNDAY:
                if( !sharedPreferences.contains("goal_sunday"))
                    saveGoal(defGoal);
                return sharedPreferences.getInt("goal_sunday", defGoal);
            case MONDAY:
                if( !sharedPreferences.contains("goal_monday"))
                    saveGoal(defGoal);
                return sharedPreferences.getInt("goal_monday", defGoal);
            case TUESDAY:
                if( !sharedPreferences.contains("goal_tuesday"))
                    saveGoal(defGoal);
                return sharedPreferences.getInt("goal_tuesday", defGoal);
            case WEDNESDAY:
                if( !sharedPreferences.contains("goal_wednesday"))
                    saveGoal(defGoal);
                return sharedPreferences.getInt("goal_wednesday", defGoal);
            case THURSDAY:
                if( !sharedPreferences.contains("goal_thursday"))
                    saveGoal(defGoal);
                return sharedPreferences.getInt("goal_thursday", defGoal);
            case FRIDAY:
                if( !sharedPreferences.contains("goal_friday"))
                    saveGoal(defGoal);
                return sharedPreferences.getInt("goal_friday", defGoal);
            case SATURDAY:
                if( !sharedPreferences.contains("goal_saturday"))
                    saveGoal(defGoal);
                return sharedPreferences.getInt("goal_saturday", defGoal);
        }
        return defGoal;
    }

    public List<Integer> getGoalsOfWeek(){
        List<Integer> list = new ArrayList<Integer>();
        list.add(sharedPreferences.getInt("goal_sunday", defGoal));
        list.add(sharedPreferences.getInt("goal_monday", defGoal));
        list.add(sharedPreferences.getInt("goal_tuesday", defGoal));
        list.add(sharedPreferences.getInt("goal_wednesday", defGoal));
        list.add(sharedPreferences.getInt("goal_thursday", defGoal));
        list.add(sharedPreferences.getInt("goal_friday", defGoal));
        list.add(sharedPreferences.getInt("goal_saturday", defGoal));
        return list;
    }
}
