package edu.ucsd.cse110.team26.personalbest;

class FitnessServiceFactory {
    private static final String TAG = "[FitnessServiceFactory]";

    static FitnessService create(boolean debug, StepCountActivity stepCountActivity) {
        if (debug) {
            return new MockFitnessAdapter(stepCountActivity);
        } else {
            return new GoogleFitAdapter(stepCountActivity);
        }
    }

}
