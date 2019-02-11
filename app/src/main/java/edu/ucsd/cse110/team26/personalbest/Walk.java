package edu.ucsd.cse110.team26.personalbest;

public class Walk {

    private long startTimeStamp;
    private long endTimeStamp;
    private long steps;

    public Walk(long startTimeStamp, long endTimeStamp, long steps) {
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.steps = steps;
    }

    public long getSteps() {
        return steps;
    }

    public long getDurationInMillis() {
        return endTimeStamp - startTimeStamp;
    }
}
