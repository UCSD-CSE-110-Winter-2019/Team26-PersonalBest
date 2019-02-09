package edu.ucsd.cse110.team26.personalbest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.ucsd.cse110.team26.personalbest.FitnessService;
import edu.ucsd.cse110.team26.personalbest.FitnessServiceFactory;

public class StepCountActivity extends AppCompatActivity {

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private TextView textSteps;
    private FitnessService fitnessService;
    private boolean isStartOn = false;
    private String currentStep;

    //private boolean goooo;

    private class UpdateStep extends AsyncTask<String, String, String>
    {
        private String resp;
        ProgressDialog progressDialog;

        //@Override
        protected String doInBackground(String... params) {


            try {
                //int time = Integer.parseInt(params[0]) * 1000;

                resp = params[0] + " steps";


                while (isStartOn)
                {
                    Thread.sleep(1000);
                    //steps taken
                    fitnessService.updateStepCount();
                    publishProgress(currentStep);
                }

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }

            return resp;
        }

        protected void onProgressUpdate(String... text)
        {
            textSteps.setText(text[0]);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        textSteps = findViewById(R.id.textSteps);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.updateStepCount();

        Button btnUpdateSteps = findViewById(R.id.buttonUpdateSteps);



        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isStartOn = true;

                UpdateStep updateStep = new UpdateStep();

                updateStep.execute(currentStep);


            }
        });

        Button stopButton = findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartOn = false;
            }
        });

        fitnessService.setup();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();

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

}

