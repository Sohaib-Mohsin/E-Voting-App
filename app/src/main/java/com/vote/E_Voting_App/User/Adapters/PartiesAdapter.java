package com.vote.E_Voting_App.User.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vote.E_Voting_App.User.Activities.Vote_Submitted;
import com.vote.E_Voting_App.User.Models.Parties_Model;
import com.vote.E_Voting_App.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PartiesAdapter extends RecyclerView.Adapter<PartiesAdapter.ViewHolder> {

    Context context;
    ArrayList<Parties_Model> parties_modelArrayList;
    String Votes = "0", total_votes = "0", My_Name = "";
    AlertDialog dialog;

    public PartiesAdapter(Context context, ArrayList<Parties_Model> parties_modelArrayList) {
        this.context = context;
        this.parties_modelArrayList = parties_modelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parties_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Parties_Model parties_model = parties_modelArrayList.get(position);
        holder.Name.setText(parties_model.getPartyName());
        holder.Desc.setText(parties_model.getPartyDescription());
        try {
            Glide.with(context).load(parties_model.getImageURL()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }
            }).into(holder.Image);
        } catch (Exception ex) {
            Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        DatabaseReference My_Status = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("My_Status");
        DatabaseReference Candidate_Votes = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Candidate_Votes");
        DatabaseReference reference2 = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Individual");
        DatabaseReference Name = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Voters");

        holder.VoteNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Display_Dialog();

                Name.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        My_Name = snapshot.child("Voter_Name").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                Candidate_Votes.child(parties_model.getPartyName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Votes = snapshot.child("Votes").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                reference2.child("Votes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            total_votes = snapshot.child("voted").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setCancelable(false);
                alert.setTitle("Vote?").setMessage("Are you sure to vote for " + parties_model.getPartyName() + " ?");
                alert.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        My_Status.child(FirebaseAuth.getInstance().getUid()).child("voted_to").setValue(parties_model.getPartyName());

                        HashMap<String, Object> map2 = new HashMap<>();
                        map2.put("voted",(Integer.parseInt(total_votes)+1));
                        reference2.child("Votes").updateChildren(map2);

                        HashMap<String, String> map3 = new HashMap<>();
                        map3.put("Candidate",My_Name);
                        map3.put("Party",parties_model.getPartyName());
                        databaseReference.push().setValue(map3);

                        int t_votes = (Integer.parseInt(Votes) + 1);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("Votes",t_votes);

                        Candidate_Votes.child(parties_model.getPartyName()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(context, Vote_Submitted.class);
                                    intent.putExtra("party", parties_model.getPartyName());
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                } else {
                                    dialog.dismiss();
                                    Snackbar.make(holder.VoteNow, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getLocalizedMessage()), 2000).show();
                                }
                            }
                        });

                    }
                }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        dialogInterface.dismiss();
                    }
                }).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return parties_modelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView Name, Desc;
        CircleImageView Image;
        Button VoteNow;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.party_name);
            Desc = itemView.findViewById(R.id.party_description);
            Image = itemView.findViewById(R.id.party_image);
            VoteNow = itemView.findViewById(R.id.vote);
            progressBar = itemView.findViewById(R.id.item_progressbar);

        }
    }

    public void Display_Dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        View v = LayoutInflater.from(context).inflate(R.layout.custom_progressdialog, null);
        alertDialog.setCancelable(false);
        alertDialog.setView(v);
        dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}
