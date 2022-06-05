package com.vote.E_Voting_App.Admin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vote.E_Voting_App.Admin.Adapters.Individual_Adapter;
import com.vote.E_Voting_App.Admin.Models.Individual_Model;
import com.vote.E_Voting_App.R;

import java.util.ArrayList;

public class Individual_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Individual_Model> individual_modelArrayList;
    Individual_Adapter individual_adapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

        recyclerView = findViewById(R.id.recyclerView);
        individual_modelArrayList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Individual");

        getIndividual();

    }

    private void getIndividual() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    individual_modelArrayList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Individual_Model individual_model = dataSnapshot.getValue(Individual_Model.class);
                        individual_modelArrayList.add(individual_model);
                    }
                    individual_adapter = new Individual_Adapter(individual_modelArrayList, Individual_Activity.this);
                    recyclerView.setAdapter(individual_adapter);
                    individual_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}