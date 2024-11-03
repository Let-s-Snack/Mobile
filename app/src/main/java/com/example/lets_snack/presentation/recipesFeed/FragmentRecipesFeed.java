package com.example.lets_snack.presentation.recipesFeed;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.lets_snack.data.remote.api.PersonsService;
import com.example.lets_snack.data.remote.api.RecipesService;
import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.databinding.FragmentRecipesFeedBinding;
import com.example.lets_snack.databinding.FragmentSearchBinding;
import com.example.lets_snack.presentation.adapter.CategoryAdapter;
import com.example.lets_snack.presentation.adapter.RecipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentRecipesFeed extends Fragment {

    private FragmentRecipesFeedBinding binding;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ProgressBar loading;
    private ImageView imageError;
    private TextView textError;
    FirebaseAuth autentication = FirebaseAuth.getInstance();
    FirebaseUser user = autentication.getCurrentUser();

    public FragmentRecipesFeed() {
        // Required empty public constructor
    }

    public static FragmentRecipesFeed newInstance() {
        FragmentRecipesFeed fragment = new FragmentRecipesFeed();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecipesFeedBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerRecipes;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false ));

        loading = binding.loadingRecipesFeed;
        loading.setVisibility(View.VISIBLE);

        imageError = binding.imageErrorRecipesFeed;

        textError = binding.textErrorRecipesFeed;

        //verificando se a tela é de curtidas
        if(getArguments().getString("id") != null){
            if(getArguments().getString("id") == "like_screen"){
                binding.returnBtn.setVisibility(View.INVISIBLE);
                binding.infoCategory.setVisibility(View.INVISIBLE);
                TextView titleName = binding.recipesFeedTitle;
                titleName.setText("Curtidas");
                likedScreenCall();
            }else{
                String categoryName = getArguments().getString("category", "Sem nome");
                binding.recipesFeedTitle.setText(categoryName);
                //chamando a API para pegar as receitas por categoria
                categoryRecipesCall(getArguments().getString("id"));
            }
        }

        //botão de voltar para a tela anterior
        binding.returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //botão para mostrar as informações da categoria
        binding.infoCategory.setOnClickListener(v -> descriptionModal());

        if(getArguments().getString("recipeName") != null){
            binding.infoCategory.setVisibility(View.INVISIBLE);
            binding.recipesFeedTitle.setText(getArguments().getString("recipeName"));
            recipesByNameCall(getArguments().getString("recipeName"));
        }

        return binding.getRoot();
    }

    public void descriptionModal() {
        // Usando Dialog
        Dialog descriptionCard = new Dialog(getContext());
        descriptionCard.setContentView(R.layout.description_modal);
        descriptionCard.getWindow().setLayout(WRAP_CONTENT, WRAP_CONTENT);
        descriptionCard.getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.background_dialog)
        );


        //Inicializar os componentes da caixaMsg
        TextView title = descriptionCard.findViewById(R.id.description_title_modal);
        TextView description = descriptionCard.findViewById(R.id.description_text_modal);
        ImageButton closeModal = descriptionCard.findViewById(R.id.description_modal_close);

        title.setText(getArguments().getString("category", "Sem nome"));
        description.setText(getArguments().getString("description", "Sem descrição"));
        closeModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionCard.dismiss();
            }
        });
        descriptionCard.show();
    }

    public void likedScreenCall() {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        // Configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Chamada da API
        PersonsService personsApi = retrofit.create(PersonsService.class);
        Call<ResponseBody> apiCall = personsApi.findWishlistByUserEmail(user.getEmail());

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
                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
                            recyclerView.setAdapter(new RecipeAdapter(recipes));
                            loading.setVisibility(View.INVISIBLE);

                            if (recipes.isEmpty()) {
                                imageError.setVisibility(View.VISIBLE);
                                imageError.setImageResource(R.drawable.neneca_confusa);
                                textError.setVisibility(View.VISIBLE);
                                textError.setText("Nenhuma receita encontrada!");
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

    public void categoryRecipesCall(String restrictionId) {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        // Configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Chamada da API
        RecipesService recipesApi = retrofit.create(RecipesService.class);
        Call<ResponseBody> apiCall = recipesApi.findRecipesByRestrictions(restrictionId,user.getEmail());

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
                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
                            recyclerView.setAdapter(new RecipeAdapter(recipes));
                            loading.setVisibility(View.INVISIBLE);

                            if (recipes.isEmpty()) {
                                imageError.setVisibility(View.VISIBLE);
                                imageError.setImageResource(R.drawable.neneca_confusa);
                                textError.setVisibility(View.VISIBLE);
                                textError.setText("Nenhuma receita encontrada!");
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


    public void recipesByNameCall(String recipeName) {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        //configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //chamada da API
        RecipesService recipesApi = retrofit.create(RecipesService.class);
        Call<ResponseBody> apiCall = recipesApi.findRecipesByName(recipeName,user.getEmail());

        //executar chamada
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
                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
                            recyclerView.setAdapter(new RecipeAdapter(recipes));
                            loading.setVisibility(View.INVISIBLE);

                            if (recipes.isEmpty()) {
                                imageError.setVisibility(View.VISIBLE);
                                imageError.setImageResource(R.drawable.neneca_confusa);
                                textError.setVisibility(View.VISIBLE);
                                textError.setText("Nenhuma receita encontrada!");
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