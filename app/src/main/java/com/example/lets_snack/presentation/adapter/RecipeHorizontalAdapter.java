package com.example.lets_snack.presentation.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.presentation.recipe.FragmentRecipe;
import com.example.lets_snack.presentation.recipesFeed.FragmentRecipesFeed;

import java.util.List;

public class RecipeHorizontalAdapter extends RecyclerView.Adapter<RecipeHorizontalAdapter.RecipeViewHolder> {
    List<RecipeDto> recipeDtoList;

    public RecipeHorizontalAdapter(List<RecipeDto> recipes) {
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
        Glide.with(holder.photoView.getContext())
                .asBitmap()
                .load(recipeDtoList.get(position).getUrlPhoto())
                .into(holder.photoView);

        holder.likeView.setChecked(recipeDtoList.get(position).getIsFavorite());

        if(recipeDtoList.get(position).getRating() != null) {
            holder.rateView.setText(String.format("%.1f", recipeDtoList.get(position).getRating()));
        }
        else{
            holder.rateView.setText("0.0");
        }

        if (position == 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(40, 0, 32, 0);
        }
        else {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMargins(0, 0, 32, 0);
        }

        //entrando na tela de busca de receitas pela categoria
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", recipeDtoList.get(position).getId());

                //chamando fragment de recipe feed
                FragmentTransaction transaction = ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
                FragmentRecipe fragmentRecipe = new FragmentRecipe();
                fragmentRecipe.setArguments(bundle);
                transaction.replace(R.id.mainContainer, fragmentRecipe);
                transaction.addToBackStack(null);
                transaction.commit();

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
