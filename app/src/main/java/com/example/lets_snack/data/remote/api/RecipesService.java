package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.RecipeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RecipesService {

    @GET("/recipes/listRecipesByRestrictions")
    Call<List<RecipeDto>> findRecipesByRestrictions(@Body Object params);
}
