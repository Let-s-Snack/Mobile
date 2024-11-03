package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PersonsService {
    @GET("/persons/personWishlist/{email}")
    Call<ResponseBody> findWishlistByUserEmail(@Path("email") String personEmail);

    @PUT("/persons/likeRecipes")
    Call<MessageDto> likeRecipe(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @PUT("/persons/saveRecipesIngredients")
    Call<MessageDto> saveRecipeIngredients(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);
}
