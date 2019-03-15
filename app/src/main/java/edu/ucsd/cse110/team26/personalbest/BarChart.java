package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
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

public class BarChart {
    private CombinedChart mChart;
    private Context context;
    private int size;
    private String[] labels;

    List<Day> info;

    public BarChart(Context context, CombinedChart mChart)
    {
        this.context = context;
        this.mChart = mChart;
    }

    public BarChart(Context context, CombinedChart mChart, List<Day> info)
    {
        this.context = context;
        this.mChart = mChart;
        this.info = info;
        this.size = info.size();
    }

    public void draw(List<Day> info)
    {
        this.info = info;
        this.size = info.size();
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

        //getData();
        ArrayList<BarEntry> entries = new ArrayList<>();
        getBarEntries(entries);

        BarDataSet dataSet = new BarDataSet(entries, "Step Count");
        dataSet.setStackLabels(new String[] {"Intentional Walks", "Unintentional Walks"});

        setupLabel();

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
    private void getLineEntriesData(ArrayList<Entry> entries) {
        for(int i = 0; i < size; i++)
        {
            entries.add(new Entry(i, info.get(i).getGoal()));
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

    private void getBarEntries(ArrayList<BarEntry> entries)
    {
        for(int i = 0; i < size; i++)
        {
            entries.add(new BarEntry(i, new float[] {info.get(i).getWalkSteps(),
                    info.get(i).getTotalSteps() - info.get(i).getWalkSteps() }));
        }
    }

    public int getSize()
    {
        return this.size;
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
