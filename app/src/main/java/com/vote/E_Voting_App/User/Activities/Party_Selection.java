package com.vote.E_Voting_App.User.Activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vote.E_Voting_App.User.Adapters.PartiesAdapter;
import com.vote.E_Voting_App.User.Models.Parties_Model;
import com.vote.E_Voting_App.R;

import java.util.ArrayList;

public class Party_Selection extends AppCompatActivity {

    RecyclerView recyclerView;
    PartiesAdapter partiesAdapter;
    ArrayList<Parties_Model> parties_modelArrayList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_selection);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        databaseReference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("All_Candidates");

        recyclerView = findViewById(R.id.recyclerView);
        parties_modelArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    parties_modelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Parties_Model parties_model = dataSnapshot.getValue(Parties_Model.class);
                        parties_modelArrayList.add(parties_model);
                    }
                    partiesAdapter = new PartiesAdapter(Party_Selection.this, parties_modelArrayList);
                    recyclerView.setAdapter(partiesAdapter);
                    partiesAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}