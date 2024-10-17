package com.example.lets_snack.presentation.recipe;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Dialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.api.CategoriesService;
import com.example.lets_snack.data.remote.api.RecipesService;
import com.example.lets_snack.data.remote.dto.CategoryDto;
import com.example.lets_snack.data.remote.dto.CommentDto;
import com.example.lets_snack.data.remote.dto.FindRecipeByIdDto;
import com.example.lets_snack.data.remote.dto.IngredientDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.data.remote.dto.SendCommentDto;
import com.example.lets_snack.data.remote.dto.StepDto;
import com.example.lets_snack.databinding.FragmentRecipeBinding;
import com.example.lets_snack.databinding.FragmentRecipesFeedBinding;
import com.example.lets_snack.presentation.adapter.CategoryAdapter;
import com.example.lets_snack.presentation.adapter.CommentAdapter;
import com.example.lets_snack.presentation.adapter.IngredientsAdapter;
import com.example.lets_snack.presentation.adapter.RecipeAdapter;
import com.example.lets_snack.presentation.adapter.StepAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentRecipe extends Fragment {

    private FragmentRecipeBinding binding;
    private RecyclerView recyclerViewIngredients;
    private RecyclerView recyclerViewSteps;
    private RecyclerView recyclerViewComments;
    private boolean verifyComment = false;

    private Retrofit retrofit;
    public FragmentRecipe() {
        // Required empty public constructor
    }

    public static FragmentRecipe newInstance() {
        FragmentRecipe fragment = new FragmentRecipe();
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
        recyclerViewIngredients = binding.recipeIngredientsRecycle;
        recyclerViewIngredients.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false ));
        recyclerViewIngredients.setNestedScrollingEnabled(false);

        recyclerViewSteps = binding.recipeStepsRecycle;
        recyclerViewSteps.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false ));
        recyclerViewSteps.setNestedScrollingEnabled(false);

        recyclerViewComments = binding.recipeComentsRecycle;
        recyclerViewComments.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false ));

        loadRecipe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);

        binding.recipeReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        return binding.getRoot();
    }

    public void loadRecipe() {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        // Configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Chamada da API
        RecipesService recipesApi = retrofit.create(RecipesService.class);

        Call<RecipeDto> apiCall = recipesApi.findRecipeById("6711242585b86ea5e7e4d30b", "leticia@gmail.com");
        apiCall.enqueue(new Callback<RecipeDto>() {
            @Override
            public void onResponse(Call<RecipeDto> call, Response<RecipeDto> response) {
                RecipeDto recipes = response.body();
                //carregar os dados
                binding.recipeScreenName.setText(recipes.getName());
                Glide.with(getContext())
                        .load(recipes.getUrlPhoto()).centerCrop().into(binding.recipeScreenImage);
                binding.recipeScreenRatingbar.setRating(Float.parseFloat(String.valueOf(recipes.getRating())));
                binding.recipeDescription.setText(recipes.getDescription());
                binding.recipeBtnEvaluate.setOnClickListener(v -> evaluationModal());


                List<String> steps = recipes.getPreparationMethods();
                if(steps != null && steps.size() > 0) {
                    recyclerViewSteps.setAdapter(new StepAdapter(steps));
                }

                List<CommentDto> comments = recipes.getComents();
                if(comments != null && comments.size() > 0) {
                    binding.recipeRateText.setText(String.valueOf(recipes.getRating()) + "/5" + " (" + comments.size() + " avaliações)");
                    recyclerViewComments.setAdapter(new CommentAdapter(comments));
                }
                else{
                    binding.recipeRateText.setText("0/5" + " (0 avaliações)");
                }

                List<IngredientDto> ingredients = recipes.getIngredients();
                if (ingredients != null && ingredients.size() > 0) {
                    recyclerViewIngredients.setAdapter(new IngredientsAdapter(ingredients));
                }

            }

            @Override
            public void onFailure(Call<RecipeDto> call, Throwable throwable) {
                System.out.println("Ocorreu um erro no servidor: " + throwable.getMessage());
            }
        });
    }

    public void evaluationModal() {
        // Usando Dialog
        Dialog evaluationCard = new Dialog(getContext());
        evaluationCard.setContentView(R.layout.rating_modal);
        evaluationCard.getWindow().setLayout(WRAP_CONTENT, WRAP_CONTENT);
        evaluationCard.getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.background_dialog)
        );


        //Inicializar os componentes da caixaMsg
        RatingBar rate = evaluationCard.findViewById(R.id.rating_bar_modal);
        EditText commentDescription = evaluationCard.findViewById(R.id.rating_text_modal);
        ImageButton closeModal = evaluationCard.findViewById(R.id.rating_modal_close);
        Button submit = evaluationCard.findViewById(R.id.rating_btn_modal);

        if(verifyComment) { evaluationCard.dismiss(); }

        rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                submit.setEnabled(rate.getRating() > 0);
                int integerRating = Math.round(rating);
                ratingBar.setRating(integerRating);
            }
        });

        closeModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluationCard.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //envio da avaliação
                sendComment(Math.round(rate.getRating()), commentDescription.getText().toString());
            }
        });

        evaluationCard.show();
    }

    public void sendComment(int rate, String commentDescription) {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        // Configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Chamada da API
        RecipesService recipesApi = retrofit.create(RecipesService.class);

        SendCommentDto comment = new SendCommentDto(rate, commentDescription);

        Call<Object> apiCall = recipesApi.insertComent("670eb19c9a2fbfeda8b1d417", comment);
        apiCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Object recipes = response.body();
                Toast.makeText(getContext(), "Enviadooooo", Toast.LENGTH_SHORT).show();
                verifyComment = true;
            }

            @Override
            public void onFailure(Call<Object> call, Throwable throwable) {
                System.out.println("Ocorreu um erro no servidor: " + throwable.getMessage());
                Toast.makeText(getContext(), "erroooooooooo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}