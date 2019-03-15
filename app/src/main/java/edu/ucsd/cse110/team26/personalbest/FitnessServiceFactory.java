package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;

class FitnessServiceFactory {
    private static final String TAG = "[FitnessServiceFactory]";

    static FitnessService create(boolean debug, Activity activity) {
        if (debug) {
            return new MockFitnessAdapter(activity);
        } else {
            return new GoogleFitAdapter(activity);
        }
    }

}
