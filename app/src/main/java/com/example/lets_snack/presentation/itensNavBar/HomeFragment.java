package com.example.lets_snack.presentation.itensNavBar;

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

import com.example.lets_snack.presentation.MainActivity;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.callbacks.RecipeCallback;
import com.example.lets_snack.data.remote.callbacks.RecipesCallback;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository;
import com.example.lets_snack.data.remote.repository.rest.RecipesRepository;
import com.example.lets_snack.databinding.FragmentHomeBinding;
import com.example.lets_snack.presentation.adapter.RecipeHorizontalAdapter;
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
    private PersonsRepository personsRepository = null;
    private RecipesRepository recipesRepository = null;

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
        recipesRepository = new RecipesRepository(requireContext());
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        personsRepository = new PersonsRepository(requireContext());
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
        weekRecipeCall(new RecipeCallback() {
            @Override
            public void onSuccess(RecipeDto recipes) {
                Log.d("WeekRecipeCall", "Receitas da semana carregadas: " + recipes.getId());
                binding.weekRecipeCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", recipes.getId());
                        Log.d("WeekRecipeCall", "Receitas da semana carregadas: " + recipes.getId());

                        // Chama o fragment de recipe feed
                        FragmentTransaction transaction = ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
                        FragmentRecipe fragmentRecipe = new FragmentRecipe();
                        fragmentRecipe.setArguments(bundle);
                        transaction.replace(R.id.mainContainer, fragmentRecipe);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }

            @Override
            public void onMessage(String message) {
                Log.d("WeekRecipeCall", "Receitas da semana carregadas: " + message);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("WeekRecipeCall", "Receitas da semana carregadas: " + throwable.getMessage());
            }
        });
        recipesMostCommentedCall(new RecipesCallback() {
            @Override
            public void onSuccess(List<RecipeDto> recipes) {
                // Manipule o sucesso, por exemplo, atualizando a interface do usuário
            }

            @Override
            public void onMessage(String message) {
                // Manipule a mensagem do servidor
                Log.d("Message from server", message);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Manipule o erro, exiba uma mensagem, etc.
                Log.e("Callback Error", throwable.getMessage());
            }
        });

        recipesRecommendedCall(new RecipesCallback() {
            @Override
            public void onSuccess(List<RecipeDto> recipes) {
                // Lógica para tratar a lista de receitas recomendadas
            }

            @Override
            public void onMessage(String message) {
                // Lógica para tratar a mensagem recebida do servidor
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Lógica para tratar o erro
            }
        });

        recipesTrendingCall(new RecipesCallback() {
            @Override
            public void onSuccess(List<RecipeDto> recipes) {
                // Lógica para tratar a lista de receitas recomendadas
            }

            @Override
            public void onMessage(String message) {
                // Lógica para tratar a mensagem recebida do servidor
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Lógica para tratar o erro
            }
        });


        return binding.getRoot();
    }

    public void weekRecipeCall(RecipeCallback callback) {
        Call<RecipeDto> apiCall = personsRepository.getWeekRecipeByEmail(user.getEmail());

        // Executar chamada
        apiCall.enqueue(new Callback<RecipeDto>() {
            @Override
            public void onResponse(Call<RecipeDto> call, Response<RecipeDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecipeDto recipes = response.body();

                    // Notifica o sucesso através do callback
                    callback.onSuccess(recipes);

                    // Atualiza a UI com os dados recebidos
                    binding.weekRecipeName.setText(recipes.getName());
                    Picasso.get()
                            .load(recipes.getUrlPhoto())
                            .resize(300, 300)
                            .centerCrop() // Centraliza e corta a imagem
                            .placeholder(R.drawable.profile_default)
                            .transform(new RoundedTransformation(180, 0) )
                            .into(binding.weekRecipePhoto);

                    binding.weekRecipeDescription.setText(recipes.getDescription());
                } else {
                    // Notifica o erro pelo callback
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<RecipeDto> call, Throwable t) {
                Log.d("Error", t.getMessage());
                // Notifica a falha pelo callback
                callback.onFailure(t);
            }
        });
    }




    public void recipesMostCommentedCall(RecipesCallback callback) {
        Call<ResponseBody> apiCall = recipesRepository.getMostCommentedRecipesByEmail(user.getEmail());

        apiCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Gson gson = new Gson();
                        String responseBodyString = response.body().string();

                        // Verifica se a resposta é uma lista de receitas
                        if (responseBodyString.startsWith("[")) {
                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
                            recyclerViewMoreCommentsRecipes.setAdapter(new RecipeHorizontalAdapter(recipes));

                            if (recipes.isEmpty()) {
                                binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
                                binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_sentada);
                                binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
                                binding.moreCommentsRecipesText.setText("Nenhuma receita encontrada!");
                            }

                            // Chama o callback de sucesso
                            callback.onSuccess(recipes);
                        } else {
                            // Caso contrário, parsear como um objeto de mensagem
                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
                            binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
                            binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_sentada);
                            binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
                            binding.moreCommentsRecipesText.setText(messageResponse.getMessage());

                            // Chama o callback de mensagem
                            callback.onMessage(messageResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
                        binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_triste);
                        binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
                        binding.moreCommentsRecipesText.setText("Erro ao processar resposta.");

                        // Chama o callback de falha
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                binding.moreCommentsRecipesImg.setVisibility(View.VISIBLE);
                binding.moreCommentsRecipesImg.setImageResource(R.drawable.neneca_triste);
                binding.moreCommentsRecipesText.setVisibility(View.VISIBLE);
                binding.moreCommentsRecipesText.setText(throwable.getLocalizedMessage());

                // Chama o callback de falha
                callback.onFailure(throwable);
            }
        });
    }


    public void recipesRecommendedCall(RecipesCallback callback) {
        Call<ResponseBody> apiCall = recipesRepository.getRecommendedRecipesByEmail(user.getEmail());

        apiCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Gson gson = new Gson();
                        String responseBodyString = response.body().string();

                        // Verifica se a resposta é uma lista de receitas
                        if (responseBodyString.startsWith("[")) {
                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
                            recyclerViewRecommendedRecipes.setAdapter(new RecipeHorizontalAdapter(recipes));

                            if (recipes.isEmpty()) {
                                binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
                                binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_sentada);
                                binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
                                binding.recommendedRecipesErrorText.setText("Nenhuma receita encontrada!");
                            }

                            // Chama o callback de sucesso
                            callback.onSuccess(recipes);
                        } else {
                            // Caso contrário, parsear como um objeto de mensagem
                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
                            binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
                            binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_sentada);
                            binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
                            binding.recommendedRecipesErrorText.setText(messageResponse.getMessage());

                            // Chama o callback de mensagem
                            callback.onMessage(messageResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
                        binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_triste);
                        binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
                        binding.recommendedRecipesErrorText.setText("Erro ao processar resposta.");

                        // Chama o callback de falha
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                binding.recommendedRecipesErrorImg.setVisibility(View.VISIBLE);
                binding.recommendedRecipesErrorImg.setImageResource(R.drawable.neneca_triste);
                binding.recommendedRecipesErrorText.setVisibility(View.VISIBLE);
                binding.recommendedRecipesErrorText.setText(throwable.getLocalizedMessage());

                // Chama o callback de falha
                callback.onFailure(throwable);
            }
        });
    }

    public void recipesTrendingCall(RecipesCallback callback) {
        Call<ResponseBody> apiCall = recipesRepository.getTrendingRecipesByEmail(user.getEmail());

        // Executar chamada
        apiCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Gson gson = new Gson();
                        String responseBodyString = response.body().string();

                        // Verifica se a resposta é um array de receitas
                        if (responseBodyString.startsWith("[")) {
                            List<RecipeDto> recipes = Arrays.asList(gson.fromJson(responseBodyString, RecipeDto[].class));
                            recyclerViewTrendingRecipes.setAdapter(new RecipeHorizontalAdapter(recipes));

                            if (recipes.isEmpty()) {
                                binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                                binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_sentada);
                                binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                                binding.trendingRecipeErrorText.setText("Nenhuma receita encontrada!");
                            }

                            // Chama o callback de sucesso com a lista de receitas
                            callback.onSuccess(recipes);
                        } else {
                            // Caso contrário, parsear como objeto de mensagem
                            MessageDto messageResponse = gson.fromJson(responseBodyString, MessageDto.class);
                            binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                            binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_confusa);
                            binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                            binding.trendingRecipeErrorText.setText(messageResponse.getMessage());

                            // Chama o callback com a mensagem
                            callback.onMessage(messageResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                        binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_triste);
                        binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                        binding.trendingRecipeErrorText.setText("Erro ao processar resposta.");

                        // Chama o callback de falha
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                binding.trendingRecipeErrorImg.setVisibility(View.VISIBLE);
                binding.trendingRecipeErrorImg.setImageResource(R.drawable.neneca_triste);
                binding.trendingRecipeErrorText.setVisibility(View.VISIBLE);
                binding.trendingRecipeErrorText.setText(throwable.getLocalizedMessage());

                // Chama o callback de falha
                callback.onFailure(throwable);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
