package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.CheckedUserDto;
import com.example.lets_snack.data.remote.dto.PersonDto;
import com.example.lets_snack.data.remote.dto.PersonDtoResponse;
import com.example.lets_snack.data.remote.dto.PersonDtoResponseEmail;
import com.example.lets_snack.data.remote.dto.PersonDtoUpdate;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.data.remote.dto.RestrictionsDto;
import com.example.lets_snack.data.remote.dto.ShoppingListDto;
import com.example.lets_snack.data.remote.dto.TokenRequestDto;
import com.example.lets_snack.data.remote.dto.TokenResponseDto;
import com.example.lets_snack.data.remote.dto.UserDto;
import com.example.lets_snack.data.remote.dto.SendCommentDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface LetsSnackService {
    @GET("restrictions/listAllRestrictions")
    Call<List<RestrictionsDto>> getRestrictions();

    @POST("persons/insertPerson")
    Call<ResponseBody> insertPerson(@Header("Authorization") String token,@Body PersonDto personDto);

    @GET("persons/listPersonByEmail/{email}")
    Call<PersonDtoResponseEmail> listPersonByEmail(@Path("email") String email);

    @GET("persons/listPersonByUsername/{username}")
    Call<PersonDtoResponse> listPersonByUsername(@Path("username") String username);

    @PUT("persons/updatePerson/{email}")
    Call<PersonDtoResponse> updatePerson(@Header("Authorization") String token, @Path("email") String email, @Body PersonDtoUpdate personDto);

    @POST("auth/login")
    Call<TokenResponseDto> generateToken(@Body TokenRequestDto tokenRequestDto);

    @GET("restrictions/listAllRestrictions")
    Call<ResponseBody> getRestrictions2();

    @GET("recipes/listRecipesByRestriction")
    Call<ResponseBody> findRecipesByRestrictions(@Query("restrictionsId") String restrictionId, @Query("personsEmail") String personsEmail);

    @GET("recipes/listRecipesById")
    Call<RecipeDto> findRecipeById(@Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @GET("recipes/listRecipesByName")
    Call<ResponseBody> findRecipesByName(@Query("recipesName") String recipesName, @Query("personsEmail") String personsEmail);

    @PUT("recipes/insertComent/{recipesId}")
    Call<MessageDto> insertComment(@Header("Authorization") String token, @Header("personsEmail")@Path("recipesId") String recipesId, @Body SendCommentDto comment);

    @GET("persons/personWishlist/{email}")
    Call<ResponseBody> findWishlistByUserEmail(@Path("email") String personEmail);

    @PUT("persons/likeRecipes")
    Call<MessageDto> likeRecipe(@Header("Authorization") String token, @Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @PUT("persons/saveRecipesIngredients")
    Call<MessageDto> saveRecipeIngredients(@Header("Authorization") String token, @Query("recipesId") String recipesId, @Query("personsEmail") String personsEmail);

    @PUT("persons/checkIngredients/{personsEmail}")
    Call<ShoppingListDto> checkIngredients(@Header("Authorization") String token, @Path("personsEmail") String personsEmail, @Body CheckedUserDto checkedUserDto);

    @GET("/persons/personsShoppingList/{email}")
    Call<List<ShoppingListDto>> getShoppingListByUserEmail(@Path("email") String personEmail);


    @GET("persons/personDirectionWeek/{email}")
    Call<RecipeDto> getWeekRecipeByEmail(@Path("email") String personEmail);

    @GET("recipes/personRecommendedRecipes/{email}")
    Call<ResponseBody> getRecommendedRecipesByEmail(@Path("email") String personEmail);

    @GET("recipes/personTrendingRecipes/{email}")
    Call<ResponseBody> getTrendingRecipesByEmail(@Path("email") String personEmail);

    @GET("recipes/personMostCommentedRecipes/{email}")
    Call<ResponseBody> getMostCommentedRecipesByEmail(@Path("email") String personEmail);

    @GET("adm/listAdmByEmail/{email}")
    Call<UserDto> findAdmByEmail(@Header("Authorization") String token,@Path("email") String email);
}
