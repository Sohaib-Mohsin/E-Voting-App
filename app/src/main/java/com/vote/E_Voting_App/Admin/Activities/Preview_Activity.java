package com.vote.E_Voting_App.Admin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vote.E_Voting_App.Admin.Adapters.Parties_Vote_Adapter;
import com.vote.E_Voting_App.R;
import com.vote.E_Voting_App.User.Activities.Party_Selection;
import com.vote.E_Voting_App.User.Adapters.PartiesAdapter;
import com.vote.E_Voting_App.User.Models.Parties_Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Preview_Activity extends AppCompatActivity {

    TextView T_Voters, R_Votes, Voted;
    RecyclerView recyclerView;
    ImageView AddParty, Reset;
    ArrayList<Parties_Model> parties_modelArrayList;
    Parties_Vote_Adapter parties_vote_adapter;
    DatabaseReference databaseReference, reference;
    ProgressBar progressBar;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        databaseReference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("All_Candidates");
        reference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Votes");

        T_Voters = findViewById(R.id.votes);
        R_Votes = findViewById(R.id.r_votes);
        Voted = findViewById(R.id.voted);
        recyclerView = findViewById(R.id.recyclerView);
        AddParty = findViewById(R.id.new_Party);
        Reset = findViewById(R.id.reset);
        progressBar = findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);

        parties_modelArrayList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String t_votes = snapshot.child("t_voters").getValue().toString();
                    String voted = snapshot.child("voted").getValue().toString();

                    T_Voters.setText(t_votes);
                    Voted.setText(voted);

                    R_Votes.setText((Integer.parseInt(t_votes) - Integer.parseInt(voted)) + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        RetrieveData();

        AddParty.setOnClickListener(view -> startActivity(new Intent(Preview_Activity.this, Add_Party.class)));

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Preview_Activity.this);
                builder.setCancelable(false).setMessage("Delete all parties with voting detail?");
                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Display_Dialog();
                        FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("All_Candidates")
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Candidate_Votes")
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Individual")
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("My_Status")
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                        HashMap<String, Object> map = new HashMap<>();
                                                                        map.put("t_voters", "0");
                                                                        map.put("voted", "0");

                                                                        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(Preview_Activity.this, "Data Reset!", Toast.LENGTH_LONG).show();
                                                                                } else {
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(Preview_Activity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });

                                                                    } else {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(Preview_Activity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            dialog.dismiss();
                                                            Toast.makeText(Preview_Activity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(Preview_Activity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(Preview_Activity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                })
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });

    }

    private void RetrieveData() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    parties_modelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Parties_Model parties_model = dataSnapshot.getValue(Parties_Model.class);
                        parties_modelArrayList.add(parties_model);
                    }
                    parties_vote_adapter = new Parties_Vote_Adapter(Preview_Activity.this, parties_modelArrayList);
                    recyclerView.setAdapter(parties_vote_adapter);
                    parties_vote_adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void View_Individual(View view) {
        startActivity(new Intent(Preview_Activity.this, Individual_Activity.class));
    }

    public void Display_Dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Preview_Activity.this, android.R.style.Theme_Material_Dialog_Alert);
        View v = getLayoutInflater().inflate(R.layout.custom_progressdialog, null);
        alertDialog.setCancelable(false);
        alertDialog.setView(v);
        dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}