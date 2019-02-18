package edu.ucsd.cse110.team26.personalbest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new Settings(getApplicationContext());

        EditText goalEdit = findViewById(R.id.goalEdit);
        NumberPicker feetNp = findViewById(R.id.feetNumberPicker);
        NumberPicker inchesNp = findViewById(R.id.inchesNumberPicker);

        feetNp.setMaxValue(8);
        feetNp.setMinValue(0);

        inchesNp.setMaxValue(11);
        inchesNp.setMinValue(0);

        goalEdit.setText(Integer.toString(settings.getGoal()));
        feetNp.setValue(settings.getHeight() / 12);
        inchesNp.setValue(settings.getHeight() % 12);

        Button btnSave = findViewById(R.id.btnSettingsSave);
        Button btnGoBack = findViewById(R.id.btnSettingsGoBack);

        btnGoBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                finish();
            }
        });

        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                save(v);
            }
        });

    }

    public void save(View view) {
        EditText goalEdit = findViewById(R.id.goalEdit);
        NumberPicker feetNp = findViewById(R.id.feetNumberPicker);
        NumberPicker inchesNp = findViewById(R.id.inchesNumberPicker);
        int newGoal = Integer.parseInt(goalEdit.getText().toString());

        if( newGoal >= 15000 ) {
            Toast.makeText(getApplicationContext(), "Error: Goal should be less than 15000", Toast.LENGTH_SHORT).show();
            return;
        }

        settings.saveGoal(newGoal);
        settings.saveHeight(feetNp.getValue(), inchesNp.getValue());
    }

}
