package com.example.lets_snack.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.ShoppingListDto
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository
import com.example.lets_snack.databinding.ActivitySaveIngredientBinding
import com.example.lets_snack.presentation.adapter.ShoppingListAdapter
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SaveIngredient : AppCompatActivity() {
    private lateinit var binding: ActivitySaveIngredientBinding
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var personsRepository: PersonsRepository
    private val email: String = FirebaseAuth.getInstance().currentUser?.email ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveIngredientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        personsRepository = PersonsRepository(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_save_ingredient)
        recyclerView.layoutManager = LinearLayoutManager(this)
        binding.back.setOnClickListener {
            finish()
        }
        // Chamada para obter a lista de compras
        getShoppingList()
    }

    private fun getShoppingList() {
        val call = personsRepository.getShoppingListByUserEmail(email)

        call.enqueue(object : Callback<List<ShoppingListDto>> {
            override fun onResponse(
                call: Call<List<ShoppingListDto>>,
                response: Response<List<ShoppingListDto>>
            ) {
                if (response.isSuccessful) {
                    val shoppingLists = response.body() ?: emptyList()

                    // Configurando o adapter com a lista de compras
                    shoppingListAdapter = ShoppingListAdapter(shoppingLists)
                    findViewById<RecyclerView>(R.id.recycler_save_ingredient).adapter = shoppingListAdapter
                } else {
                    // Trate aqui o caso de resposta com erro
                    showError("Erro ao obter lista de compras: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ShoppingListDto>>, t: Throwable) {
                // Trate aqui o caso de falha na requisição
                showError("Falha ao se comunicar com o servidor: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        // Exibe a mensagem de erro conforme sua necessidade (Toast, TextView, etc.)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}