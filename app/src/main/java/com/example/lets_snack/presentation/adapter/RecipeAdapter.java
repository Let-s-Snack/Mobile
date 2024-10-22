package com.example.lets_snack.presentation.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lets_snack.MainActivity;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.presentation.recipesFeed.FragmentRecipesFeed;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    List<RecipeDto> recipeDtoList;

    public RecipeAdapter(List<RecipeDto> recipes) {
        this.recipeDtoList = recipes;
    }


    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card, parent, false);
        RecipeViewHolder pvh = new RecipeViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //adicionando informações no card
        holder.nameView.setText(recipeDtoList.get(position).getName());
        String url = recipeDtoList.get(position).getUrlPhoto();
        Glide.with(holder.photoView.getContext())
                .asBitmap()
                .load("https://i.pinimg.com/control/564x/13/2c/ca/132ccab00cbe2774aa975c147c584aa8.jpg")
                .into(holder.photoView);
        holder.likeView.setChecked(recipeDtoList.get(position).isFavorite());
        if(recipeDtoList.get(position).getRating() != null) {
            holder.rateView.setText(String.valueOf(recipeDtoList.get(position).getRating()));
        }
        else{
            holder.rateView.setText("0.0");
        }

        //adicionando margem apenas para items pares, para manter o espaçamento central
//        if (position % 2 == 0) {ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
//            layoutParams.setMarginEnd(64);
//        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adicionar lógica e parâmetros para ir na tela da receita selecionada
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeDtoList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        ImageView photoView;
        TextView nameView;
        CheckBox likeView;
        RatingBar ratingBarView;
        TextView rateView;
        ConstraintLayout cardView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.recipe_photo);
            nameView = itemView.findViewById(R.id.recipe_name);
            likeView = itemView.findViewById(R.id.recipe_like_button);
            ratingBarView = itemView.findViewById(R.id.recipe_rating_bar);
            rateView = itemView.findViewById(R.id.recipe_rate);
            cardView = itemView.findViewById(R.id.card_recipe);
        }
    }
}
