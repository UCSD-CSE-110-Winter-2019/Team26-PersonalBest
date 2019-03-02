package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.NumberPicker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

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

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        GetHeightActivity getHeightActivity = Robolectric.setupActivity(GetHeightActivity.class);
        inch = getHeightActivity.findViewById(R.id.inch);
        feet = getHeightActivity.findViewById(R.id.feet);
        confirm = getHeightActivity.findViewById(R.id.confirm);
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
