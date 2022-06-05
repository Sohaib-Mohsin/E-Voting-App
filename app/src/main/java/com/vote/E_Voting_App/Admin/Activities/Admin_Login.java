package com.vote.E_Voting_App.Admin.Activities;

import static com.vote.E_Voting_App.Login_Status.Check_Login_Status.Save_Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.vote.E_Voting_App.Login_Status.Check_Login_Status;
import com.vote.E_Voting_App.R;
import com.vote.E_Voting_App.User.Activities.Account_Login;

public class Admin_Login extends AppCompatActivity {

    EditText Email, Password;
    Button LoginToAccount;
    AlertDialog dialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference My_Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        LoginToAccount = findViewById(R.id.Login);

        firebaseAuth = FirebaseAuth.getInstance();

        LoginToAccount.setOnClickListener(view -> {
            String U_Email = Email.getText().toString(), U_Password = Password.getText().toString();
            if (TextUtils.isEmpty(U_Password) || !Patterns.EMAIL_ADDRESS.matcher(U_Email).matches()) {
                Toast.makeText(Admin_Login.this, "Please insert valid data!", Toast.LENGTH_LONG).show();
            } else {
                Display_Dialog();

                firebaseAuth.signInWithEmailAndPassword(U_Email, U_Password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(Admin_Login.this, "Admin Logged In successfully!", Toast.LENGTH_LONG).show();
                        Save_Login(Admin_Login.this, "Admin");
                        startActivity(new Intent(Admin_Login.this, Preview_Activity.class));
                        finish();
                    } else {
                        dialog.dismiss();
                        Snackbar.make(LoginToAccount, task.getException().getLocalizedMessage(), 1000).show();
                    }
                });
            }
        });
    }
    public void Display_Dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Admin_Login.this, android.R.style.Theme_Material_Dialog_Alert);
        View v = getLayoutInflater().inflate(R.layout.custom_progressdialog, null);
        alertDialog.setCancelable(false);
        alertDialog.setView(v);
        dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    public void go_back(View view) {
        startActivity(new Intent(Admin_Login.this, Account_Login.class));
        finish();
    }
}