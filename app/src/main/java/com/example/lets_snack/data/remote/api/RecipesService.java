package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.RecipeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecipesService {

    @GET("/recipes/listRecipesByRestriction")
    Call<List<RecipeDto>> findRecipesByRestrictions(@Query("restrictionsId") String recipesId, @Query("personsEmail") String personsId);
}
