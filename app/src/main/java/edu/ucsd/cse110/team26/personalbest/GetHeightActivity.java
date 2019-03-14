package edu.ucsd.cse110.team26.personalbest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetHeightActivity extends AppCompatActivity {
    private static final String TAG = "GetHeightActivity";
    private String fitnessServiceKey = "GOOGLE_FIT";
    private Settings settings;
    DocumentReference user_data;
    String COLLECTION_KEY = "users";
    String NAME;
    String UID;
    String DOCUMENT_KEY;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_height);

        DOCUMENT_KEY = getIntent().getExtras().getString("EMAIL");
        NAME = getIntent().getExtras().getString("NAME");
        UID = getIntent().getExtras().getString("UID");
        user_data = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY);

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

                    settings = new Settings(getApplicationContext(), new ConcreteTimeStamper());
                    settings.saveHeight(np1.getValue(), np2.getValue());
                    int height = np1.getValue() * 12 + np2.getValue();

                    getInitialHeight(height);

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
    public void getInitialHeight(int height)
    {
        user = new User(height, NAME, DOCUMENT_KEY, UID);
        user_data.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "successfully written");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing");
            }
        });
    }
}
