package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.ucsd.cse110.team26.personalbest.FitnessService;
import edu.ucsd.cse110.team26.personalbest.FitnessServiceFactory;

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

                publishProgress(resp);

                while(true) {
                    Thread.sleep(1000);
                    int s = fitnessService.getStepCount();
                    if (s != -1) resp = s;
                    publishProgress(resp);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            return resp;
        }

        protected void onProgressUpdate(Integer... params) {
            textSteps.setText(String.valueOf(params[0]));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        textSteps = findViewById(R.id.textSteps);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        textSteps.setText(String.valueOf(fitnessService.getStepCount()));

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
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                textSteps.setText(String.valueOf(fitnessService.getStepCount()));
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    /*public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount));
    }*/

    public void setCurrentStep(String stepsTaken)
    {
        currentStep = stepsTaken;
    }
    public void showEncouragement(String stepsTaken)
    {
        Context context = getApplicationContext();

        int numStep = Integer.parseInt(stepsTaken);
        if(numStep >= 1000)
        {
            int percentInt = numStep/500;
            //String percentString = String.valueOf(percentInt);
            CharSequence text = "Good job! You're already at " + percentInt + "% of the daily recommended number of steps";

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
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
            Toast.makeText(this, "settings is Clicked", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(StepCountActivity.this, SettingsActivity.class);

            StepCountActivity.this.startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

