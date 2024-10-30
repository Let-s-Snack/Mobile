package com.example.lets_snack.presentation.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lets_snack.MainActivity;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.presentation.recipesFeed.FragmentRecipesFeed;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    List<CategoryDto> categoryDtoList;
    int count = 0;

    public CategoryAdapter(List<CategoryDto> categories) {
        this.categoryDtoList = categories;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
        CategoryViewHolder pvh = new CategoryViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //adicionando informações no card
        holder.titleView.setText(categoryDtoList.get(position).getName());
        String url = categoryDtoList.get(position).getUrlPhoto();
        Glide.with(holder.photoView.getContext())
                .asBitmap()
                .load(url)
                .centerCrop()
                .circleCrop()
                .into(holder.photoView);

        //adicionando margem apenas para items pares, para manter o espaçamento central
        if (position % 2 == 0) {ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
             layoutParams.setMarginEnd(32);
        }

        //aplicando lógica para intercalar cores dos cards
        if (position != 0) {
            if(count == 0 || count == 1) {
                holder.cardView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.laranja));
                count++;
            }
            else{
                count++;
                if(count == 4){
                    count = 0;
                }
            }
        }

        //entrando na tela de busca de receitas pela categoria
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", categoryDtoList.get(position).getName());
                bundle.putString("description", categoryDtoList.get(position).getDescription());
                bundle.putString("id", categoryDtoList.get(position).getId());

                //chamando fragment de recipe feed
                FragmentTransaction transaction = ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
                FragmentRecipesFeed fragmentRecipesFeed = new FragmentRecipesFeed();
                fragmentRecipesFeed.setArguments(bundle);
                transaction.replace(R.id.mainContainer, fragmentRecipesFeed);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryDtoList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        ImageView photoView;
        ConstraintLayout cardView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.category_name);
            photoView = itemView.findViewById(R.id.category_photo);
            cardView = itemView.findViewById(R.id.card_category);
        }
    }
}
