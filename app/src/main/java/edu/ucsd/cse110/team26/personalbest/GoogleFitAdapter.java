package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFitAdapter implements FitnessService {
    private final String TAG = "GoogleFitAdapter";
    private final String SESSION_NAME = "PersonalBestWalk";

    private Context context;
    private GoogleSignInAccount lastSignedInAccount;

    GoogleFitAdapter(Context context) {
        this.context = context;
        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context);
    }

    public void setup() {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        if (GoogleSignIn.hasPermissions(lastSignedInAccount, fitnessOptions)) {
            startRecording();
        }

    }

    private void startRecording() {
        if (lastSignedInAccount != null) {
            Fitness.getRecordingClient(context, lastSignedInAccount)
                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnSuccessListener(aVoid -> Log.i(TAG, "Successfully subscribed!"))
                    .addOnFailureListener(e -> Log.i(TAG, "There was a problem subscribing."));
        }
    }


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public void updateStepCount(Callback<Long> stepCountCallback) {
        if (lastSignedInAccount != null) {
            Fitness.getHistoryClient(context, lastSignedInAccount)
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(dataSet -> {
                        long totalSteps = dataSet.isEmpty()
                                ? 0
                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                        Log.i(TAG, "Steps successfully read: " + totalSteps);

                        stepCountCallback.call(totalSteps);
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "There was a problem getting the step count.", e));
        } else {
            Log.e(TAG, "Error reading step count: no login found");
        }
    }

    @Override
    public void walk(long startTimeStamp, long endTimeStamp) {

        Session session = new Session.Builder()
                .setName(SESSION_NAME)
                .setActivity(FitnessActivities.WALKING)
                .setStartTime(startTimeStamp, TimeUnit.MILLISECONDS)
                .setEndTime(endTimeStamp, TimeUnit.MILLISECONDS)
                .build();

        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .build();

        if(lastSignedInAccount != null) {
            Log.i(TAG, "Inserting new walk in the Sessions API");
            Fitness.getSessionsClient(context, lastSignedInAccount)
                    .insertSession(insertRequest)
                    .addOnSuccessListener(aVoid -> Log.i(TAG, "Session insert was successful!"))
                    .addOnFailureListener(e -> Log.i(TAG, "There was a problem inserting the session: " +
                            e.getLocalizedMessage()));
        }
    }


    /**
     * Asynchronous call to the Google Fit History to retrieve walk info, and populates the
     * provided list with the Walk info upon retrieving info successfully.
     * @param startTimeStamp Starting time of the interval to query for
     * @param endTimeStamp Ending time of the interval to query for
     * @param walkList List of Walks to be filled by the function
     */
    @Override
    public void getWalks(long startTimeStamp, long endTimeStamp, final List<Walk> walkList) {

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTimeStamp, endTimeStamp, TimeUnit.MILLISECONDS)
                //.read(DataType.TYPE_STEP_COUNT_DELTA)
                .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setSessionName(SESSION_NAME)
                .enableServerQueries()
                .build();

        if(lastSignedInAccount != null) {
            Task<SessionReadResponse> task = Fitness.getSessionsClient(context, lastSignedInAccount)
                    .readSession(readRequest)
                    .addOnSuccessListener(sessionReadResponse -> {
                        List<Session> sessions = sessionReadResponse.getSessions();
                        Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                                + sessions.size());

                        for (Session session : sessions) {
                            long startTimeStamp1 = session.getStartTime(TimeUnit.MILLISECONDS);
                            long endTimeStamp1 = session.getEndTime(TimeUnit.MILLISECONDS);
                            long tally = 0;

                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session, DataType.TYPE_STEP_COUNT_DELTA);
                            if(dataSets.isEmpty()) continue;
                            for (DataSet dataSet : dataSets) {
                                if(dataSet.isEmpty()) continue;
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        tally += dp.getValue(field).asInt();
                                    }
                                }
                                //Log.i(TAG, "Data returned for Data type " + dataSet.getDataType().getName() + ": " + tally);
                            }
                            walkList.add(new Walk(tally, startTimeStamp1, endTimeStamp1));
                        }
                    })
                    .addOnFailureListener(e -> Log.i(TAG, "Failed to read session"));
        }
    }

    @Override
    public void getStepsCount(long startTimeStamp, long endTimeStamp, final List<Integer> stepsList) {
        if(lastSignedInAccount != null) {
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTimeStamp, endTimeStamp, TimeUnit.MILLISECONDS)
                    .enableServerQueries()
                    .build();

            Fitness.getHistoryClient(context, lastSignedInAccount).readData(readRequest)
                    .addOnSuccessListener(dataReadResponse -> {
                        List<Bucket> buckets = dataReadResponse.getBuckets();
                        Log.i(TAG, "Step counts retrieved: " + buckets.size());
                        for (Bucket bucket : buckets) {
                            int tally = 0;
                            DataSet dataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
                            if(dataSet == null) continue;
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                for (Field field : dp.getDataType().getFields()) {
                                    tally += dp.getValue(field).asInt();
                                }
                                //Log.i(TAG, "Data returned for Data type " + dataSet.getDataType().getName() + ": " + tally);
                            }
                            stepsList.add(tally);
                        }
                    })
                    .addOnFailureListener(e -> Log.i(TAG, "Failed to read session"));
        }
    }

    public void getDays(long startTimestamp, long endTimestamp, Callback<List<Day>> dayCallback) {

    }


}

