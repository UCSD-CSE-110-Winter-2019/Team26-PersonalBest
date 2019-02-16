package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.lang.*;

// reference: https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial

public class StepCountActivity extends AppCompatActivity {
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private UpdateStep updateStep;

    private TextView textSteps;
    private TextView textWalkData;
    private Button btnStartWalk;
    private Button btnEndWalk;

    private FitnessService fitnessService;
    private long currentSteps = 0;
    private long goalSteps = 0;
    private int user_height;
    private boolean goalCompleted;
    private List<Integer> stepCounts = new ArrayList<>();
    private List<Walk> walkList = new ArrayList<>();

    private boolean hasSuggestHappend = false;

    private long startTimeStamp = -1;
    private long initialSteps = 0;
    private Walk currentWalk;

    TimeStamper timeStamper;

    private class UpdateStep extends AsyncTask<Integer, Integer, Integer> {
        private boolean run = true;
        private int resp;

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                resp = params[0];
                while(run) {
                    fitnessService.updateStepCount();
                    Thread.sleep(500);
                    stepCounts.clear();
                    walkList.clear();
                    fitnessService.getStepsCount(timeStamper.weekStart(), timeStamper.weekEnd(), stepCounts);
                    fitnessService.getWalks(timeStamper.weekStart(), timeStamper.weekEnd(), walkList);
                    Thread.sleep(500);
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

        CombinedChart mChart = findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setText("");
        mChart.setHighlightFullBarEnabled(false);
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });

        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setAxisMinimum(0.0f); // this replaces setStartAtZero(true)
        mChart.getAxisRight().setAxisMinimum(0.0f);

        ArrayList<BarEntry> entries = new ArrayList<>();
        getBarEntries(entries);

        BarDataSet dataSet = new BarDataSet(entries, "Step Count");
        dataSet.setStackLabels(new String[] {"Intentional Walks", "Unintentional Walks"});

        final String[] labels = new String[] {
                "Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"
        };

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset
        BarData d = new BarData(dataSet);
        d.setBarWidth(barWidth);
        BarData data = new BarData(dataSet);

