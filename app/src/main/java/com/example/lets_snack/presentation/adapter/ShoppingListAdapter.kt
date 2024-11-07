package com.example.lets_snack.presentation.adapter

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.ShoppingListDto

class ShoppingListAdapter(
    private val shoppingLists: List<ShoppingListDto>
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    // Define o ViewHolder para a lista de compras
    inner class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textListName: TextView = itemView.findViewById(R.id.name_save_receipe)
        val recyclerIngredients: RecyclerView = itemView.findViewById(R.id.recycler_save_ingredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        val viewHolder = ShoppingListViewHolder(view)

        // Adicionar espaçamento entre os itens do RecyclerView diretamente
        val spacing = -10 // Espaço entre os itens em pixels (ajuste conforme necessário)
        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = spacing // Defina o espaçamento entre os itens
            }
        }

        viewHolder.recyclerIngredients.addItemDecoration(itemDecoration)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingList = shoppingLists[position]

        // Nome da lista de compras
        holder.textListName.text = shoppingList.recipeName

        // Configurando o RecyclerView interno com o ShoppingListCheckBoxAdapter
        Log.d("ShoppingListAdapter", "onBindViewHolder: ${shoppingList.ingredients}")
        val ingredientsAdapter = ShoppingListCheckBoxAdapter(shoppingList.ingredients, shoppingList.recipesId)
        holder.recyclerIngredients.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context)
            adapter = ingredientsAdapter
            isNestedScrollingEnabled = false
        }
    }

    override fun getItemCount(): Int = shoppingLists.size
}
