package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.FindRecipeByIdDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecipesService {

    @GET("/recipes/listRecipesByRestrictions")
    Call<List<RecipeDto>> findRecipesByRestrictions(@Body Object params);

    @GET("/recipes/listRecipesById")
    Call<RecipeDto> findRecipeById(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @PUT("/recipes/insertComent/{recipesId}")
    Call<Object> insertComent(@Path("recipesId") String recipesId, @Body Object params);

}