        mChart.getXAxis().setLabelCount(dataSet.getEntryCount());

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels[(int) value % labels.length];
            }
        });

        dataSet.setColors(new int[] {ContextCompat.getColor(mChart.getContext(), R.color.colorAccent),
                ContextCompat.getColor(mChart.getContext(), R.color.colorPrimary),});

        CombinedData dataCombined = new CombinedData();
        dataCombined.setData( generateLineData());
        dataCombined.setData(data);

        mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
        mChart.getXAxis().setAxisMinimum(data.getXMin() - 0.25f);
        mChart.setData(dataCombined);


        textSteps = findViewById(R.id.textSteps);

        final String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        timeStamper = new TimeStampNow();

        fitnessService.setup();




        btnStartWalk = findViewById(R.id.btnStartWalk);
        btnEndWalk = findViewById(R.id.btnEndWalk);
        textWalkData = findViewById(R.id.textWalkData);
        btnStartWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startTimeStamp == -1) {
                    startTimeStamp = timeStamper.now();
                    initialSteps = currentSteps;
                    SharedPreferences.Editor editor = getSharedPreferences("walk", MODE_PRIVATE).edit();
                    editor.putLong("startTimeStamp", startTimeStamp)
                            .putLong("initialSteps", initialSteps).apply();
                    btnStartWalk.setVisibility(View.GONE);
                    btnEndWalk.setVisibility(View.VISIBLE);
                    currentWalk = new Walk(0, startTimeStamp);
                    updateWalkData();
                }
            }
        });
        btnEndWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(updateStep != null && !updateStep.isCancelled()) updateStep.cancel(true);
        updateStep = new UpdateStep();
        updateStep.execute(-1);

        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
        goalSteps = user.getInt("goal", 5000);
        user_height = user.getInt("height", 0);
        if(user_height == 0) {
            launchGetHeightActivity();
        }

        //Checking to see if we need to suggest a new step goal.
        if(suggestGoal()){
            //check if dialog box has been shown and if it's a new week:
            if(hasSuggestHappend == false && timeStamper.isToday(timeStamper.weekStart())){
                int suggestedGoal = (int)goalSteps+500;
                createAlertDialog(suggestedGoal);
                goalSteps = user.getInt("goal", 5000);
                hasSuggestHappend = true;
            }
            else if(hasSuggestHappend == true && !timeStamper.isToday(timeStamper.weekStart())){
                hasSuggestHappend = false;
            }
            else{
                //do nothing; keep suggestHappened as true since it's still sunday.
            }
        }


        setStepCount(currentSteps);

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
        if (resultCode == Activity.RESULT_OK) {
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
        currentSteps = stepCount;

        textSteps.setText(String.format(Locale.getDefault(),"%d/%d steps today", currentSteps, goalSteps));
        updateWalkData();
        if(currentSteps >= goalSteps && !goalCompleted ) {
            Toast completeGoalToast = Toast.makeText(getApplicationContext(),
                    String.format(Locale.getDefault(),"Congratulations, you've completed " +
                            "your goal of %d steps today!", goalSteps),
                    Toast.LENGTH_SHORT);

            completeGoalToast.show();
            goalCompleted = true;
        } else {
            goalCompleted = false;
        }
    }

    private void updateWalkData() {
        if(startTimeStamp != -1) {
            initialSteps = getSharedPreferences("walk", MODE_PRIVATE).getLong("initialSteps", 0);
            currentWalk.setSteps(currentSteps - initialSteps);
            textWalkData.setText(String.format(Locale.getDefault(),
                    "Current walk:\nWalk duration: %s\n%d steps taken\nDistance walked: %.1f feet\nAverage speed: %.1fmph",
                    timeStamper.durationToString(timeStamper.now() - startTimeStamp),
                    currentWalk.getSteps(),
                    currentWalk.stepsToFeet(user_height),
                    currentWalk.averageMph(user_height)));
        } else if(!walkList.isEmpty()) {
            Walk lastWalk = walkList.get(walkList.size() - 1);
            textWalkData.setText(String.format(Locale.getDefault(),
                    "Last walk:\nWalk duration: %s\n%d steps taken\nDistance walked: %.1f feet\nAverage speed: %.1fmph",
                    timeStamper.durationToString(lastWalk.getDurationInMillis()),
                    lastWalk.getSteps(),
                    lastWalk.stepsToFeet(user_height),
                    lastWalk.averageMph(user_height)));
        }
        Log.i(TAG, "updateWalkData" + startTimeStamp + " " + walkList.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(StepCountActivity.this, SettingsActivity.class);
            StepCountActivity.this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean suggestGoal(){
        List<Integer> prevWeek = new ArrayList<>();
        int weekDif = 7*24*60*60*1000;
        try{
            fitnessService.getStepsCount(timeStamper.weekStart()- weekDif, timeStamper.weekEnd()-weekDif, prevWeek);
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }


        for(int stepsThatDay: prevWeek){
            if(stepsThatDay >= goalSteps){
                return true;
            }

        }
        return false;
    }

    public void createAlertDialog(final int suggestedGoal) {
        AlertDialog alertDialog = new AlertDialog.Builder(StepCountActivity.this).create();
        alertDialog.setTitle("Suggesting Goals");



        alertDialog.setMessage("Would you like to set next weeks steps to be " + suggestedGoal);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = user.edit();
                        editor.putInt("goal", suggestedGoal);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void launchGetHeightActivity() {
        Intent intent = new Intent(this, GetHeightActivity.class);
        startActivity(intent);
    }

    private void getLineEntriesData(ArrayList<Entry> entries) {
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 20));
        entries.add(new Entry(3, 18));
        entries.add(new Entry(4, 20));
        entries.add(new Entry(5, 15));
        entries.add(new Entry(6, 20));
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();

        getLineEntriesData(entries);

        LineDataSet set = new LineDataSet(entries, "Goal");
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(60, 79, 109));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(60, 79, 109));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(60, 79, 109));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private void getBarEntries(ArrayList<BarEntry> entries){
        entries.add(new BarEntry(0f, new float[] {1, 2}));
        entries.add(new BarEntry(1f, new float[] {3, 4}));
        entries.add(new BarEntry(2f, new float[] {1, 4}));
        entries.add(new BarEntry(3f, new float[] {5, 2}));
        entries.add(new BarEntry(4f, new float[] {6, 2}));
        entries.add(new BarEntry(5f, new float[] {1, 3}));
        entries.add(new BarEntry(6f, new float[] {1, 4}));
    }
}


