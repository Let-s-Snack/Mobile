package com.example.lets_snack.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.dto.IngredientDto;
import com.example.lets_snack.data.remote.dto.StepDto;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    List<IngredientDto> ingredientsDtoList;

    public IngredientsAdapter(List<IngredientDto> ingredients) {
        this.ingredientsDtoList = ingredients;
    }


    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_component, parent, false);
        IngredientViewHolder pvh = new IngredientViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nameView.setText(ingredientsDtoList.get(position).getName());
        String quantity = ingredientsDtoList.get(position).getQuantity();
        if (Double.parseDouble(quantity) == Math.floor(Double.parseDouble(quantity))) {
            quantity =  String.valueOf(Math.round(Double.parseDouble(quantity)));
        }

        if(Integer.parseInt(quantity) > 0 && quantity != null) {
            holder.descriptionView.setText("(" + quantity + " " + ingredientsDtoList.get(position).getMeditionType() + ")");
        }
        else{
            holder.descriptionView.setText("(" + ingredientsDtoList.get(position).getMeditionType() + ")");
        }

        if(ingredientsDtoList.get(position).isSwift() == true){
            System.out.println("isSwift: " + ingredientsDtoList.get(position).isSwift());
            holder.isSwiftIngredient.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return ingredientsDtoList.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionView;
        TextView nameView;
        ImageView isSwiftIngredient;
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.ingredient_component_name);
            descriptionView = itemView.findViewById(R.id.ingredient_component_medition);
            isSwiftIngredient = itemView.findViewById(R.id.ingredient_component_isSwift);
        }
    }
}
