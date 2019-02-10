package edu.ucsd.cse110.team26.personalbest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class GoogleFitAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";


    private GoogleApiClient mGoogleApiClient;
    private StepCountActivity activity;

    private int steps = -1;

    GoogleFitAdapter(StepCountActivity activity) {
        this.activity = activity;
    }

    public void setup() {

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Fitness.SESSIONS_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .build();

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    getRequestCode(),
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
        } else {
            getStepCount();
            //startRecording();
        }
    }


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public int getStepCount() {

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);

        if (lastSignedInAccount != null) {
            Fitness.getHistoryClient(activity, lastSignedInAccount)
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                        @Override
                        public void onSuccess(DataSet dataSet) {
                            Log.d(TAG, dataSet.toString());
                            int totalSteps = dataSet.isEmpty() ? -1 : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                            Log.d(TAG, "Total steps: " + totalSteps);
                            setSteps(totalSteps);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "There was a problem getting the step count.", e);
                        }
                    });
        }

        return steps;
    }

    private void setSteps(int steps) {
        this.steps = steps;
    }


    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }
}

