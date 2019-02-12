package edu.ucsd.cse110.team26.personalbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class GetHeight extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_height);

        final TextView resultFeet = findViewById(R.id.feetText);
        final TextView resultInch = findViewById(R.id.inchText);

        Button confirmButton = findViewById(R.id.confirm);

        final NumberPicker np1 = findViewById(R.id.feet);
        final NumberPicker np2 = findViewById(R.id.inch);
        np1.setMinValue(0);
        np1.setMaxValue(20);

        np2.setMinValue(0);
        np2.setMaxValue(20);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultFeet.setText(String.valueOf(np1.getValue()));
                resultInch.setText(String.valueOf(np2.getValue()));
            }
        });
    }
}
