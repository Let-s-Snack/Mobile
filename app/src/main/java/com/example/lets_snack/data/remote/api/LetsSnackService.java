package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.PersonDto;
import com.example.lets_snack.data.remote.dto.PersonDtoResponse;
import com.example.lets_snack.data.remote.dto.PersonDtoResponseEmail;
import com.example.lets_snack.data.remote.dto.RestrictionsDto;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LetsSnackService {
    @GET("restrictions/listAllRestrictions")
    Call<List<RestrictionsDto>> getRestrictions();

    @POST("persons/insertPerson")
    Call<ResponseBody> insertPerson(@Body PersonDto personDto);

    @GET("/persons/listPersonByEmail/{email}")
    Call<PersonDtoResponseEmail> listPersonByEmail(@Path("email") String email);

    @GET("/persons/listPersonByUsername/{username}")
    Call<PersonDtoResponse> listPersonByUsername(@Path("username") String username);
}
