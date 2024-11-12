package com.example.lets_snack.presentation.itensNavBar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lets_snack.data.remote.repository.rest.CategoriesRepository;
import com.example.lets_snack.data.remote.repository.rest.RecipesRepository;
import com.example.lets_snack.presentation.MainActivity;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.repository.rest.RestrictionsRepository;
import com.example.lets_snack.databinding.FragmentSearchBinding;
import com.example.lets_snack.presentation.adapter.CategoryAdapter;
import com.example.lets_snack.presentation.recipesFeed.FragmentRecipesFeed;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecyclerView recyclerViewRestrictions;
    private RecyclerView recyclerViewCategories;
    private ProgressBar loadingRestrictions;
    private ProgressBar loadingCategories;
    private ImageView imageErrorRestrictions;
    private ImageView imageErrorCategories;
    private TextView textErrorRestrictions;
    private TextView textErrorCategories;
    private RestrictionsRepository restrictionsRepository = new RestrictionsRepository();

    private CategoriesRepository categoriesRepository = null;

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
        categoriesRepository = new CategoriesRepository(requireContext());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //fazendo logout
        recyclerViewRestrictions = binding.recyclerCategories;
        recyclerViewRestrictions.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false ));
        //chamar api para co    locar as categorias
        loadRestrictions();

        recyclerViewCategories = binding.recyclerPartners;
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false ));
        loadCategories();

        loadingCategories = binding.loadingCategories;
        loadingCategories.setVisibility(View.VISIBLE);

        loadingRestrictions = binding.loadingRestrictions;
        loadingRestrictions.setVisibility(View.VISIBLE);

        imageErrorRestrictions = binding.imageErrorRestrictions;
        imageErrorCategories = binding.imageErrorCategories;

        textErrorRestrictions = binding.textErrorRestrictions;
        textErrorCategories = binding.textErrorRestrictions;

        binding.searchInputText.setOnEditorActionListener((v, actionId, event) -> {
            // Captura o texto inserido e inicia a ação de busca
            if(binding.searchInputText.getText().toString().isEmpty()) {
                return false;
            }
            Bundle bundle = new Bundle();
            bundle.putString("recipeName", binding.searchInputText.getText().toString());

            //chamando fragment de recipe feed
            FragmentTransaction transaction = ((MainActivity) binding.getRoot().getContext()).getSupportFragmentManager().beginTransaction();
            FragmentRecipesFeed fragmentRecipesFeed = new FragmentRecipesFeed();
            fragmentRecipesFeed.setArguments(bundle);
            transaction.replace(R.id.mainContainer, fragmentRecipesFeed);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void loadRestrictions() {

        // Chamada da API
        Call<ResponseBody> apiCall = restrictionsRepository.getRestrictions2();

        // Executar chamada
        apiCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Analisar a resposta com Gson
                        Gson gson = new Gson();
                        String responseBodyString = response.body().string();

                        // Tentar parsear como um array
                        if (responseBodyString.startsWith("[")) {
                            List<CategoryDto> categories = Arrays.asList(gson.fromJson(responseBodyString, CategoryDto[].class));
                            recyclerViewRestrictions.setAdapter(new CategoryAdapter(categories));
                            loadingRestrictions.setVisibility(View.INVISIBLE);

                            if (categories.isEmpty()) {
                                imageErrorRestrictions.setVisibility(View.VISIBLE);
                                imageErrorRestrictions.setImageResource(R.drawable.neneca_confusa);
                                textErrorRestrictions.setVisibility(View.VISIBLE);
                                textErrorRestrictions.setText("Nenhuma categoria encontrada!");
                            }
                        } else {
                            // Caso contrário, parsear como um objeto com mensagem
                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
                            loadingRestrictions.setVisibility(View.INVISIBLE);
                            imageErrorRestrictions.setVisibility(View.VISIBLE);
                            imageErrorRestrictions.setImageResource(R.drawable.neneca_confusa);
                            textErrorRestrictions.setVisibility(View.VISIBLE);
                            textErrorRestrictions.setText(messageResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        textErrorRestrictions.setVisibility(View.INVISIBLE);
                        imageErrorRestrictions.setVisibility(View.VISIBLE);
                        imageErrorRestrictions.setImageResource(R.drawable.neneca_triste);
                        textErrorRestrictions.setVisibility(View.VISIBLE);
                        textErrorRestrictions.setText("Erro ao processar resposta.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                // Chamar imagem de erro
                textErrorRestrictions.setVisibility(View.INVISIBLE);
                imageErrorRestrictions.setVisibility(View.VISIBLE);
                imageErrorRestrictions.setImageResource(R.drawable.neneca_triste);
                textErrorRestrictions.setVisibility(View.VISIBLE);
                textErrorRestrictions.setText(throwable.getLocalizedMessage());
            }
        });
    }

    public void loadCategories() {

        // Chamada da API
        Call<ResponseBody> apiCall = categoriesRepository.getCategories();

        // Executar chamada
        apiCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Analisar a resposta com Gson
                        Gson gson = new Gson();
                        String responseBodyString = response.body().string();

                        // Tentar parsear como um array
                        if (responseBodyString.startsWith("[")) {
                            List<CategoryDto> categories = Arrays.asList(gson.fromJson(responseBodyString, CategoryDto[].class));
                            recyclerViewCategories.setAdapter(new CategoryAdapter(categories));
                            loadingCategories.setVisibility(View.INVISIBLE);

                            if (categories.isEmpty()) {
                                imageErrorCategories.setVisibility(View.VISIBLE);
                                imageErrorCategories.setImageResource(R.drawable.neneca_confusa);
                                textErrorCategories.setVisibility(View.VISIBLE);
                                textErrorCategories.setText("Nenhuma categoria encontrada!");
                            }
                        } else {
                            // Caso contrário, parsear como um objeto com mensagem
                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
                            loadingCategories.setVisibility(View.INVISIBLE);
                            imageErrorCategories.setVisibility(View.VISIBLE);
                            imageErrorCategories.setImageResource(R.drawable.neneca_confusa);
                            textErrorCategories.setVisibility(View.VISIBLE);
                            textErrorCategories.setText(messageResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingCategories.setVisibility(View.INVISIBLE);
                        imageErrorCategories.setVisibility(View.VISIBLE);
                        imageErrorCategories.setImageResource(R.drawable.neneca_triste);
                        textErrorCategories.setVisibility(View.VISIBLE);
                        textErrorCategories.setText("Erro ao processar resposta.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                // Chamar imagem de erro
                loadingCategories.setVisibility(View.INVISIBLE);
                imageErrorCategories.setVisibility(View.VISIBLE);
                imageErrorCategories.setImageResource(R.drawable.neneca_triste);
                textErrorCategories.setVisibility(View.VISIBLE);
                textErrorCategories.setText(throwable.getLocalizedMessage());
            }
        });
    }
}