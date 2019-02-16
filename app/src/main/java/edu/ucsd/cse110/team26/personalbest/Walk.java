package edu.ucsd.cse110.team26.personalbest;

class Walk {

    private long steps;
    private long startTimeStamp;
    private long endTimeStamp = 0;
    private TimeStamper timeStamper = new TimeStampNow();

    Walk(long steps, long startTimeStamp, long endTimeStamp) {
        this.steps = steps;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
    }

    Walk(long steps, long startTimeStamp) {
        this.steps = steps;
        this.startTimeStamp = startTimeStamp;
    }

    long getSteps() {
        return steps;
    }

    void setSteps(long steps) {
        this.steps = steps;
    }

    long getDurationInMillis() {
        if(endTimeStamp != 0) {
            return endTimeStamp - startTimeStamp;
        } else {
            return timeStamper.now() - startTimeStamp;
        }
    }

    double stepsToFeet(long height) {
        double feetPerStep = height * 0.03441666666;
        return steps * feetPerStep;
    }

    double averageMph(long height) {
        long duration = getDurationInMillis();
        if(duration == 0) return 0;
        double hours = (double) duration / (60*60*1000);
        return stepsToFeet(height) / 5280 / hours;
    }
}
