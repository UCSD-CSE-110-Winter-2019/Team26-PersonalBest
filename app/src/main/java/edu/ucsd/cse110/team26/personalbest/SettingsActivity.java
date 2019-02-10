package edu.ucsd.cse110.team26.personalbest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static edu.ucsd.cse110.team26.personalbest.StepCountActivity.FITNESS_SERVICE_KEY;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE );

        EditText goalEdit = findViewById(R.id.goalEdit);
        EditText heightEdit = findViewById(R.id.heightEdit);
        goalEdit.setText(String.valueOf(sharedPreferences.getInt("goal", 5000)));
        heightEdit.setText(String.valueOf(sharedPreferences.getInt("height", 0)));

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
        EditText heightEdit = findViewById(R.id.heightEdit);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putInt("goal", Integer.parseInt(goalEdit.getText().toString()))
                .putInt("height", Integer.parseInt(heightEdit.getText().toString()));

        editor.apply();
    }

}
