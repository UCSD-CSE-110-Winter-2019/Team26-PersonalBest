package edu.ucsd.cse110.team26.personalbest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class GetHeightActivity extends AppCompatActivity {

    private String fitnessServiceKey = "GOOGLE_FIT";
    private Settings settings;
    private boolean DEBUG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_height);

        DEBUG = getIntent().getBooleanExtra("DEBUG", false);

        final TextView resultFeet = findViewById(R.id.feetText);
        final TextView resultInch = findViewById(R.id.inchText);

        final Button confirmButton = findViewById(R.id.confirm);

        final NumberPicker np1 = findViewById(R.id.feet);
        final NumberPicker np2 = findViewById(R.id.inch);
        np1.setMinValue(0);
        np1.setMaxValue(8);

        np2.setMinValue(0);
        np2.setMaxValue(11);

        confirmButton.setTag(1);
        confirmButton.setText("Confirm");
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status =(Integer) v.getTag();

                if(status == 1) {
                    resultFeet.setText(String.valueOf(np1.getValue()));
                    resultInch.setText(String.valueOf(np2.getValue()));
                    np1.setEnabled(false);
                    np2.setEnabled(false);

                    settings = new Settings(getApplicationContext(), new ConcreteTimeStamper(), DEBUG);
                    settings.saveHeight(np1.getValue(), np2.getValue());

                    confirmButton.setText("Done");
                    v.setTag(0);
                } else {
                    v.setTag(1);
                    launchStepCountActivity();
                }

            }
        });
    }

    public void launchStepCountActivity() {
        finish();
    }
}
