package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

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
    String TAG = "Settings Activity";
    private TimeStamper timeStamper;
    private int defGoal = 5000;
    private String dayID;
    private String DOCUMENT_KEY;
    String COLLECTION_KEY = "users";
    String RECORD_KEY = "record";
    private User user;

    //variables for Day objects
    private Day dayInfo;
    private Day dayInfo1;
    private int userHeight;
    private int goal;
    private int currentStep;
    private int totalSteps;
    private int walkSteps;
    private Date date;
    DocumentReference user_data;

    public Settings (Context context, TimeStamper timeStamper) {
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE );
        this.timeStamper = timeStamper;
        this.DOCUMENT_KEY = GoogleSignIn.getLastSignedInAccount(context).getEmail();
    }

    public Settings(Context context, String dayID, String DOCUMENT_KEY)
    {
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE );
        this.dayID = dayID;
        this.DOCUMENT_KEY = DOCUMENT_KEY;
        userHeight = 0;
    }
    public Settings(Context context, String DOCUMENT_KEY)
    {
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE );
        this.DOCUMENT_KEY = DOCUMENT_KEY;
        user_data = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(GoogleSignIn.getLastSignedInAccount(context).getEmail());
    }

    public void setDOCUMENT_KEY(String DOCUMENT_KEY)
    {
        this.DOCUMENT_KEY = DOCUMENT_KEY;
    }

    public void setDayID(String dayID)
    {
        this.dayID = dayID;
    }
    public void setTimeStamper(TimeStamper timeStamper)
    {
        this.timeStamper = timeStamper;
    }

    public void saveHeight( int feet, int inches ) {
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putInt("height", feet*12 + inches);
        editor.apply();
    }

    public void saveUserHeight(int feet, int inches)
    {
        DocumentReference user_data = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY);
        int new_height = feet*12 + inches;
        user_data.update("height", new_height).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Successfully update user height");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error to overwrite user height",e);
            }
        });

    }

    public int getUserHeight()
    {
        DocumentReference user_data = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY);

        user_data.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                userHeight = user.getHeight();
            }
        });
        return userHeight;
    }

    public void saveTodayGoal(int new_goal)
    {
        DocumentReference user_record = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(RECORD_KEY)
                .document(getTodayID());

        user_record.update("goal", new_goal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Successfully overwritten the new goal");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating new goal",e);
            }
        });

        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch( timeStamper.getDayOfWeek() ) {
            case SUNDAY:
                editor.putInt("goal_sunday", new_goal); break;
            case MONDAY:
                editor.putInt("goal_monday", new_goal); break;
            case TUESDAY:
                editor.putInt("goal_tuesday", new_goal); break;
            case WEDNESDAY:
                editor.putInt("goal_wednesday", new_goal); break;
            case THURSDAY:
                editor.putInt("goal_thursday", new_goal); break;
            case FRIDAY:
                editor.putInt("goal_friday", new_goal); break;
            case SATURDAY:
                editor.putInt("goal_saturday", new_goal); break;
            default:
                break;
        }
        editor.apply();
        saveGoal(new_goal);

        setupTmrGoal(new_goal);
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
        editor.apply();
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

    public void setGoal(int goal)
    {
        this.goal = goal;
    }

    public int getCurrentStep()
    {
        DocumentReference user_record = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(RECORD_KEY)
                .document(getTodayID());

        user_record.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                dayInfo = documentSnapshot.toObject(Day.class);
                setCurrentStep((int)(dayInfo.getTotalSteps() + dayInfo.getWalkSteps()));
            }
        });
        return currentStep;
    }
    public void setCurrentStep(int step)
    {
        this.currentStep = step ;
    }

    public String getTmrID()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date date = cal.getTime();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH));
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String dayID = month + day + year;
        return dayID;
    }

    public Date getTmrDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date date = cal.getTime();
        return date;
    }

    public void setupTmrGoal(int new_goal)
    {
        DocumentReference user_record = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(RECORD_KEY)
                .document(getTmrID());

        user_record.set(new Day(new_goal, 0 ,0, getTmrID(), getTmrDate() ))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "update the yesterday info successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating the yesterday info");
                    }
                });
    }

    public String getTodayID()
    {
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH));
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        return  (month + day + year);
    }

    /*public int getGoal() {
        DocumentReference user_record = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(RECORD_KEY)
                .document(getTodayID());

        user_record.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dayInfo = documentSnapshot.toObject(Day.class);
                setGoal((int)dayInfo.getGoal());
            }
        });
        return goal;
    }*/
}
