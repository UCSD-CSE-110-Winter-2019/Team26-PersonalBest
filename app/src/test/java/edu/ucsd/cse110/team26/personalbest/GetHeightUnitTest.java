package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.NumberPicker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import androidx.test.platform.app.InstrumentationRegistry;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class GetHeightUnitTest {

    private NumberPicker inch;
    private NumberPicker feet;

    private Button confirm;
    private Context context;
    private SharedPreferences sharedPreferences;
    GetHeightActivity activity;

    @Before
    public void setup() {

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra("DEBUG", true);
        intent.putExtra("EMAIL", " ");
        intent.putExtra("NAME", " " );
        intent.putExtra("UID", " ");
        activity = Robolectric.buildActivity(GetHeightActivity.class, intent).create().get();
        context = InstrumentationRegistry.getInstrumentation().getContext();

        inch = activity.findViewById(R.id.inch);
        feet = activity.findViewById(R.id.feet);
        confirm = activity.findViewById(R.id.confirm);
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
    }

    @Test
    public void testSetHeight() {
        feet.setValue(5);
        inch.setValue(10);
        confirm.performClick();
        assertFalse(feet.isEnabled());
        assertFalse(inch.isEnabled());
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        assertEquals( 70, sharedPreferences.getInt("height", 0 ));
    }
}
