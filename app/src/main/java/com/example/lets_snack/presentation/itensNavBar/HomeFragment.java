package com.example.lets_snack.presentation.itensNavBar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lets_snack.MainActivity;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.dto.CommentDto;
import com.example.lets_snack.data.remote.dto.IngredientDto;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository;
import com.example.lets_snack.data.remote.repository.rest.RecipesRepository;
import com.example.lets_snack.databinding.FragmentHomeBinding;
import com.example.lets_snack.presentation.adapter.CommentAdapter;
import com.example.lets_snack.presentation.adapter.IngredientsAdapter;
import com.example.lets_snack.presentation.adapter.RecipeAdapter;
import com.example.lets_snack.presentation.adapter.RecipeHorizontalAdapter;
import com.example.lets_snack.presentation.adapter.StepAdapter;
import com.example.lets_snack.presentation.recipe.FragmentRecipe;
import com.example.lets_snack.presentation.transform.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth autentication = FirebaseAuth.getInstance();
    private FirebaseUser user = autentication.getCurrentUser();
    private RecyclerView recyclerViewTrendingRecipes;
    private RecyclerView recyclerViewRecommendedRecipes;
    private RecyclerView recyclerViewMoreCommentsRecipes;
    private PersonsRepository personsRepository = new PersonsRepository();
    private RecipesRepository recipesRepository = new RecipesRepository();

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        recyclerViewTrendingRecipes = binding.recyclerTrendingRecipes;
        recyclerViewTrendingRecipes.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false ));

        recyclerViewRecommendedRecipes = binding.recyclerRecommendedRecipes;
        recyclerViewRecommendedRecipes.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false ));


        recyclerViewMoreCommentsRecipes = binding.recyclerMoreCommentsRecipes;
        recyclerViewMoreCommentsRecipes.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false ));

        if (user != null) {
            binding.nameUser.setText("Olá, "+ user.getDisplayName());
            if (user.getPhotoUrl() != null) {

                Picasso.get()
                        .load(user.getPhotoUrl())
                        .resize(300, 300)
                        .centerCrop() // Centraliza e corta a imagem
                        .placeholder(R.drawable.profile_default)
                        .transform(new RoundedTransformation(180, 0) )
                        .into(binding.imageUser);
            } else {
                binding.imageUser.setImageResource(R.drawable.profile_default);
            }
        }

        weekRecipeCall();
        recipesTrendingCall();
//        recipesRecommendedCall();
//        recipesMostCommentedCall();

        return binding.getRoot();
    }

    public void weekRecipeCall() {
        Call<RecipeDto> apiCall = personsRepository.getWeekRecipeByEmail(user.getEmail());

        // Executar chamada
        apiCall.enqueue(new Callback<RecipeDto>() {
            @Override
            public void onResponse(Call<RecipeDto> call, Response<RecipeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecipeDto recipes = response.body();
                    binding.weekRecipeName.setText(recipes.getName());
                    Glide.with(getContext())
                            .load(recipes.getUrlPhoto()).centerCrop().into(binding.weekRecipePhoto);
                    binding.weekRecipeDescription.setText(recipes.getDescription());
                    binding.weekRecipeCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", recipes.getId());

                            //chamando fragment de recipe feed
                            FragmentTransaction transaction = ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
                            FragmentRecipe fragmentRecipe = new FragmentRecipe();
                            fragmentRecipe.setArguments(bundle);
                            transaction.replace(R.id.mainContainer, fragmentRecipe);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RecipeDto> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    public void recipesTrendingCall() {
        Call<ResponseBody> apiCall = recipesRepository.getTrendingRecipesByEmail(user.getEmail());

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
                            recyclerViewTrendingRecipes.setAdapter(new RecipeHorizontalAdapter(recipes));
//                            loading.setVisibility(View.INVISIBLE);

                            if (recipes.isEmpty()) {
                                binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                                binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_sentada);
                                binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                                binding.trendingRecipeErrorText.setText("Nenhuma receita encontrada!");
                            }
                        } else {
                            // Caso contrário, parsear como um objeto com mensagem
                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
//                            loading.setVisibility(View.INVISIBLE);
                            binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                            binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_confusa);
                            binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                            binding.trendingRecipeErrorText.setText(messageResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        loading.setVisibility(View.INVISIBLE);
                        binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                        binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_triste);
                        binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                        binding.trendingRecipeErrorText.setText("Erro ao processar resposta.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                // Chamar imagem de erro
//                loading.setVisibility(View.INVISIBLE);
                binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_triste);
                binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                binding.trendingRecipeErrorText.setText(throwable.getLocalizedMessage());
            }
        });
    }
