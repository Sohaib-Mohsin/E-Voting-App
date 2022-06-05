package com.vote.E_Voting_App.User.Activities;

import static com.vote.E_Voting_App.Login_Status.Check_Login_Status.Save_Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vote.E_Voting_App.Admin.Activities.Admin_Login;
import com.vote.E_Voting_App.R;

public class Account_Login extends AppCompatActivity {

    EditText Email, Password;
    Button LoginToAccount;
    AlertDialog dialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference My_Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        LoginToAccount = findViewById(R.id.Login);

        firebaseAuth = FirebaseAuth.getInstance();

        LoginToAccount.setOnClickListener(view -> {
            String U_Email = Email.getText().toString(), U_Password = Password.getText().toString();
            if (TextUtils.isEmpty(U_Password) || !Patterns.EMAIL_ADDRESS.matcher(U_Email).matches()) {
                Toast.makeText(Account_Login.this, "Please insert valid data!", Toast.LENGTH_LONG).show();
            } else {
                Display_Dialog();

                firebaseAuth.signInWithEmailAndPassword(U_Email, U_Password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Save_Login(Account_Login.this, "User");
                        My_Status = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference();

                        My_Status.child("My_Status").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    dialog.dismiss();
                                    String Status = snapshot.child("voted_to").getValue().toString();
                                    Intent intent = new Intent(Account_Login.this, Vote_Submitted.class);
                                    intent.putExtra("party", Status);
                                    startActivity(intent);
                                    finish();
                                } else if (!snapshot.exists()) {

                                    dialog.dismiss();
                                    startActivity(new Intent(Account_Login.this, Fingerprint_Authentication.class));
                                    finish();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    } else {
                        dialog.dismiss();
                        Snackbar.make(LoginToAccount, task.getException().getLocalizedMessage(), 1000).show();
                    }
                });
            }
        });

    }

    public void Display_Dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Account_Login.this, android.R.style.Theme_Material_Dialog_Alert);
        View v = getLayoutInflater().inflate(R.layout.custom_progressdialog, null);
        alertDialog.setCancelable(false);
        alertDialog.setView(v);
        dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    public void NewUser(View view) {
        startActivity(new Intent(Account_Login.this, Account_Creation.class));
        finish();
    }

    public void admin_login(View view) {
        startActivity(new Intent(Account_Login.this, Admin_Login.class));
        finish();
    }
}