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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
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

public class BarChart {
    private CombinedChart mChart;
    private List<Integer> stepCounts;
    private List<ArrayList<Walk>> walkData;
    private Context context;
    private int size;
    private String userID;
    private String[] labels;

    DocumentReference user_data;
    CollectionReference user_list;
    GoogleSignInAccount currentUser;
    String COLLECTION_KEY = "users";
    String RECORD_KEY = "record";
    String DOCUMENT_KEY;
    Day day;

    private int[] weekGoal;
    private int[] weekStep;
    private int[] weekWalk;

    public BarChart(Context context, CombinedChart mChart)
    {
        this.context = context;
        this.mChart = mChart;
    }

    public void setSize(int size)
    {
        this.size = size;
        weekGoal = new int[size];
        weekStep = new int[size];
        weekWalk = new int[size];
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

        getData();
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
            entries.add(new Entry(i, weekGoal[i]));
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
            entries.add(new BarEntry(i, new float[] {weekWalk[i], weekStep[i] - weekWalk[i] }));
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

    public void getData()
    {
        String [] listDayID;
        if(size == 7)
        {
            listDayID = get7DaysID();
        }
        else
        {
            listDayID = get28DaysID();
        }

        for( int i = 0; i < size; i++)
        {
            final int count = i;
            DocumentReference user_record = FirebaseFirestore.getInstance()
                    .collection(COLLECTION_KEY)
                    .document(GoogleSignIn.getLastSignedInAccount(context).getEmail())
                    .collection(RECORD_KEY)
                    .document(listDayID[count]);

            user_record.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    day = documentSnapshot.toObject(Day.class);
                    weekStep[count] = (int)day.getTotalSteps();
                    weekGoal[count] = (int)day.getGoal();
                    weekWalk[count] = (int)day.getWalkSteps();
                }
            });
        }
    }

    public String[] get7DaysID()
    {
        String[] weekID = new String[7];
        int count = 0;
        for(int i = -6 ; i < 1; i++)
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

    public String[] get28DaysID()
    {
        String[] last28ID = new String[28];
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
            last28ID[count] = dayID;
            count++;
        }
        return last28ID;
    }

    public void setDOCUMENT_KEY(String DOCUMENT_KEY)
    {
        this.DOCUMENT_KEY = DOCUMENT_KEY;
    }
}
