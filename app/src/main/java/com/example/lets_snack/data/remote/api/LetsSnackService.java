package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.PersonDto;
import com.example.lets_snack.data.remote.dto.PersonDtoResponse;
import com.example.lets_snack.data.remote.dto.PersonDtoResponseEmail;
import com.example.lets_snack.data.remote.dto.PersonDtoUpdate;
import com.example.lets_snack.data.remote.dto.RestrictionsDto;
import com.example.lets_snack.data.remote.dto.TokenRequestDto;
import com.example.lets_snack.data.remote.dto.TokenResponseDto;
import com.example.lets_snack.data.remote.dto.UserDto;

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

    @GET("administrator/findAdministratorByEmailAndPassword")
    Call<UserDto> findAdministradorByEmailAndPassword(@Header("Authorization") String token, @Query("email") String email, @Query("password") String password);
}
