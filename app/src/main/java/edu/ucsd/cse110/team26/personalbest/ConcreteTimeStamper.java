package edu.ucsd.cse110.team26.personalbest;


import java.util.Calendar;
import java.util.TimeZone;

public class ConcreteTimeStamper implements TimeStamper {

    @Override
    public long now() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public int getDayOfWeek() { return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);}

    @Override
    public long weekStart() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public long weekEnd() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    @Override
    public long lastTwentyEightDays() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.DATE, -27);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public long lastSevenDays()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.DATE, -6);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public long today()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    @Override
    public long[] getPreviousDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return new long[]{startOfDay(cal.getTimeInMillis()), endOfDay(cal.getTimeInMillis())};
    }

    @Override
    public boolean isToday(long timeStamp) {
        Calendar today = Calendar.getInstance(TimeZone.getDefault());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(timeStamp);

        return today.before(cal);
    }

    @Override
    public long endOfDay(long timeStamp) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(timeStamp);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }


    @Override
    public long startOfDay(long timeStamp) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(timeStamp);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public long nextDay(long timeStamp) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(timeStamp);
        cal.add(Calendar.DATE, 1);
        return cal.getTimeInMillis();
    }

    @Override
    public String durationToString(long duration) {
        if(duration < 60000) {
            return "" + duration/1000 + "s";
        } else if(duration < 60*60*1000) {
            return "" + duration/60000 + "m " + (duration % 60000)/1000 + "s";
        } else {
            return "" + duration/3600000 + ":" + (duration % 3600000)/60000;
        }
    }

}
