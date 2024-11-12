package com.example.lets_snack.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.callbacks.ShoppingListCallback
import com.example.lets_snack.data.remote.dto.CheckedUserDto
import com.example.lets_snack.data.remote.dto.SaveIngredientDto
import com.example.lets_snack.data.remote.dto.ShoppingListDto
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository
import com.google.firebase.auth.FirebaseAuth

class ShoppingListCheckBoxAdapter(
    private val ingredients: List<SaveIngredientDto>,
    private val recipeId : String
) : RecyclerView.Adapter<ShoppingListCheckBoxAdapter.IngredientViewHolder>() {

    // Define o ViewHolder que segura as referências do layout de cada item
    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkboxItem: CheckBox = itemView.findViewById(R.id.checkBox)
        val textItemName: TextView = itemView.findViewById(R.id.name_save_ingredient)
        val saveButton: Button = itemView.findViewById(R.id.button_save) // Referência ao botão
    }

    // Infla o layout do item da lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkbox_shopping_list, parent, false)
        return IngredientViewHolder(view)
    }

    // Vincula os dados a cada item da lista
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.textItemName.text = ingredient.ingredientName
        holder.checkboxItem.isChecked = ingredient.isChecked
        // Ação do CheckBox (por exemplo, atualizar um estado)
        holder.checkboxItem.setOnCheckedChangeListener { _, isChecked ->
            ingredient.isChecked = isChecked
        }


        if (position == ingredients.size - 1) {
            holder.saveButton.visibility =
                View.VISIBLE
        } else {
            holder.saveButton.visibility = View.GONE // Torna o botão invisível nos outros itens
        }

        holder.saveButton.setOnClickListener {
            val personsRepository = PersonsRepository(it.context)
            holder.saveButton.setOnClickListener {
                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                val checkedUserDto = CheckedUserDto(recipeId, ingredients, "")

                personsRepository.checkIngredients(currentUserEmail, checkedUserDto, object :
                    ShoppingListCallback {
                    override fun onSuccess(recipes: ShoppingListDto) {
                        Toast.makeText(it.context, "Ingredientes salvos com sucesso", Toast.LENGTH_LONG).show()
                    }

                    override fun onMessage(message: String) {
                        Log.d("Mensagem", "${message}")
                  }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("Erro", "${throwable.message}")
                    }
                })
            }
        }
        }

        // Retorna o número total de itens na lista
        override fun getItemCount(): Int = ingredients.size
    }

