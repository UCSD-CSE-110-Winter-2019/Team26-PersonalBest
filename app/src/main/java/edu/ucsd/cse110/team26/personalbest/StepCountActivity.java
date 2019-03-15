package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

// reference: https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial

public class StepCountActivity extends AppCompatActivity {

    private static final String TAG = "StepCountActivity";
    private static boolean DEBUG;
    private static boolean ESPRESSO;

    static boolean toggleEncouragementMessage = true;

    private UpdateStep updateStep;

    private Settings settings;

    private TextView textSteps;
    private TextView textWalkData;
    private Button btnStartWalk;
    private Button btnEndWalk;

    FitnessService fitnessService;
    private long previousDaySteps = 0;
    private long lastEncouragingMessageSteps = 0;
    private boolean goalCompleted;
    private List<Integer> stepCounts = new ArrayList<>();
    private List<Integer> walkStepCounts = new ArrayList<>();
    private List<ArrayList<Walk>> walkData = new ArrayList<>();
    List<Walk> walksToday;

    private List<Day> week;
    private List<Day> month;
    private List<Day> monthUpdate = new ArrayList<Day>();
    private long startTimeStamp = -1;
    private long initialSteps = 0;
    private long currentDate;

    private Walk currentWalk;
    private Day today = new Day();
    private User user = new User();

    IDataAdapter dataAdapter;
    TimeStamper timeStamper;

    // BarChart object
    private BarChart createMonthChart;
    private BarChart createWeekChart;
    private GoalNotifications notifier;

