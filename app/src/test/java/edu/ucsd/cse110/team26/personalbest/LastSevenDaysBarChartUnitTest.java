package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.Intent;

import com.github.mikephil.charting.charts.CombinedChart;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class LastSevenDaysBarChartUnitTest
{
    private Context context;
    private CombinedChart mChart;
    BarChart createBarChart;
    private StepCountActivity activity;
    @Before
    public void setUp()
    {
        context = InstrumentationRegistry.getInstrumentation().getContext();

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        //activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();

        List<Integer> stepCounts =  new ArrayList<>();
        List<ArrayList<Walk>> walkData = new ArrayList<>();
        stepCounts.add(0);

        ArrayList<Walk> fakeWalk = new ArrayList<>();
        Walk walk = new Walk(0,0);
        fakeWalk.add(walk);
        walkData.add(fakeWalk);

        createBarChart = new BarChart(context, mChart);
        createBarChart.setupLabel();
    }

    @Test
    public void testLastDayLabel()
    {
        String lastDay = createBarChart.getLastDayOfLabel();
        Calendar date = Calendar.getInstance();
        int todayDate = date.get(Calendar.DAY_OF_WEEK);
        String[] weekDay = new String[] {"Sun", "Mon","Tue", "Wed", "Thur", "Fri", "Sat"};
        assertEquals(lastDay, weekDay[todayDate-1]);
    }

    @Test
    public void testFirstDayLabel()
    {
        String firstDay = createBarChart.getFirstDayOfLabel();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR,-6);
        int sevenDaysAgo = date.get(Calendar.DAY_OF_WEEK);
        String[] weekDay = new String[] {"Sun", "Mon","Tue", "Wed", "Thur", "Fri", "Sat"};
        assertEquals(firstDay, weekDay[sevenDaysAgo-1]);
    }




}
