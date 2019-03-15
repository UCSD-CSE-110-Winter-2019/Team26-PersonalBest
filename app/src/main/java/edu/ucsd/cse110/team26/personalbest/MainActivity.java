package edu.ucsd.cse110.team26.personalbest;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private final int SIGN_IN_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private static final String TAG = "[MainActivity]";
    private boolean DEBUG = false;
    private boolean ESPRESSO = false;
    private FirebaseAuth mAuth;
    GoogleSignInClient gsoclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkEnvironment();

        if(!DEBUG) {
            FirebaseApp.initializeApp(this);
            mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(auth -> {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) launchStepCountActivity();
            });
            GoogleSignInOptions signInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestProfile()
                    .requestEmail()
                    .build();

            gsoclient = GoogleSignIn.getClient(this, signInOptions);
        }

        View btnGoToSteps = findViewById(R.id.buttonGoToSteps);
        btnGoToSteps.setOnClickListener(v -> {
            if(DEBUG) {
                launchStepCountActivity();
            } else {
                Intent signInIntent = gsoclient.getSignInIntent();
                startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(this::firebaseAuthWithGoogle)
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to authenticate with error: " + e);
                    });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,  (result) -> {
                        if (result.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            launchStepCountActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", result.getException());
                        }
                    }
                );
    }

    private void checkEnvironment() {
        // check if running in Firebase Test Lab
        String testLabSetting = Settings.System.getString(getContentResolver(), "firebase.test.lab");
        if ("true".equals(testLabSetting)) {
            DEBUG = true;
        }

        try {
            Class.forName("android.support.test.espresso.Espresso");
            ESPRESSO = true;
            DEBUG = true;
        } catch (ClassNotFoundException e) {
            ESPRESSO = false;
        }
        Log.i(TAG, "Env checked, debug flag is " + DEBUG);
        Log.i(TAG, "Env checked, espresso flag is " + ESPRESSO);

        DEBUG = true;
        ESPRESSO = true;
    }

    public void launchStepCountActivity() {
        Intent intent = new Intent(this, StepCountActivity.class);
        intent.putExtra("DEBUG", DEBUG);
        intent.putExtra("ESPRESSO", ESPRESSO);
        startActivity(intent);
    }

}