//
//    public void recipesRecommendedCall() {
//        Call<ResponseBody> apiCall = recipesRepository.getRecommendedRecipesByEmail(user.getEmail());
//
//        // Executar chamada
//        apiCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        // Analisar a resposta com Gson
//                        Gson gson = new Gson();
//                        String responseBodyString = response.body().string();
//
//                        // Tentar parsear como um array
//                        if (responseBodyString.startsWith("[")) {
//                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
//                            recyclerViewTrendingRecipes.setAdapter(new RecipeHorizontalAdapter(recipes));
////                            loading.setVisibility(View.INVISIBLE);
//
//                            if (recipes.isEmpty()) {
//                                binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
//                                binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_sentada);
//                                binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
//                                binding.recommendedRecipesErrorText.setText("Nenhuma receita encontrada!");
//                            }
//                        } else {
//                            // Caso contrário, parsear como um objeto com mensagem
//                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
////                            loading.setVisibility(View.INVISIBLE);
//                            binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
//                            binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_sentada);
//                            binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
//                            binding.recommendedRecipesErrorText.setText(messageResponse.getMessage());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
////                        loading.setVisibility(View.INVISIBLE);
//                        binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
//                        binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_triste);
//                        binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
//                        binding.recommendedRecipesErrorText.setText("Erro ao processar resposta.");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//                // Chamar imagem de erro
////                loading.setVisibility(View.INVISIBLE);
//                binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
//                binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_triste);
//                binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
//                binding.recommendedRecipesErrorText.setText(throwable.getLocalizedMessage());
//            }
//        });
//    }
//
//    public void recipesMostCommentedCall() {
//        Call<ResponseBody> apiCall = recipesRepository.getMostCommentedRecipesByEmail(user.getEmail());
//
//        // Executar chamada
//        apiCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        // Analisar a resposta com Gson
//                        Gson gson = new Gson();
//                        String responseBodyString = response.body().string();
//
//                        // Tentar parsear como um array
//                        if (responseBodyString.startsWith("[")) {
//                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
//                            recyclerViewTrendingRecipes.setAdapter(new RecipeHorizontalAdapter(recipes));
////                            loading.setVisibility(View.INVISIBLE);
//
//                            if (recipes.isEmpty()) {
//                                binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
//                                binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_sentada);
//                                binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
//                                binding.moreCommentsRecipesText.setText("Nenhuma receita encontrada!");
//                            }
//                        } else {
//                            // Caso contrário, parsear como um objeto com mensagem
//                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
////                            loading.setVisibility(View.INVISIBLE);
//                            binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
//                            binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_sentada);
//                            binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
//                            binding.moreCommentsRecipesText.setText(messageResponse.getMessage());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
////                        loading.setVisibility(View.INVISIBLE);
//                        binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
//                        binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_triste);
//                        binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
//                        binding.moreCommentsRecipesText.setText("Erro ao processar resposta.");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//                // Chamar imagem de erro
////                loading.setVisibility(View.INVISIBLE);
//                binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
//                binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_triste);
//                binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
//                binding.moreCommentsRecipesText.setText(throwable.getLocalizedMessage());
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
