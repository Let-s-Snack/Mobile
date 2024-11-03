package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.CommentDto;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.data.remote.dto.SendCommentDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecipesService {

    @GET("/recipes/listRecipesByRestriction")
    Call<ResponseBody> findRecipesByRestrictions(@Query("restrictionsId") String restrictionId, @Query("personsEmail") String personsEmail);

    @GET("/recipes/listRecipesById")
    Call<RecipeDto> findRecipeById(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @GET("/recipes/listRecipesByName")
    Call<ResponseBody> findRecipesByName(@Query("recipesName") String recipesName, @Query("personsEmail") String personsEmail);

    @PUT("/recipes/insertComent/{recipesId}")
    Call<MessageDto> insertComment(@Path("recipesId") String recipesId, @Body SendCommentDto comment);
}
