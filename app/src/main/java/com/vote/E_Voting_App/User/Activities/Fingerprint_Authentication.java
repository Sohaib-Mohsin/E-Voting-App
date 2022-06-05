package com.vote.E_Voting_App.User.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vote.E_Voting_App.R;

import java.util.HashMap;
import java.util.concurrent.Executor;

public class Fingerprint_Authentication extends AppCompatActivity {

    BiometricManager biometricManager;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo biometricInfo;
    FingerprintManager fingerprintManager;
    FingerprintManager.AuthenticationCallback callback;
    DatabaseReference databaseReference, databaseReference1, CheckRefer;
    String name = "";
    TextView Status;
    String Hardware = "Available", FingerPrint_Status = "Not Verified";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authentication);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Status = findViewById(R.id.status);

        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        databaseReference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Verification");
        databaseReference1 = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference();
        CheckRefer = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Voters");

        callback = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Status.setText(errString + " " + errorCode);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Status.setText(result.toString());
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Status.setText("Failed");

            }
        };

        fingerprintManager.authenticate(null, null, 0, callback, null);

        biometricManager = BiometricManager.from(Fingerprint_Authentication.this);

        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Status.setText("Fingerprint not Supported");
                Hardware = "Not Available";
                Save_Fp_Verification_Status();
                Status.setTextColor(Color.RED);
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Hardware not working!", Toast.LENGTH_SHORT).show();
                Hardware = "Hardware not working";
                Save_Fp_Verification_Status();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Status.setText("Please Assign Fingerprint first");
                Status.setTextColor(Color.RED);
                break;

            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(Fingerprint_Authentication.this, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Fingerprint_Authentication.this, "Problem in verification", Toast.LENGTH_LONG).show();
                FingerPrint_Status = "Problem in verification";
                Save_Fp_Verification_Status();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                FingerPrint_Status = "Fingerprint Verified";
                Save_Fp_Verification_Status();
                startActivity(new Intent(Fingerprint_Authentication.this, Facial_Recognization.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                FingerPrint_Status = "Fingerprint Verification failed";
                Save_Fp_Verification_Status();
                AlertDialog.Builder alert = new AlertDialog.Builder(Fingerprint_Authentication.this);
                alert.setMessage("Failed");
                alert.show();
            }
        });

        biometricInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Voting App").setDescription("FingerPrint Sensor")
                .setDeviceCredentialAllowed(true).build();

        biometricPrompt.authenticate(biometricInfo);

    }

    private void RemovePending() {
        FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Pending_Voters")
                .child(FirebaseAuth.getInstance().getUid()).removeValue();
    }

    public void Save_Fp_Verification_Status() {
        HashMap<String, String> map = new HashMap<>();
        map.put("FP_Hardware", Hardware);
        map.put("FP_Status", FingerPrint_Status);
        if (FirebaseAuth.getInstance().getUid() != null) {
            databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(map);
        }
    }

    public void Next(View view) {
        startActivity(new Intent(Fingerprint_Authentication.this, Facial_Recognization.class));
        finish();
    }

}