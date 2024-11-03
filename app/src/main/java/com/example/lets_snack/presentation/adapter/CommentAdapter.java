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

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    List<CommentDto> commentDtoList;

    public CommentAdapter(List<CommentDto> comments) {
        this.commentDtoList = comments;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);
        CommentViewHolder pvh = new CommentViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.usernameView.setText(commentDtoList.get(position).getPersonsName());
        holder.descriptionView.setText(commentDtoList.get(position).getMessage());
        holder.dateView.setText("Há "+ commentDtoList.get(position).getCreationDate() + " dias");
        holder.rateView.setRating(commentDtoList.get(position).getRating());

        //adicionando margem apenas para o primeiro e último item
        if (position == 0) {ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(56, 0, 56, 0);
        }
    }

    @Override
    public int getItemCount() {
        return commentDtoList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView usernameView;
        RatingBar rateView;
        TextView descriptionView;
        TextView dateView;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.comment_card_username);
            rateView = itemView.findViewById(R.id.comment_card_rate);
            descriptionView = itemView.findViewById(R.id.comment_card_description);
            dateView = itemView.findViewById(R.id.comment_card_date);
        }
    }
}
