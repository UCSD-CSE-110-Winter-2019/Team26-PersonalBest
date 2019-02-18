package edu.ucsd.cse110.team26.personalbest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("height", Integer.parseInt(heightEdit.getText().toString()));
        editor.putBoolean("new_week", false);
        editor.putInt("goal",Integer.parseInt(goalEdit.getText().toString()));

        int new_goal = Integer.parseInt(goalEdit.getText().toString());
        Calendar calendar = Calendar.getInstance();

        int current_day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (current_day)
        {
            case Calendar.SUNDAY:
                editor.putInt("goal_Sun", new_goal);
            case Calendar.MONDAY:
                editor.putInt("goal_Mon", new_goal);
            case Calendar.TUESDAY:
                editor.putInt("goal_Tue", new_goal);
            case Calendar.WEDNESDAY:
                editor.putInt("goal_Wed", new_goal);
            case Calendar.THURSDAY:
                editor.putInt("goal_Thu", new_goal);
            case Calendar.FRIDAY:
                editor.putInt("goal_Fri", new_goal);
            case Calendar.SATURDAY:
                editor.putInt("goal_Sat", new_goal);
                break;
            default:
                break;

        }

        editor.apply();
    }



}
