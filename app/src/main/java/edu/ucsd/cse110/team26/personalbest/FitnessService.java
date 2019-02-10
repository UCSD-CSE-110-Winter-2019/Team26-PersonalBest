package edu.ucsd.cse110.team26.personalbest;

import java.util.List;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount();
    void walk(long startTimeStamp, long endTimeStamp);
    void getWalks(long startTimeStamp, long endTimeStamp, List<Walk> walkList);
}
