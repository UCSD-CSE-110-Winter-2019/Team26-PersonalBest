package edu.ucsd.cse110.team26.personalbest;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BarChart {
    private CombinedChart mChart;
    private int size = 7;
    private String[] labels;
    private List<Day> days;

    public BarChart(CombinedChart mChart)
    {
        this.mChart = mChart;
    }


    public void draw(List<Day> days)
    {
        this.days = days;
        if(days.size() == 28) this.size = 28;
        else this.size = 7;
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
        dataSet.setStackLabels(new String[] {"During Walks", "Other Activities"});
        setupLabel();
        mChart.getXAxis().setLabelCount(dataSet.getEntryCount());
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter((value, axis) -> labels[(int) value % labels.length]);


        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset
        BarData d = new BarData(dataSet);
        d.setBarWidth(barWidth);
        BarData data = new BarData(dataSet);


        dataSet.setColors(ContextCompat.getColor(mChart.getContext(), R.color.colorAccent),
                ContextCompat.getColor(mChart.getContext(), R.color.colorPrimary));

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
                labels = new String[] {"T", "W", "Th", "F", "Sa","Su", "M"};
                break;
            case Calendar.TUESDAY:
                labels = new String[] {"W", "T", "F", "Sa","Su", "M","T"};
                break;
            case Calendar.WEDNESDAY:
                labels = new String[] {"Th", "F", "Sa","Su", "M","T", "W"};
                break;
            case Calendar.THURSDAY:
                labels = new String[] {"F", "Sa","Su", "M","T", "W", "Th"};
                break;
            case Calendar.FRIDAY:
                labels = new String[] {"Sa","Su", "M", "T", "W", "Th", "F"};
                break;
            case Calendar.SATURDAY:
                labels = new String[] {"Su", "M","T", "W", "Th", "F", "Sa"};
                break;
            case Calendar.SUNDAY:
                labels = new String[] {"M", "T", "W", "Th", "F", "Sa","Su"};
                break;
        }
    }


    private void getLineEntriesData(ArrayList<Entry> entries) {
        for (int i=0; i< this.size; i++) {
            entries.add(new Entry(i, days.get(i).goal));
        }
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
        long[] totalStep = new long[this.size];
        for (int k=0; k<this.size; k++) {
            entries.add(new BarEntry(k, new float[] {days.get(k).walkSteps, days.get(k).totalSteps-days.get(k).walkSteps}));
        }

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