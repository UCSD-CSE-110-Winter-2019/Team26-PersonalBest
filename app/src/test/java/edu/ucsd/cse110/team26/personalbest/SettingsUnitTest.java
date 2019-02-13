package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import androidx.test.platform.app.InstrumentationRegistry;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class SettingsUnitTest {
    private EditText goalEdit;
    private EditText heightEdit;
    private Button saveBtn;
    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        SettingsActivity settingsActivity = Robolectric.setupActivity(SettingsActivity.class);
        goalEdit = settingsActivity.findViewById(R.id.goalEdit);
        heightEdit = settingsActivity.findViewById(R.id.heightEdit);
        saveBtn = settingsActivity.findViewById(R.id.btnSettingsSave);
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
    }

    @Test
    public void testSaveGoal() {
        goalEdit.setText("1000");
        saveBtn.performClick();

        assertEquals( 1000, sharedPreferences.getInt("goal", 5000) );

        goalEdit.setText("10000");
        saveBtn.performClick();

        assertEquals( 10000, sharedPreferences.getInt("goal", 5000) );
    }

    @Test
    public void testSaveHeight() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);

        heightEdit.setText("8");
        saveBtn.performClick();

        assertEquals( 8, sharedPreferences.getInt("height", 0) );
    }
}