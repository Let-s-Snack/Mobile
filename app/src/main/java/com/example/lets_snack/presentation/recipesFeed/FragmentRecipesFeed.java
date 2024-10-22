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
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.databinding.FragmentRecipesFeedBinding;
import com.example.lets_snack.databinding.FragmentSearchBinding;
import com.example.lets_snack.presentation.adapter.CategoryAdapter;
import com.example.lets_snack.presentation.adapter.RecipeAdapter;

import java.util.List;

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

        //botão de voltar para a tela anterior
        binding.returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //botão para mostrar as informações da categoria
        binding.infoCategory.setOnClickListener(v -> descriptionModal());

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

        //configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //chamada da API
        PersonsService personsApi = retrofit.create(PersonsService.class);
        Call<List<RecipeDto>> apiCall = personsApi.findWishlistByUserEmail("leticia@gmail.com"); //mudar para pegar o email do user por email quando estiver completo o fluxo de cadastr

        //executar chamada
        apiCall.enqueue(new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(Call<List<RecipeDto>> call, Response<List<RecipeDto>> response) {
                List<RecipeDto> recipes = response.body();
                recyclerView.setAdapter(new RecipeAdapter(recipes));
                loading.setVisibility(View.INVISIBLE);

                if(recipes.isEmpty()) {
                    imageError.setVisibility(View.VISIBLE);
                    imageError.setImageResource(R.drawable.neneca_triste);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Nenhuma receita encontrada!");
                }
            }

            @Override
            public void onFailure(Call<List<RecipeDto>> call, Throwable throwable) {
                //chamar imagem de erro
                loading.setVisibility(View.INVISIBLE);
                imageError.setVisibility(View.VISIBLE);
                imageError.setImageResource(R.drawable.neneca_triste);

                //colocar no textView o erro
                textError.setVisibility(View.VISIBLE);
                textError.setText(throwable.getLocalizedMessage());
            }
        });
    }

    public void categoryRecipesCall(String restrictionId) {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        //configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //chamada da API
        RecipesService recipesApi = retrofit.create(RecipesService.class);
        Call<List<RecipeDto>> apiCall = recipesApi.findRecipesByRestrictions(restrictionId,"leticia@gmail.com");

        //executar chamada
        apiCall.enqueue(new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(Call<List<RecipeDto>> call, Response<List<RecipeDto>> response) {
                List<RecipeDto> recipes = response.body();
                recyclerView.setAdapter(new RecipeAdapter(recipes));
                loading.setVisibility(View.INVISIBLE);

                if(recipes.isEmpty()) {
                    imageError.setVisibility(View.VISIBLE);
                    imageError.setImageResource(R.drawable.neneca_triste);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Nenhuma receita encontrada!");
                }
            }

            @Override
            public void onFailure(Call<List<RecipeDto>> call, Throwable throwable) {
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