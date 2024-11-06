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
import retrofit2.Retrofit;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ProgressBar loading;
    private ImageView imageError;
    private TextView textError;
    private RestrictionsRepository restrictionsRepository = new RestrictionsRepository();

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

    public void loadCategories() {

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
                            recyclerView.setAdapter(new CategoryAdapter(categories));
                            loading.setVisibility(View.INVISIBLE);

                            if (categories.isEmpty()) {
                                imageError.setVisibility(View.VISIBLE);
                                imageError.setImageResource(R.drawable.neneca_confusa);
                                textError.setVisibility(View.VISIBLE);
                                textError.setText("Nenhuma categoria encontrada!");
                            }
                        } else {
                            // Caso contrário, parsear como um objeto com mensagem
                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
                            loading.setVisibility(View.INVISIBLE);
                            imageError.setVisibility(View.VISIBLE);
                            imageError.setImageResource(R.drawable.neneca_confusa);
                            textError.setVisibility(View.VISIBLE);
                            textError.setText(messageResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loading.setVisibility(View.INVISIBLE);
                        imageError.setVisibility(View.VISIBLE);
                        imageError.setImageResource(R.drawable.neneca_triste);
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Erro ao processar resposta.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                // Chamar imagem de erro
                loading.setVisibility(View.INVISIBLE);
                imageError.setVisibility(View.VISIBLE);
                imageError.setImageResource(R.drawable.neneca_triste);
                textError.setVisibility(View.VISIBLE);
                textError.setText(throwable.getLocalizedMessage());
            }
        });
    }
}