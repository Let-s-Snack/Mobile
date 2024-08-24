package com.example.lets_snack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.lets_snack.data.remote.dto.ExampleDto
import com.example.lets_snack.data.remote.repository.rest.ExampleIRepositoryRest
import com.example.lets_snack.databinding.ActivityTesteApiBinding

class ExampleApi : AppCompatActivity() {
    private lateinit var binding: ActivityTesteApiBinding
    private val exampleIRepositoryRest = ExampleIRepositoryRest()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTesteApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            //aqui fazemos a chamada após o botão ser clicado, aqui ele já irá trazer os dados
            val call = exampleIRepositoryRest.exampleFunRepository()
            call.enqueue(object : retrofit2.Callback<List<ExampleDto>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ExampleDto>>,
                    response: retrofit2.Response<List<ExampleDto>>
                ) {
                    val fotos = response.body()
                    Log.d("TESTE", "onResponse: $fotos")
                }

                override fun onFailure(call: retrofit2.Call<List<ExampleDto>>, t: Throwable) {
                }
            })
        }
    }
}