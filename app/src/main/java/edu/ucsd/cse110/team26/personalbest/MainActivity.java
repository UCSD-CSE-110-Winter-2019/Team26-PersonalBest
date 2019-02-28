package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[MainActivity]";
    private boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGoToSteps = findViewById(R.id.buttonGoToSteps);
        btnGoToSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchStepCountActivity();
            }
        });

        checkEnvironment();
    }

    private void checkEnvironment() {

        // check if running debug build variant
        //DEBUG = BuildConfig.DEBUG;

        // check if running in Firebase Test Lab
        String testLabSetting = Settings.System.getString(getContentResolver(), "firebase.test.lab");
        if ("true".equals(testLabSetting)) {
            DEBUG = true;
        }
        Log.i(TAG, "Env checked, debug flag is " + DEBUG);
    }

    public void launchStepCountActivity() {
        Intent intent = new Intent(this, StepCountActivity.class);
        intent.putExtra("DEBUG", DEBUG);
        startActivity(intent);
    }

}
