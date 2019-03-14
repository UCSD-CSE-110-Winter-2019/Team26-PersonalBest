package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

// reference: https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial

public class StepCountActivity extends AppCompatActivity {

    private static final String TAG = "StepCountActivity";
    private static boolean DEBUG;
    private static boolean ESPRESSO;

    private UpdateStep updateStep;

    private TextView textSteps;
    private TextView textWalkData;
    private Button btnStartWalk;
    private Button btnEndWalk;

    FitnessService fitnessService;
    private long previousDaySteps = 0;
    private long lastEncouragingMessageSteps = 0;
    private boolean goalCompleted;
    private List<Integer> stepCounts = new ArrayList<>();
    private List<ArrayList<Walk>> walkData = new ArrayList<>();
    List<Walk> walksToday;

    private long startTimeStamp = -1;
    private long initialSteps = 0;
    private long currentDate;

    //
    private Walk currentWalk;
    private Day today = new Day();
    private User user = new User();
    private int height;

    IDataAdapter dataAdapter;
    TimeStamper timeStamper;
    Settings settings;

    // BarChart object
    CombinedChart sevenDayBarchart;
    CombinedChart twentyEightDayBarchart;
    private BarChart createBarChart;
    private BarChart createBarChart2;
    boolean month;

    //firebase object
    DocumentReference user_data;
    CollectionReference user_list;
    String COLLECTION_KEY = "users";
    String RECORD_KEY = "record";
    String DOCUMENT_KEY;


