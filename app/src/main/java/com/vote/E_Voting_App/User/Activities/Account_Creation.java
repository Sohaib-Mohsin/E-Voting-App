package com.vote.E_Voting_App.User.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OtpView;
import com.vote.E_Voting_App.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Account_Creation extends AppCompatActivity {

    EditText Name, Email, Password, Phone_Number;
    Button CreateAccount;
    AlertDialog dialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    int t_votes = 0;
    Dialog alertDialog;
    private String VerificationID;
    private boolean verified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        CreateAccount = findViewById(R.id.Create);
        Phone_Number = findViewById(R.id.phone_number);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference();

        FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference()
                .child("Votes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        t_votes = Integer.parseInt(snapshot.child("t_voters").getValue().toString()) + 1;


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        CreateAccount.setOnClickListener(view -> {

            String phone_number = Phone_Number.getText().toString(), U_Name = Name.getText().toString(), U_Email = Email.getText().toString(), U_Password = Password.getText().toString();

            if (TextUtils.isEmpty(U_Name) || TextUtils.isEmpty(U_Password) || !Patterns.EMAIL_ADDRESS.matcher(U_Email).matches() || TextUtils.isEmpty(phone_number)) {
                Toast.makeText(Account_Creation.this, "Please insert valid data!", Toast.LENGTH_LONG).show();
            } else {
                Display_Dialog();
                sendCode(phone_number, U_Name, U_Email, U_Password);
            }
        });
    }

    public void Display_Dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Account_Creation.this, android.R.style.Theme_Material_Dialog_Alert);
        View v = getLayoutInflater().inflate(R.layout.custom_progressdialog, null);
        alertDialog.setCancelable(false);
        alertDialog.setView(v);
        dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    public void Append(String name, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                HashMap<String, String> map = new HashMap<>();
                map.put("Voter_Name", name);
                map.put("Voter_Email", email);
                map.put("Voter_Password", password);

                databaseReference.child("Voters").child(firebaseAuth.getUid()).setValue(map);
                HashMap<String, Object> map2 = new HashMap<>();
                map2.put("t_voters", (Integer.toString(t_votes)));
                FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Votes").updateChildren(map2)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(Account_Creation.this, "Voter Account Created", Toast.LENGTH_LONG).show();
                                    Snackbar.make(CreateAccount, "Login to Continue", 1000).show();
                                    firebaseAuth.signOut();
                                    startActivity(new Intent(Account_Creation.this, Account_Login.class));
                                    finish();

                                } else {
                                    dialog.dismiss();
                                    Snackbar.make(CreateAccount, task.getException().getLocalizedMessage(), 1000).show();

                                }
                            }
                        });

            } else {
                dialog.dismiss();
                Snackbar.make(CreateAccount, task.getException().getLocalizedMessage(), 1000).show();
            }
        });

    }


    private void sendCode(String number, String Name, String Email, String Password) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(Account_Creation.this).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);

                        dialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(Account_Creation.this, R.style.material);
                        View v = getLayoutInflater().inflate(R.layout.custom_verification_code_layout, null);

                        builder.setView(v);
                        builder.setCancelable(false);
                        alertDialog = builder.create();
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        OtpView otpView = v.findViewById(R.id.otpView);
                        TextView otpText = v.findViewById(R.id.code_text);
                        Button Verify = v.findViewById(R.id.verify);

                        otpText.setText("Enter OTP sent to ".concat(number));

                        otpView.setOtpCompletionListener(otp -> {

                            try {

                                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(VerificationID, otp);
                                firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        verified = true;
                                    } else {
                                        Toast.makeText(Account_Creation.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception ex) {
                                Toast.makeText(Account_Creation.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });
                        VerificationID = verifyID;

                        Verify.setOnClickListener(view -> {

                            if (verified) {
                                alertDialog.dismiss();
                                dialog.show();
                                Append(Name, Email, Password);

                            } else {
                                alertDialog.dismiss();
                                dialog.show();
                                Toast.makeText(Account_Creation.this, "Error in Verifying Phone Number", Toast.LENGTH_SHORT).show();
                            }

                        });

                        alertDialog.show();

                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public void Login(View view) {
        startActivity(new Intent(Account_Creation.this, Account_Login.class));
        finish();
    }
}