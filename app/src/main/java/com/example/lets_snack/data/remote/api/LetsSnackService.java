package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.ExampleDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LetsSnackService {
    //Aqui ficará as nossas funções que irão consumir a api, essa é a primeira camada
    @GET("/photos")
    Call<List<ExampleDto>> exampleFunService();
}
