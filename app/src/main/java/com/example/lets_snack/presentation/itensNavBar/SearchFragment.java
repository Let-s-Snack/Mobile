package com.example.lets_snack.presentation.itensNavBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.api.CategoriesService;
import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.databinding.FragmentProfileBinding;
import com.example.lets_snack.databinding.FragmentSearchBinding;
import com.example.lets_snack.presentation.adapter.CategoryAdapter;
import com.example.lets_snack.presentation.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecyclerView recyclerView;
    private Retrofit retrofit;

    public SearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //fazendo logout
        recyclerView = binding.recyclerCategories;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false ));
        //chamar api para co    locar as categorias
        loadCategories();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void loadCategories() {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        //configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //chamada da API
        CategoriesService categoriesApi = retrofit.create(CategoriesService.class);
        Call<List<CategoryDto>> apiCall = categoriesApi.findAllCategories();

        //executar chamada
        apiCall.enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                List<CategoryDto> categories = response.body();
                recyclerView.setAdapter(new CategoryAdapter(categories));
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable throwable) {
                //chamar modal de erro com o servidor
            }
        });
    }
}