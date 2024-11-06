package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.data.remote.dto.RestrictionsDto;
import com.example.lets_snack.data.remote.dto.SendCommentDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LetsSnackService {
    @GET("restrictions/listAllRestrictions")
    Call<List<RestrictionsDto>> getRestrictions();

    @GET("restrictions/listAllRestrictions")
    Call<ResponseBody> getRestrictions2();

    @GET("recipes/listRecipesByRestriction")
    Call<ResponseBody> findRecipesByRestrictions(@Query("restrictionsId") String restrictionId, @Query("personsEmail") String personsEmail);

    @GET("recipes/listRecipesById")
    Call<RecipeDto> findRecipeById(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @GET("recipes/listRecipesByName")
    Call<ResponseBody> findRecipesByName(@Query("recipesName") String recipesName, @Query("personsEmail") String personsEmail);

    @PUT("recipes/insertComent/{recipesId}")
    Call<MessageDto> insertComment(@Path("recipesId") String recipesId, @Body SendCommentDto comment);

    @GET("persons/personWishlist/{email}")
    Call<ResponseBody> findWishlistByUserEmail(@Path("email") String personEmail);

    @PUT("persons/likeRecipes")
    Call<MessageDto> likeRecipe(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @PUT("persons/saveRecipesIngredients")
    Call<MessageDto> saveRecipeIngredients(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @GET("persons/personDirectionWeek/{email}")
    Call<RecipeDto> getWeekRecipeByEmail(@Path("email") String personEmail);

    @GET("recipes/personRecommendedRecipes/{email}")
    Call<ResponseBody> getRecommendedRecipesByEmail(@Path("email") String personEmail);

    @GET("recipes/personTrendingRecipes/{email}")
    Call<ResponseBody> getTrendingRecipesByEmail(@Path("email") String personEmail);

    @GET("recipes/personMostCommentedRecipes/{email}")
    Call<ResponseBody> getMostCommentedRecipesByEmail(@Path("email") String personEmail);
}
