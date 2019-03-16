package edu.ucsd.cse110.team26.personalbest;

import com.github.mikephil.charting.charts.CombinedChart;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class LastSevenDaysBarChartUnitTest
{
    private CombinedChart mChart;
    private BarChart createBarChart;

    @Before
    public void setUp()
    {
        createBarChart = new BarChart(mChart);
        createBarChart.setupLabel();
    }

    @Test
    public void testLastDayLabel()
    {
        String lastDay = createBarChart.getLastDayOfLabel();
        Calendar date = Calendar.getInstance();
        int todayDate = date.get(Calendar.DAY_OF_WEEK);
        String[] weekDay = new String[] {"Su", "M","T", "W", "Th", "F", "Sa"};
        assertEquals(lastDay, weekDay[todayDate-1]);
    }

    @Test
    public void testFirstDayLabel()
    {
        String firstDay = createBarChart.getFirstDayOfLabel();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR,-6);
        int sevenDaysAgo = date.get(Calendar.DAY_OF_WEEK);
        String[] weekDay = new String[] {"Su", "M","T", "W", "Th", "F", "Sa"};
        assertEquals(firstDay, weekDay[sevenDaysAgo-1]);
    }




}
