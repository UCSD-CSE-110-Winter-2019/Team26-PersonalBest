package edu.ucsd.cse110.team26.personalbest;

import java.util.HashMap;
import java.util.Map;

class FitnessServiceFactory {
    private static final String TAG = "[FitnessServiceFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    static FitnessService create(String key, StepCountActivity stepCountActivity) {
        //Log.i(TAG, String.format("creating FitnessService with key %s", key));
        return blueprints.get(key).create(stepCountActivity);
    }

    public interface BluePrint {
        FitnessService create(StepCountActivity stepCountActivity);
    }
}
