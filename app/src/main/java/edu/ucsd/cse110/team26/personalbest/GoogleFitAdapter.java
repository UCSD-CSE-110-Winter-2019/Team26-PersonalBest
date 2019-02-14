package edu.ucsd.cse110.team26.personalbest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFitAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";
    private final String SESSION_NAME = "PersonalBestWalk";

    private StepCountActivity activity;
    private GoogleSignInAccount lastSignedInAccount;

    GoogleFitAdapter(StepCountActivity activity) {
        this.activity = activity;
    }

    public void setup() {

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity,
                    getRequestCode(),
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
        } else {
            startRecording();
            updateStepCount();
        }

    }

    private void startRecording() {
        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount != null) {
            Fitness.getRecordingClient(activity, lastSignedInAccount)
                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Successfully subscribed!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    });
        }
    }


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public void updateStepCount() {
        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount != null) {
            Fitness.getHistoryClient(activity, lastSignedInAccount)
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                        @Override
                        public void onSuccess(DataSet dataSet) {
                            int totalSteps = dataSet.isEmpty()
                                    ? 0
                                    : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                            Log.i(TAG, "Steps successfully read: " + totalSteps);

                            activity.setStepCount(totalSteps);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "There was a problem getting the step count.", e);
                        }
                    });
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
            Fitness.getSessionsClient(activity, lastSignedInAccount)
                    .insertSession(insertRequest)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Session insert was successful!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "There was a problem inserting the session: " +
                                    e.getLocalizedMessage());
                        }
                    });
        }
    }

    @Override
    public void getWalks(long startTimeStamp, long endTimeStamp, List<Walk> walkList) {

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTimeStamp, endTimeStamp, TimeUnit.MILLISECONDS)
                //.read(DataType.TYPE_STEP_COUNT_DELTA)
                .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setSessionName(SESSION_NAME)
                .enableServerQueries()
                .build();

        if(lastSignedInAccount != null) {
            Task<SessionReadResponse> task = Fitness.getSessionsClient(activity, lastSignedInAccount)
                    .readSession(readRequest)
                    .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                        @Override
                        public void onSuccess(SessionReadResponse sessionReadResponse) {
                            List<Session> sessions = sessionReadResponse.getSessions();
                            Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                                    + sessions.size());

                            for (Session session : sessions) {
                                long startTimeStamp = session.getStartTime(TimeUnit.MILLISECONDS);
                                long endTimeStamp = session.getEndTime(TimeUnit.MILLISECONDS);
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
                                    Log.i(TAG, "Data returned for Data type " + dataSet.getDataType().getName() + ": " + tally);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Failed to read session");
                        }
                    });
        }
    }

    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }


}

