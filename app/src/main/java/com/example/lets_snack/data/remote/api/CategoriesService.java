package com.example.lets_snack.data.remote.api;

import com.example.lets_snack.data.remote.dto.CategoryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoriesService {

    @GET("/restrictions/listAllRestrictions")
    Call<List<CategoryDto>> findAllCategories();
}
