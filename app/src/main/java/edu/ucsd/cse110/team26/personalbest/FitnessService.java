package edu.ucsd.cse110.team26.personalbest;

import java.util.List;

public interface FitnessService {
    void setup();
    void updateStepCount(Callback<Long> stepCountCallback);
    void walk(long startTimeStamp, long endTimeStamp);
    void getWalks(long startTimeStamp, long endTimeStamp, List<Walk> walkList);
    void getStepsCount(long startTimeStamp, long endTimeStamp, List<Integer> stepsList);
}
