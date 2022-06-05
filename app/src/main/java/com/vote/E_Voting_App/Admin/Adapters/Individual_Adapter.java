package com.vote.E_Voting_App.Admin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vote.E_Voting_App.Admin.Models.Individual_Model;
import com.vote.E_Voting_App.R;

import java.util.ArrayList;

public class Individual_Adapter extends RecyclerView.Adapter<Individual_Adapter.ViewHolder>{

    ArrayList<Individual_Model> individual_modelArrayList;
    Context context;

    public Individual_Adapter(ArrayList<Individual_Model> individual_modelArrayList, Context context) {
        this.individual_modelArrayList = individual_modelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_individual, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Individual_Model individual_model = individual_modelArrayList.get(position);
        holder.Candidate.setText(individual_model.getCandidate()+" votes "+individual_model.getParty());

    }

    @Override
    public int getItemCount() {
        return individual_modelArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView Candidate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Candidate = itemView.findViewById(R.id.candidate_name);

        }
    }

}
