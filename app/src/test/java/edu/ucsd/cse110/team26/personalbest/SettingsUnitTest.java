package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class SettingsUnitTest {
    private EditText goalEdit;
    private NumberPicker feetNp;
    private NumberPicker inchesNp;
    private Button saveBtn;
    private Context context;
    private Settings settings;
    SettingsActivity activity;
    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        intent.putExtra("DOCUMENT_KEY", " ");
        activity = Robolectric.buildActivity(SettingsActivity.class, intent).create().get();
        goalEdit = activity.findViewById(R.id.goalEdit);
        feetNp = activity.findViewById(R.id.feetNumberPicker);
        inchesNp = activity.findViewById(R.id.inchesNumberPicker);
        saveBtn = activity.findViewById(R.id.btnSettingsSave);
        settings = new Settings(context, new ConcreteTimeStamper());
    }

    @Test
    public void testSaveGoal() {
        goalEdit.setText("1000");
        saveBtn.performClick();

        assertEquals( 1000,  settings.getGoal());

        goalEdit.setText("10000");
        saveBtn.performClick();

        assertEquals( 10000, settings.getGoal() );
    }

    @Test
    public void testErrorSaveGoal() {

        goalEdit.setText("1000");
        saveBtn.performClick();

        goalEdit.setText("1000000");
        saveBtn.performClick();

        assertEquals("Error: Goal should be less than 15000", ShadowToast.getTextOfLatestToast());
        assertEquals( 1000, settings.getGoal());

    }

    @Test
    public void testSaveHeight() {
        feetNp.setValue(100 / 12);
        inchesNp.setValue(100 % 12);
        saveBtn.performClick();

        assertEquals( 100, settings.getHeight() );
    }
}