package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Locale;

// reference: https://www.studytutorial.in/android-combined-line-and-bar-chart-using-mpandroid-library-android-tutorial

public class StepCountActivity extends AppCompatActivity {

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private UpdateStep updateStep;

    private TextView textSteps;
    private FitnessService fitnessService;
    private long currentSteps = 0;
    private long goalSteps = 0;
    private boolean goalCompleted;
    private List<Integer> stepCounts = new ArrayList<>();
    private List<Walk> walkList = new ArrayList<>();

    private String user_height;
    TimeStamper timeStamper;

    private class UpdateStep extends AsyncTask<Integer, Integer, Integer> {
        private int resp;

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                resp = params[0];
                while(true) {
                    fitnessService.updateStepCount();
                    stepCounts.clear();
                    walkList.clear();
                    fitnessService.getStepsCount(timeStamper.weekStart(), timeStamper.weekEnd(), stepCounts);
                    fitnessService.getWalks(timeStamper.weekStart(), timeStamper.weekEnd(), walkList);
                    Thread.sleep(1000);
                    Log.i(TAG, stepCounts.toString());
                    for(Walk walk : walkList) {
                        Log.i(TAG, "Walk starting at " + walk.getDurationInMillis() + " with steps: " + walk.getSteps());
                    }
                    Thread.sleep(9000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            return resp;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        CombinedChart mChart = (CombinedChart) findViewById(R.id.chart1);
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
        entries = getBarEnteries(entries);

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

        // Check if the user started a walk and has not stopped it
        SharedPreferences walkInfo = getSharedPreferences("walk", MODE_PRIVATE );


        SharedPreferences user = getSharedPreferences("user",MODE_PRIVATE);
        user_height = user.getString("height", "");

        if(user_height == "")
        {
            launchGetHeightActivity();
        }

        long startTimeStamp = walkInfo.getLong("startTimeStamp", -1);
        if(startTimeStamp != -1 && !timeStamper.isToday(startTimeStamp)) {
            fitnessService.walk(startTimeStamp, timeStamper.endOfDay(startTimeStamp)); // terminate walk at end of day
            SharedPreferences.Editor e = walkInfo.edit();
            e.putLong("startTimeStamp", -1);
            e.apply();
        }

        Button btnWalk = findViewById(R.id.buttonUpdateSteps);
        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        fitnessService.setup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(updateStep != null && !updateStep.isCancelled()) updateStep.cancel(true);
        updateStep = new UpdateStep();
        updateStep.execute(-1);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE );
        goalSteps = sharedPreferences.getInt("goal", 5000);
        setStepCount(currentSteps);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(updateStep != null) updateStep.cancel(true);
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
        textSteps.setText(String.format(Locale.getDefault(),"%d/%d steps today!", currentSteps, goalSteps));
        if( currentSteps >= stepCount && !goalCompleted ) {
            Toast completeGoalToast = Toast.makeText(getApplicationContext(),
                    String.format(Locale.getDefault(),"Congratulations, you've completed " +
                            "your goal of %d steps today!", goalSteps),
                    Toast.LENGTH_SHORT);

            completeGoalToast.show();
            goalCompleted = true;
        } else
            goalCompleted = false;
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

    public void launchGetHeightActivity()
    {
        Intent intent = new Intent(this, GetHeightActivity.class);
        startActivity(intent);
    }

    private ArrayList<Entry> getLineEntriesData(ArrayList<Entry> entries){
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 20));
        entries.add(new Entry(3, 18));
        entries.add(new Entry(4, 20));
        entries.add(new Entry(5, 15));
        entries.add(new Entry(6, 20));

        return entries;
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        entries = getLineEntriesData(entries);

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

    private ArrayList<BarEntry> getBarEnteries(ArrayList<BarEntry> entries){
        entries.add(new BarEntry(0f, new float[] {1, 2}));
        entries.add(new BarEntry(1f, new float[] {3, 4}));
        entries.add(new BarEntry(2f, new float[] {1, 4}));
        entries.add(new BarEntry(3f, new float[] {5, 2}));
        entries.add(new BarEntry(4f, new float[] {6, 2}));
        entries.add(new BarEntry(5f, new float[] {1, 3}));
        entries.add(new BarEntry(6f, new float[] {1, 4}));
        return  entries;
    }
}


