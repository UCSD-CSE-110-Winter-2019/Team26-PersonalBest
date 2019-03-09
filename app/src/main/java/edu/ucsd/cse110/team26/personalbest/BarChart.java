package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

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
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BarChart {
    private CombinedChart mChart;
    private List<Integer> stepCounts;
    private List<ArrayList<Walk>> walkData;
    private Context context;
    private int size;
    private String[] labels;
    private List<Integer> goalData;

    public BarChart(Context context, CombinedChart mChart, List<Integer> stepCounts, List<ArrayList<Walk>> walkData
            ,List<Integer> goalData, boolean monthlySummary)
    {
        this.context = context;
        this.mChart = mChart;
        this.stepCounts = stepCounts;
        this.walkData = walkData;
        this.goalData = goalData;
        if(monthlySummary) this.size = 28;
        else this.size = 7;
    }


    public void draw()
    {
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

        updateGoal();

        ArrayList<BarEntry> entries = new ArrayList<>();
        getBarEntries(entries);

        BarDataSet dataSet = new BarDataSet(entries, "Step Count");
        dataSet.setStackLabels(new String[] {"Intentional Walks", "Unintentional Walks"});

        if(size == 7)
        {
            setupLabel();
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
        }

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset
        BarData d = new BarData(dataSet);
        d.setBarWidth(barWidth);
        BarData data = new BarData(dataSet);


        dataSet.setColors(new int[] {ContextCompat.getColor(mChart.getContext(), R.color.colorAccent),
                ContextCompat.getColor(mChart.getContext(), R.color.colorPrimary),});

        CombinedData dataCombined = new CombinedData();
        dataCombined.setData( generateLineData());
        dataCombined.setData(data);

        mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
        mChart.getXAxis().setAxisMinimum(data.getXMin() - 0.25f);
        mChart.setData(dataCombined);

    }

    public void setupLabel()
    {
        Calendar date = Calendar.getInstance();
        int today = date.get(Calendar.DAY_OF_WEEK);
        switch (today)
        {
            case Calendar.MONDAY:
                labels = new String[] {"Tue", "Wed", "Thur", "Fri", "Sat","Sun", "Mon"};
                break;
            case Calendar.TUESDAY:
                labels = new String[] {"Wed", "Thur", "Fri", "Sat","Sun", "Mon","Tue"};
                break;
            case Calendar.WEDNESDAY:
                labels = new String[] {"Thur", "Fri", "Sat","Sun", "Mon","Tue", "Wed"};
                break;
            case Calendar.THURSDAY:
                labels = new String[] {"Fri", "Sat","Sun", "Mon","Tue", "Wed", "Thur"};
                break;
            case Calendar.FRIDAY:
                labels = new String[] {"Sat","Sun", "Mon", "Tue", "Wed", "Thur", "Fri"};
                break;
            case Calendar.SATURDAY:
                labels = new String[] {"Sun", "Mon","Tue", "Wed", "Thur", "Fri", "Sat"};
                break;
            case Calendar.SUNDAY:
                labels = new String[] {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat","Sun"};
                break;
        }
    }

    /*private void getLineEntriesData(ArrayList<Entry> entries) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        int goal_sun = sharedPreferences.getInt("goal_Sun", 5000);
        int goal_mon = sharedPreferences.getInt("goal_Mon", 5000);
        int goal_tue = sharedPreferences.getInt("goal_Tue", 5000);
        int goal_wed = sharedPreferences.getInt("goal_Wed", 5000);
        int goal_thu = sharedPreferences.getInt("goal_Thu", 5000);
        int goal_fri = sharedPreferences.getInt("goal_Fri", 5000);
        int goal_sat = sharedPreferences.getInt("goal_Sat", 5000);


        entries.add(new Entry(0, goal_sun));
        entries.add(new Entry(1, goal_mon));
        entries.add(new Entry(2, goal_tue));
        entries.add(new Entry(3, goal_wed));
        entries.add(new Entry(4, goal_thu));
        entries.add(new Entry(5, goal_fri));
        entries.add(new Entry(6, goal_sat));
    }*/

    private void getLineEntriesData(ArrayList<Entry> entries) {
        for (int i=0; i< this.size; i++) {
            entries.add(new Entry(i, goalData.get(i)));
        }
    }

    /*private void updateGoal()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.getBoolean("new_week",false) )
        {
            int previous_goal = sharedPreferences.getInt("goal_Sat", 5000);
            editor.putInt("goal_Sun", previous_goal);
            editor.putInt("goal_Mon", previous_goal);
            editor.putInt("goal_Tue", previous_goal);
            editor.putInt("goal_Wed", previous_goal);
            editor.putInt("goal_Thu", previous_goal);
            editor.putInt("goal_Fri", previous_goal);
            editor.putInt("goal_Sat", previous_goal);
            editor.putBoolean("new_week",false);
        }
        editor.apply();
    }*/

    private void updateGoal() {

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
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        long[] totalStep = new long[7];
        int count = 0;
        for(Integer i: stepCounts)
        {
            totalStep[count] = totalStep[count] + i;

            count++;
        }
        long[] totalIntent = new long[7];
        count = 0;
        for(ArrayList<Walk> i : walkData)
        {
            totalIntent[count] = 0;
            for(Walk j: i)
            {
                totalIntent[count] = totalIntent[count] + j.getSteps();
            }

            count++;
        }

        entries.add(new BarEntry(0f, new float[] {totalIntent[0],totalStep[0]- totalIntent[0]}));
        entries.add(new BarEntry(1f, new float[] {totalIntent[1],totalStep[1]- totalIntent[1]}));
        entries.add(new BarEntry(2f, new float[] {totalIntent[2],totalStep[2]- totalIntent[2]}));
        entries.add(new BarEntry(3f, new float[] {totalIntent[3],totalStep[3]- totalIntent[3]}));
        entries.add(new BarEntry(4f, new float[] {totalIntent[4],totalStep[4]- totalIntent[4]}));
        entries.add(new BarEntry(5f, new float[] {totalIntent[5],totalStep[5]- totalIntent[5]}));
        entries.add(new BarEntry(6f, new float[] {totalIntent[6],totalStep[6]- totalIntent[6]}));

        editor.apply();
    }

    public String getLastDayOfLabel()
    {
        return labels[6];
    }
    public String getFirstDayOfLabel()
    {
        return labels[0];
    }
}
