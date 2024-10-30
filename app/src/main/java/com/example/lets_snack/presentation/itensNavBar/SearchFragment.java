package com.example.lets_snack.presentation.itensNavBar;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Dialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.api.CategoriesService;
import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.databinding.FragmentSearchBinding;
import com.example.lets_snack.presentation.adapter.CategoryAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ProgressBar loading;
    private ImageView imageError;
    private TextView textError;

    public SearchFragment() {
        // Required empty public constructor
    }

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

        loading = binding.loadingCategories;
        loading.setVisibility(View.VISIBLE);

        imageError = binding.imageErrorCategory;

        textError = binding.textErrorCategory;
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
                loading.setVisibility(View.INVISIBLE);

                if(categories.isEmpty()) {
                    imageError.setVisibility(View.VISIBLE);
                    imageError.setImageResource(R.drawable.neneca_triste);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Nenhuma categoria encontrada!");
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable throwable) {
                //chamar imagem de erro
                loading.setVisibility(View.INVISIBLE);
                imageError.setVisibility(View.VISIBLE);
                imageError.setImageResource(R.drawable.neneca_triste);

                //colocar no textView o erro
                textError.setVisibility(View.VISIBLE);
                textError.setText(throwable.getMessage());
            }
        });
    }
}