    private CombinedChart monthChart;
    private CombinedChart weekChart;


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
            updateDatabase();
            try {
                resp = params[0];
                while(run) {
                    if( !timeStamper.isToday(currentDate) ) {
                        initializeNewDay();
                        currentDate = timeStamper.now();
                    }
                    fitnessService.updateStepCount(StepCountActivity.this::setStepCount);

                    dataAdapter.getDays(28, (list) -> {
                        Log.d(TAG, list.toString());
                        month = new ArrayList<Day>();
                        week = new ArrayList<Day>();
                        month.addAll(list);
                        long ts;
                        if( list.size() != 0 )
                            ts = timeStamper.previousDay(timeStamper.startOfDay(month.get(month.size()-1).timeStamp));
                        else
                            ts = timeStamper.previousDay(timeStamper.startOfDay(timeStamper.now()));
                        for(int i = month.size(); i < 28; i++ ) {
                            month.add(new Day(5000, 0, 0, ts));
                            ts = timeStamper.previousDay(ts);
                        }
                        for(int i = 0; i < 7; i++ ) {
                            week.add(month.get(i));
                        }
                        createMonthChart.draw(month);
                        createWeekChart.draw(week);
                    });

                    Thread.sleep(10000);
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

        textSteps = findViewById(R.id.textSteps);
        btnStartWalk = findViewById(R.id.btnStartWalk);
        btnEndWalk = findViewById(R.id.btnEndWalk);
        textWalkData = findViewById(R.id.textWalkData);
        weekChart = findViewById(R.id.weekChart);
        monthChart = findViewById(R.id.monthChart);
        createWeekChart = new BarChart(weekChart);
        createMonthChart = new BarChart(monthChart);

        fitnessService = FitnessServiceFactory.create(DEBUG, this);
        dataAdapter = IDatabaseAdapterFactory.create(DEBUG, this.getApplicationContext());
        timeStamper = new ConcreteTimeStamper();
        settings = new Settings(getApplicationContext(), DEBUG);

        fitnessService.setup();


        // 28-day bar chart
        monthChart.setVisibility(View.GONE);

        // 7-day bar chart
        weekChart.setVisibility(View.VISIBLE);

        Switch sw = findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                weekChart.setVisibility(View.GONE);
                monthChart.setVisibility(View.VISIBLE);
            } else {
                weekChart.setVisibility(View.VISIBLE);
                monthChart.setVisibility(View.GONE);
            }
        });

        currentDate = timeStamper.now();
        notifier=new GoalNotifications(this);

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

        dataAdapter.getFriends( (friendsList) -> {
            toggleEncouragementMessage = !friendsList.isEmpty();
        });

        if(updateStep != null && !updateStep.isCancelled()) {
            updateStep.cancel(true);
        }

        // If espresso test is not running, start async task
        if(!ESPRESSO) {
            updateStep = new UpdateStep();
            updateStep.execute(-1);
        }

        Settings settings = new Settings(getApplicationContext(), timeStamper);
        today.goal = settings.getGoal();
        user.height = settings.getHeight();
        if(user.height == 0) {
            launchGetHeightActivity();
        }

        //create notification channel
        if(notifier == null){
            notifier = new GoalNotifications(this);
        }
        notifier.createNotificationChannel();

        setStepCount(today.totalSteps);

        // Check if the user started a walk and has not stopped it
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
        textSteps.setText(String.format(Locale.getDefault(),"%d/%d steps today", today.totalSteps, today.goal));
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

            //notification for when they cmolpete the goal;
            notifier.showNotification();

            createAlertDialog(suggestedGoalNum);

            Toast completeGoalToast = Toast.makeText(getApplicationContext(),
                    String.format(Locale.getDefault(),"Congratulations, you've completed your goal of %d steps today!", today.goal),
                    Toast.LENGTH_SHORT);

            completeGoalToast.show();
            goalCompleted = true;
        } else {
            goalCompleted = false;
            if( toggleEncouragementMessage ) {
                if (previousDaySteps != 0 && today.totalSteps < today.goal) {
                    int improvementPercentage = (int) ((today.totalSteps - previousDaySteps) / previousDaySteps) * 100;
                    if ((lastEncouragingMessageSteps == 0 && previousDaySteps + 500 <= today.totalSteps)
                            || (lastEncouragingMessageSteps + 500 <= today.totalSteps && lastEncouragingMessageSteps != 0)) {
                        lastEncouragingMessageSteps = today.totalSteps - (today.totalSteps - previousDaySteps) % 500;
                        EncouragementMessage.makeEncouragementMessage(getApplicationContext(), improvementPercentage);
                    }
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
                    dialog.dismiss();
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", (dialog, which) ->{
            dialog.dismiss();
        });
        alertDialog.show();
    }

    public void launchGetHeightActivity() {
        Intent intent = new Intent(this, GetHeightActivity.class);
        intent.putExtra("DEBUG", DEBUG);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (previousSteps.size() == 0)
            previousSteps.add(0);
        previousDaySteps = previousSteps.get(0);
        Log.i(TAG, String.format("New day. Setting previous day's steps to %d", previousDaySteps));
        Settings settings = new Settings(getApplicationContext(), timeStamper);
        settings.saveGoal((int) today.goal);
        lastEncouragingMessageSteps = 0;
    }

    public void updateDatabase() {
        stepCounts.clear();
        fitnessService.getStepsCount(timeStamper.lastTwentyEightDays(), timeStamper.today(), stepCounts);

        walkData.clear();
        walkStepCounts.clear();
        monthUpdate.clear();
        int walkStepCount;
        List<Integer> weekGoal = settings.getGoalsOfLastWeek();
        long ts = timeStamper.startOfDay(timeStamper.lastTwentyEightDays());
        for (int i = 0; i < 28; i++) {
            ArrayList<Walk> list = new ArrayList<>();
            walkData.add(list);
            fitnessService.getWalks(ts, timeStamper.endOfDay(ts), list);
            if (timeStamper.isToday(ts))
                walksToday = list;
            ts = timeStamper.nextDay(ts);
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(List<Walk> walklist : walkData) {
            Log.i(TAG, walklist.toString());
        }

        ts = timeStamper.startOfDay(timeStamper.lastTwentyEightDays());
        for (int i = 0; i < 28; i++) {
            walkStepCount = 0;
            for (Walk w : walkData.get(i)) {
                walkStepCount += w.getSteps();
            }
            walkStepCounts.add(i, walkStepCount);
            if (i >= 21)
                monthUpdate.add(new Day((int) weekGoal.get(i - 21), (int) stepCounts.get(i), walkStepCounts.get(i), ts));
            else
                monthUpdate.add(new Day((int) weekGoal.get(6), (int) stepCounts.get(i), walkStepCounts.get(i), ts));
            ts = timeStamper.nextDay(ts);
        }
        dataAdapter.updateDays(monthUpdate, (success) -> {
            if (success)
                Log.i(TAG, "Successfully updated last" + monthUpdate.size() + "days of data in firestore");
        });
    }


}


