package edu.ucsd.cse110.team26.personalbest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new Settings(getApplicationContext(), new ConcreteTimeStamper() );

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

        btnGoBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> save(v));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

        settings.saveTodayGoal(newGoal);
        settings.saveUserHeight(feetNp.getValue(), inchesNp.getValue());

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //editor.putInt("height", Integer.parseInt(heightEdit.getText().toString()));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
