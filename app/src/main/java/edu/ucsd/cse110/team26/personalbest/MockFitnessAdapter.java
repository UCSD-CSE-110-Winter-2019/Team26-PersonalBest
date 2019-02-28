package edu.ucsd.cse110.team26.personalbest;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class MockFitnessAdapter implements FitnessService {

    private final static String TAG = "[MockFitnessAdapter]";

    private StepCountActivity activity;

    private long steps = 0;
    private List<Walk> walks = new ArrayList<>();
    private TimeStamper timeStamper = new ConcreteTimeStamper();

    MockFitnessAdapter(StepCountActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getRequestCode() {
        return 0;
    }

    @Override
    public void setup() {

    }

    @Override
    public void updateStepCount() {
        steps += 1;
        activity.setStepCount(steps);
        Log.d(TAG, "update steps: " + steps);
    }

    @Override
    public void walk(long startTimeStamp, long endTimeStamp) {
        walks.add(new Walk(steps, startTimeStamp, endTimeStamp));
    }

    @Override
    public void getWalks(long startTimeStamp, long endTimeStamp, List<Walk> walkList) {
        for(Walk w : walks) {
            if(w.inTimeRange(startTimeStamp, endTimeStamp)) walkList.add(w);
        }
    }

    @Override
    public void getStepsCount(long startTimeStamp, long endTimeStamp, List<Integer> stepsList) {
        int days = 0;
        for(long i = startTimeStamp; i < endTimeStamp; i = timeStamper.nextDay(i)) {
            days ++;
            stepsList.add(500 * days);
        }
    }
}
