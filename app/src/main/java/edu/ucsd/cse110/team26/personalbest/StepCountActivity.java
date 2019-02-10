package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class StepCountActivity extends AppCompatActivity {

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private UpdateStep updateStep;

    private TextView textSteps;
    private FitnessService fitnessService;

    private class UpdateStep extends AsyncTask<Integer, Integer, Integer> {
        private int resp;

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                resp = params[0];
                while(true) {
                    fitnessService.getStepCount();
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            return resp;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        textSteps = findViewById(R.id.textSteps);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(updateStep != null && !updateStep.isCancelled()) updateStep.cancel(true);
        updateStep = new UpdateStep();
        updateStep.execute(-1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(updateStep != null) updateStep.cancel(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                if(updateStep != null && !updateStep.isCancelled()) updateStep.cancel(true);
                updateStep = new UpdateStep();
                updateStep.execute(-1);
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(StepCountActivity.this, SettingsActivity.class);
            StepCountActivity.this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

