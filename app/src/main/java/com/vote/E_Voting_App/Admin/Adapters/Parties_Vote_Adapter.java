package com.vote.E_Voting_App.Admin.Adapters;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class Parties_Vote_Adapter extends RecyclerView.Adapter<Parties_Vote_Adapter.ViewHolder> {

    Context context;
    ArrayList<Parties_Model> parties_modelArrayList;
    String Votes = "0";
    AlertDialog dialog;

    public Parties_Vote_Adapter(Context context, ArrayList<Parties_Model> parties_modelArrayList) {
        this.context = context;
        this.parties_modelArrayList = parties_modelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_voting_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Parties_Model parties_model = parties_modelArrayList.get(position);
        holder.Name.setText(parties_model.getPartyName());
        holder.Candidate.setText(parties_model.getCandidate());
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
            holder.Image.setImageResource(R.drawable.image);
        }

        DatabaseReference Candidate_Votes = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Candidate_Votes");

        Candidate_Votes.child(parties_model.getPartyName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Votes = snapshot.child("Votes").getValue().toString();
                    holder.Vote.setText(Votes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return parties_modelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView Name, Vote, Candidate;
        CircleImageView Image;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Candidate = itemView.findViewById(R.id.candidate);
            Name = itemView.findViewById(R.id.party_name);
            Vote = itemView.findViewById(R.id.party_votes);
            Image = itemView.findViewById(R.id.circleImageView);
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
