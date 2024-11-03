package com.example.lets_snack.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.dto.CommentDto;
import com.example.lets_snack.data.remote.dto.StepDto;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    List<String> stepDtoList;

    public StepAdapter(List<String> steps) {
        this.stepDtoList = steps;
    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_component, parent, false);
        StepViewHolder pvh = new StepViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.descriptionView.setText(stepDtoList.get(position).trim());
    }

    @Override
    public int getItemCount() {
        return stepDtoList.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionView;
        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionView = itemView.findViewById(R.id.step_component_description);
        }
    }
}
