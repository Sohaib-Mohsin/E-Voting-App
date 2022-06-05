package com.vote.E_Voting_App.User.Activities;

import static com.vote.E_Voting_App.Login_Status.Check_Login_Status.Get_Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vote.E_Voting_App.Admin.Activities.Admin_Login;
import com.vote.E_Voting_App.Admin.Activities.Preview_Activity;
import com.vote.E_Voting_App.R;

import java.util.Objects;

public class Splash_Screen extends AppCompatActivity {

    DatabaseReference My_Status;
    String Status = "Not Voted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (FirebaseAuth.getInstance().getUid() != null) {

            if(Get_Login(Splash_Screen.this).equals("Admin")){
                startActivity(new Intent(Splash_Screen.this, Preview_Activity.class));
                finish();
            }
            else if(Get_Login(Splash_Screen.this).equals("User")){

                My_Status = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference();

                My_Status.child("My_Status").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Status = Objects.requireNonNull(snapshot.child("voted_to").getValue()).toString();
                            Intent intent = new Intent(Splash_Screen.this, Vote_Submitted.class);
                            intent.putExtra("party",Status);
                            startActivity(intent);
                            finish();
                        }
                        else if(!snapshot.exists()){
                            startActivity(new Intent(Splash_Screen.this, Fingerprint_Authentication.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            startActivity(new Intent(Splash_Screen.this, Account_Login.class));
            finish();
        }

    }
}
