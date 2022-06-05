package com.vote.E_Voting_App.User.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.vote.E_Voting_App.R;

public class Vote_Submitted extends AppCompatActivity {

    TextView Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_submitted);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        Status = findViewById(R.id.voted);
        Status.setText("Your vote is submitted to " +getIntent().getStringExtra("party"));
        Toast.makeText(this, "Vote Submitted to "+getIntent().getStringExtra("party"), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}