    /* ================
    Description: keep the UI update with the current number of taken steps
    Pre:
    Post:
    ================ */
    private class UpdateStep extends AsyncTask<Integer, Integer, Integer> {
        private boolean run = true;
        private int resp;

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                resp = params[0];
                while(run) {
                    if( !timeStamper.isToday(currentDate) ) {
                        initializeNewDay();
                        currentDate = timeStamper.now();
                    }
                    fitnessService.updateStepCount(StepCountActivity.this::setStepCount);
                    stepCounts.clear();
                    fitnessService.getStepsCount(timeStamper.lastTwentyEightDays(), timeStamper.today(), stepCounts);

                    walkData.clear();
                    long ts = timeStamper.startOfDay(timeStamper.lastTwentyEightDays());
                    for(int i = 0; i < 28; i++) {
                        ArrayList<Walk> list = new ArrayList<>();
                        walkData.add(list);
                        fitnessService.getWalks(ts, timeStamper.endOfDay(ts), list);
                        if(timeStamper.isToday(ts))
                            walksToday = list;
                        ts = timeStamper.nextDay(ts);
                    }

                    if(!stepCounts.isEmpty() && !DEBUG )
                    {
                        //updateDataToFirebase();
                    }

                    Thread.sleep(10000);
                    for(List<Walk> walklist : walkData) {
                        Log.i(TAG, walklist.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            return resp;
        }

        void setRun(boolean run) {
            this.run = run;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        if(getIntent().getExtras() != null) {
            DEBUG = getIntent().getExtras().getBoolean("DEBUG");
            ESPRESSO = getIntent().getExtras().getBoolean("ESPRESSO");
        }

        DOCUMENT_KEY = getIntent().getExtras().getString("DOCUMENT_KEY");


        fitnessService = FitnessServiceFactory.create(DEBUG, this);
        dataAdapter = IDatabaseAdapterFactory.create(DEBUG, this.getApplicationContext());

        timeStamper = new ConcreteTimeStamper();
        currentDate = timeStamper.now();

        FirebaseApp.initializeApp(getApplicationContext());
        user_data = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY);
        //dataAdapter.getUser(x -> checkHeight(x));
        if(!DEBUG)
        {
            user_data = FirebaseFirestore.getInstance()
                    .collection(COLLECTION_KEY)
                    .document(DOCUMENT_KEY);
            dataAdapter.getUser(x -> checkHeight(x));
            //checkHeight();
        }



        fitnessService.setup();
        setupBarChart();
        buttonWalk();
    }

    public void setupBarChart()
    {
        sevenDayBarchart = findViewById(R.id.chart1);
        twentyEightDayBarchart = findViewById(R.id.chart2);





        //createBarChart.setDOCUMENT_KEY(DOCUMENT_KEY);
        //createBarChart.setSize(7);
        //createBarChart.draw();
        if(!DEBUG)
        {
            //updateDataToFirebase();
        }

        createBarChart = new BarChart(getApplicationContext(),sevenDayBarchart);
        dataAdapter.getDays(7,listInfo -> createBarChart.draw(listInfo) );
        sevenDayBarchart.setVisibility(View.VISIBLE);

        createBarChart2 = new BarChart(getApplicationContext(),twentyEightDayBarchart);
        dataAdapter.getDays(28,listInfo-> createBarChart2.draw(listInfo));
        twentyEightDayBarchart.setVisibility(View.GONE);

        Switch sw = findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    month = true;
                    sevenDayBarchart.setVisibility(View.GONE);
                    twentyEightDayBarchart.setVisibility(View.VISIBLE);
                } else {
                    month = false;
                    sevenDayBarchart.setVisibility(View.VISIBLE);
                    twentyEightDayBarchart.setVisibility(View.GONE);
                }
            }
        });
    }
    public void buttonWalk()
    {
        textSteps = findViewById(R.id.textSteps);
        btnStartWalk = findViewById(R.id.btnStartWalk);
        btnEndWalk = findViewById(R.id.btnEndWalk);
        textWalkData = findViewById(R.id.textWalkData);
        btnStartWalk.setOnClickListener(view -> {
            if(startTimeStamp == -1) {
                startTimeStamp = timeStamper.now();
                initialSteps = today.totalSteps;
                SharedPreferences.Editor editor = getSharedPreferences("walk", MODE_PRIVATE).edit();
                editor.putLong("startTimeStamp", startTimeStamp)
                        .putLong("initialSteps", initialSteps).apply();
                btnStartWalk.setVisibility(View.GONE);
                btnEndWalk.setVisibility(View.VISIBLE);
                currentWalk = new Walk(0, startTimeStamp);
                updateWalkData();
            }
        });
        btnEndWalk.setOnClickListener(view -> {
            if(startTimeStamp != -1) {
                // ensure that any walk will end at the end of the day - splits off by midnight
                if(!timeStamper.isToday(startTimeStamp)) {
                    fitnessService.walk(startTimeStamp, timeStamper.endOfDay(startTimeStamp));
                    startTimeStamp = timeStamper.startOfDay(timeStamper.now());
                }
                fitnessService.walk(startTimeStamp, timeStamper.now());
                startTimeStamp = -1;
                currentWalk = null;
                SharedPreferences.Editor editor = getSharedPreferences("walk", MODE_PRIVATE).edit();
                editor.putLong("startTimeStamp", -1).apply();
                btnStartWalk.setVisibility(View.VISIBLE);
                btnEndWalk.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //
        // createBarChart.draw();
        if(updateStep != null && !updateStep.isCancelled()) {
            updateStep.cancel(true);
        }

        // If espresso test is not running, start async task
        if(!ESPRESSO) {
            updateStep = new UpdateStep();
            updateStep.execute(-1);
        }

        if(!stepCounts.isEmpty() && !DEBUG)
        {
            //updateDataToFirebase();
            //dcreateBarChart.draw();
            //createBarChart2.draw();
        }
        /*dataAdapter.getDays(7,weekInfo -> {
            Log.d(TAG, weekInfo.toString());
            createBarChart.draw(weekInfo);
        });*/
        //dataAdapter.getDays(7,weekInfo -> createBarChart.draw(weekInfo));
        //createBarChart.draw();
        //createBarChart2.draw();
        /*sevenDayBarchart.setVisibility(View.VISIBLE);
        twentyEightDayBarchart.setVisibility(View.GONE);
        if(!month)
        {
            twentyEightDayBarchart.setVisibility(View.GONE);
            sevenDayBarchart.setVisibility(View.VISIBLE);
        }
        else
        {
            twentyEightDayBarchart.setVisibility(View.VISIBLE);
            sevenDayBarchart.setVisibility(View.GONE);
        }*/

        Settings settings = new Settings(getApplicationContext(), timeStamper);
        settings.setDOCUMENT_KEY(DOCUMENT_KEY);
        today.goal = settings.getGoal();
        user.height = settings.getUserHeight();
        setStepCount(today.totalSteps);

        // Check if the user started a walk and has not stopped it
        checkStartWalk();
    }

    public void checkStartWalk()
    {
        SharedPreferences walkInfo = getSharedPreferences("walk", MODE_PRIVATE );
        startTimeStamp = walkInfo.getLong("startTimeStamp", -1);
        initialSteps = walkInfo.getLong("initialSteps", 0);
        if(startTimeStamp != -1) {
            if (!timeStamper.isToday(startTimeStamp)) {
                fitnessService.walk(startTimeStamp, timeStamper.endOfDay(startTimeStamp)); // terminate walk at end of day
                SharedPreferences.Editor e = walkInfo.edit();
                e.putLong("startTimeStamp", -1);
                e.apply();
                btnStartWalk.setVisibility(View.VISIBLE);
                btnEndWalk.setVisibility(View.GONE);
            } else {
                btnStartWalk.setVisibility(View.GONE);
                btnEndWalk.setVisibility(View.VISIBLE);
                currentWalk = new Walk(today.totalSteps - initialSteps, startTimeStamp);
            }
        }
    }

    /*public void checkHeight()
    {
        //dataAdapter.getUser(x -> getHeight(x));
        user_data.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                if (user.getHeight() == 0)
                {
                    String name = user.getName();
                    String email = user.getEmail();
                    String userID = user.getUid();
                    launchGetHeightActivity(name, email, userID);
                }
            }
        });
    }*/

    public void checkHeight(User user)
    {
        this.height = user.getHeight();
        if(height == 0)
        {
            String name = user.getName();
            String email = user.getEmail();
            String userID = user.getUid();
            launchGetHeightActivity(name, email, userID);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(updateStep != null) {
            updateStep.setRun(false);
            updateStep.cancel(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK && !ESPRESSO) {
            if (requestCode == fitnessService.getRequestCode()) {
                if(updateStep != null && !updateStep.isCancelled()) updateStep.cancel(true);
                updateStep = new UpdateStep();
                updateStep.execute(-1);
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        Settings settings = new Settings(getApplicationContext(), timeStamper);
        today.totalSteps = stepCount;
        today.goal = settings.getGoal();
        textSteps.setText(String.format(Locale.getDefault(),"%d/%d steps", today.totalSteps, today.goal));
        updateWalkData();
        if(today.totalSteps >= today.goal && !goalCompleted && today.goal != 0) {
            //do dialog box as well.

            int suggestedGoalNum = (int) today.goal;

            if(today.goal + 500 <= 15000){
                suggestedGoalNum += 500;
            }
            else{
                suggestedGoalNum = 15000;
            }
            createAlertDialog(suggestedGoalNum);

            Toast completeGoalToast = Toast.makeText(getApplicationContext(),
                    String.format(Locale.getDefault(),"Congratulations, you've completed your goal of %d steps today!", today.goal),
                    Toast.LENGTH_SHORT);

            completeGoalToast.show();
            goalCompleted = true;
        } else {
            goalCompleted = false;
            if(previousDaySteps != 0 && today.totalSteps < today.goal ) {
                int improvementPercentage = (int) ((today.totalSteps - previousDaySteps) / previousDaySteps)*100;
                if (lastEncouragingMessageSteps == 0 && previousDaySteps + 500 <= today.totalSteps) {
                    lastEncouragingMessageSteps = today.totalSteps - (today.totalSteps - previousDaySteps) % 500;
                    String message = String.format(Locale.US, "Good job! You've improved by %d%% from yesterday", improvementPercentage);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } else if (lastEncouragingMessageSteps + 500 <= today.totalSteps && lastEncouragingMessageSteps != 0 ) {
                    lastEncouragingMessageSteps = today.totalSteps - (today.totalSteps - previousDaySteps) % 500;
                    String message = String.format(Locale.US, "Good job! You've improved by %d%% from yesterday", improvementPercentage);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public void updateWalkData() {
        if(startTimeStamp != -1 ) {
            initialSteps = getSharedPreferences("walk", MODE_PRIVATE).getLong("initialSteps", 0);
            currentWalk.setSteps(today.totalSteps - initialSteps);
            textWalkData.setText(String.format(Locale.getDefault(),
                    "Current walk:\nWalk duration: %s\n%d steps taken\nDistance walked: %.1f feet\nAverage speed: %.1fmph",
                    timeStamper.durationToString(timeStamper.now() - startTimeStamp),
                    currentWalk.getSteps(),
                    currentWalk.stepsToFeet(user.height),
                    currentWalk.averageMph(user.height)));
        } else if(walksToday != null && !walksToday.isEmpty()) {
            Walk lastWalk = walksToday.get(walksToday.size() - 1);
            textWalkData.setText(String.format(Locale.getDefault(),
                    "Last walk:\nWalk duration: %s\n%d steps taken\nDistance walked: %.1f feet\nAverage speed: %.1fmph",
                    timeStamper.durationToString(lastWalk.getDurationInMillis()),
                    lastWalk.getSteps(),
                    lastWalk.stepsToFeet(user.height),
                    lastWalk.averageMph(user.height)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_step_count, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(StepCountActivity.this, SettingsActivity.class);
            intent.putExtra("DOCUMENT_KEY", DOCUMENT_KEY);
            intent.putExtra("DEBUG", DEBUG);
            StepCountActivity.this.startActivity(intent);
            return true;
        }
        if( id == R.id.action_friends_list ) {
            Intent intent = new Intent(StepCountActivity.this, FriendsListActivity.class);
            intent.putExtra("DEBUG", DEBUG);
            StepCountActivity.this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createAlertDialog(final int suggestedGoal) {
        AlertDialog alertDialog = new AlertDialog.Builder(StepCountActivity.this).create();
        alertDialog.setTitle("Suggesting Goals");

        alertDialog.setMessage("Would you like to set next weeks steps to be " + suggestedGoal);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", (dialog, which) -> {
                    Settings settings = new Settings(getApplicationContext(), timeStamper);
                    settings.saveGoal(suggestedGoal);
                    if(!DEBUG)
                    {
                        settings.setDOCUMENT_KEY(DOCUMENT_KEY);
                        settings.saveTodayGoal(suggestedGoal);
                    }
                    dialog.dismiss();
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    public void launchGetHeightActivity(String name, String email, String userID) {
        Intent intent = new Intent(this, GetHeightActivity.class);
        intent.putExtra("DEBUG", DEBUG);
        intent.putExtra("EMAIL", email);
        intent.putExtra("NAME", name );
        intent.putExtra("UID", userID);
        startActivity(intent);
    }

    /**
     * Resets previousDaySteps and saves previous day's goal as new goal
     */
    public void initializeNewDay() {
        long prev[] = timeStamper.getPreviousDay();
        List<Integer> previousSteps = new ArrayList<>();
        try {
            fitnessService.getStepsCount(prev[0], prev[1], previousSteps);
            sleep(1000);
        } catch( Exception e ) {
            e.printStackTrace();
        }
        if(previousSteps.size() == 0)
            previousSteps.add(0);
        previousDaySteps = previousSteps.get(0);
        Log.i(TAG, String.format("New day. Setting previous day's steps to %d", previousDaySteps));
        Settings settings = new Settings(getApplicationContext(), timeStamper);
        settings.saveGoal((int) today.goal);
        if(!DEBUG)
        {
            settings.setDOCUMENT_KEY(DOCUMENT_KEY);
            settings.saveTodayGoal((int)today.goal);
        }
        lastEncouragingMessageSteps = 0;
    }

    public void updateDataToFirebase()
    {
        int[] stepList = new int[28];
        int count = 0;
        for(Integer i: stepCounts)
        {
            stepList[count] = stepList[count] + i;
            count++;
        }
        int[] walkList = new int[28];
        count = 0;
        for(ArrayList<Walk> i : walkData)
        {
            walkList[count] = 0;
            for(Walk j: i)
            {
                walkList[count] = walkList[count] + (int)j.getSteps();
            }
            count++;
        }

        for(int i = 0; i < 28; i++)
        {
            DocumentReference user_record = FirebaseFirestore.getInstance()
                    .collection(COLLECTION_KEY)
                    .document(DOCUMENT_KEY)
                    .collection(RECORD_KEY)
                    .document(getWeekID()[i]);

            user_record.set(new Day(5000, stepList[i], walkList[i], getWeekID()[i], getWeekDate()[i] ))
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
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public String[] getWeekID()
    {
        String[] weekID = new String[28];
        int count = 0;
        for(int i = -27 ; i < 1; i++)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, i);
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf(cal.get(Calendar.MONTH));
            if(month.length() == 1)
            {
                month = "0" + month;
            }
            String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            if(day.length() == 1)
            {
                day = "0" + day;
            }
            String dayID = year + month + day;
            weekID[count] = dayID;
            count++;
        }
        return weekID;
    }

    public Date[] getWeekDate()
    {
        Date[] weekDate = new Date[28];
        int count = 0;
        for(int i = -27 ; i < 1; i++)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, i);
            Date date = cal.getTime();
            weekDate[count] = date;
            count++;
        }
        return weekDate;
    }

}


