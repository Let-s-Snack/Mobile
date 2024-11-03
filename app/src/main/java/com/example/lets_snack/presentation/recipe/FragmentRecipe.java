package com.example.lets_snack.presentation.recipe;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Dialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lets_snack.R;
import com.example.lets_snack.data.remote.api.PersonsService;
import com.example.lets_snack.data.remote.api.RecipesService;
import com.example.lets_snack.data.remote.dto.CommentDto;
import com.example.lets_snack.data.remote.dto.IngredientDto;
import com.example.lets_snack.data.remote.dto.MessageDto;
import com.example.lets_snack.data.remote.dto.RecipeDto;
import com.example.lets_snack.data.remote.dto.SendCommentDto;
import com.example.lets_snack.databinding.FragmentRecipeBinding;
import com.example.lets_snack.presentation.adapter.CommentAdapter;
import com.example.lets_snack.presentation.adapter.IngredientsAdapter;
import com.example.lets_snack.presentation.adapter.StepAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.StringReader;
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
    private Handler handler = new Handler();
    private Runnable apiCallRunnable;
    private static final int DELAY_MILLIS = 1000; // 1 segundos de atraso
    private ScrollView scrollView;
    private ProgressBar loading;
    private View whiteOverlayScreen;
    private ImageView imageError;
    private TextView textError;
    FirebaseAuth autentication = FirebaseAuth.getInstance();
    FirebaseUser user = autentication.getCurrentUser();

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
        //inicializando recyclers
        recyclerViewIngredients = binding.recipeIngredientsRecycle;
        recyclerViewIngredients.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false ));
        recyclerViewIngredients.setNestedScrollingEnabled(false);

        recyclerViewSteps = binding.recipeStepsRecycle;
        recyclerViewSteps.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false ));
        recyclerViewSteps.setNestedScrollingEnabled(false);

        recyclerViewComments = binding.recipeComentsRecycle;
        recyclerViewComments.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false ));

        scrollView = binding.recipeScrollview;

        loading = binding.loadingRecipe;
        whiteOverlayScreen = binding.overlayWhiteScreen;

        imageError = binding.imageErrorRecipe;
        textError = binding.textErrorRecipe;

        //definindo camada de visualização
        whiteOverlayScreen.setTranslationZ(10);
        loading.setTranslationZ(11);
        binding.recipeReturnBtn.setTranslationZ(11);
        imageError.setTranslationZ(11);
        textError.setTranslationZ(11);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //criando binding
        binding = FragmentRecipeBinding.inflate(inflater, container, false);

        //setando botão de voltar
        binding.recipeReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //verificando se o id da receita foi passado
        if(getArguments().getString("id") != null){
            //chamando api para pegar receita
            loadRecipe(getArguments().getString("id"));
        }

        return binding.getRoot();
    }

    //carregando receita
    public void loadRecipe(String recipeId) {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        // Configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Chamada da API
        RecipesService recipesApi = retrofit.create(RecipesService.class);

        Call<RecipeDto> apiCall = recipesApi.findRecipeById(recipeId, user.getEmail());
        apiCall.enqueue(new Callback<RecipeDto>() {
            @Override
            public void onResponse(Call<RecipeDto> call, Response<RecipeDto> response) {
                RecipeDto recipes = response.body();
                whiteOverlayScreen.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);
                scrollView.setSmoothScrollingEnabled(true);
                //carregar os dados
                binding.recipeScreenName.setText(recipes.getName());
                Glide.with(getContext())
                        .load(recipes.getUrlPhoto()).centerCrop().into(binding.recipeScreenImage);
                if(recipes.getRating() != null) {
                    binding.recipeScreenRatingbar.setRating(recipes.getRating());
                }
                binding.recipeScreenLikeButton.setChecked(recipes.getIsFavorite());
                binding.recipeDescription.setText(recipes.getDescription());
                binding.recipeBtnEvaluate.setOnClickListener(v -> evaluationModal());

                binding.recipeScreenLikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //cancela qualquer chamada de API agendada anterior
                        if (apiCallRunnable != null) {
                            handler.removeCallbacks(apiCallRunnable);
                        }

                        //define a nova tarefa para ser executada após 5 segundos
                        apiCallRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if(recipes.getIsFavorite() != binding.recipeScreenLikeButton.isChecked()) {
                                    likeRecipe();
                                }
                            }
                        };

                        // Agenda a tarefa após o atraso definido
                        handler.postDelayed(apiCallRunnable, DELAY_MILLIS);
                    }
                });

                binding.recipeBtnSaveIngredients.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveRecipeIngredients();
                    }
                });

                List<String> steps = recipes.getPreparationMethods();
                if(steps != null && steps.size() > 0) {
                    recyclerViewSteps.setAdapter(new StepAdapter(steps));
                }

                List<CommentDto> comments = recipes.getComents();
                if(comments != null && comments.size() > 0) {
                    binding.recipeRateText.setText(String.format("%.1f", recipes.getRating()) + "/5" + " (" + comments.size() + " avaliações)");
                    recyclerViewComments.setAdapter(new CommentAdapter(comments));
                    binding.imageEmptyComments.setVisibility(View.INVISIBLE);
                    binding.textEmptyComment.setVisibility(View.INVISIBLE);
                }
                else{
                    binding.recipeRateText.setText("0/5" + " (0 avaliações)");
                    binding.imageEmptyComments.setVisibility(View.VISIBLE);
                    binding.textEmptyComment.setVisibility(View.VISIBLE);
                }

                List<IngredientDto> ingredients = recipes.getIngredients();
                if (ingredients != null && ingredients.size() > 0) {
                    recyclerViewIngredients.setAdapter(new IngredientsAdapter(ingredients));
                }

            }

            @Override
            public void onFailure(Call<RecipeDto> call, Throwable throwable) {
                loading.setVisibility(View.INVISIBLE);
                imageError.setVisibility(View.VISIBLE);
                imageError.setImageResource(R.drawable.neneca_triste);
                textError.setVisibility(View.VISIBLE);
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
                evaluationCard.dismiss();
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

        SendCommentDto comment = new SendCommentDto(user.getEmail(), rate, commentDescription);

        Call<MessageDto> apiCall = recipesApi.insertComment(getArguments().getString("id"), comment);
        apiCall.enqueue(new Callback<MessageDto>() {
            @Override
            public void onResponse(Call<MessageDto> call, Response<MessageDto> response) {
                MessageDto responses = response.body();
                if(responses != null) {
                    Toast.makeText(getContext(), responses.getMessage(), Toast.LENGTH_SHORT).show();
                    loadRecipe(getArguments().getString("id"));
                }
                else {
                    warningModal("Erro", "Ocorreu um erro ao enviar a avaliação");
                }
            }

            @Override
            public void onFailure(Call<MessageDto> call, Throwable throwable) {
                warningModal("Erro", throwable.getMessage());
            }
        });
    }

    public void likeRecipe() {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        // Configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Chamada da API
        PersonsService personsApi = retrofit.create(PersonsService.class);

        Call<MessageDto> apiCall = personsApi.likeRecipe(getArguments().getString("id"), user.getEmail());
        apiCall.enqueue(new Callback<MessageDto>() {
            @Override
            public void onResponse(Call<MessageDto> call, Response<MessageDto> response) {
            }

            @Override
            public void onFailure(Call<MessageDto> call, Throwable throwable) {
                warningModal("Erro", throwable.getMessage());
            }
        });
    }

    public void saveRecipeIngredients() {
        String baseUrl = "https://spring-mongo-6c8h.onrender.com";

        // Configurar acesso da API
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Chamada da API
        PersonsService personsApi = retrofit.create(PersonsService.class);

        Call<MessageDto> apiCall = personsApi.saveRecipeIngredients(getArguments().getString("id"), "leticia@gmail.com");
        apiCall.enqueue(new Callback<MessageDto>() {
            @Override
            public void onResponse(Call<MessageDto> call, Response<MessageDto> response) {
                MessageDto recipes = response.body();
                warningModal("Aviso", recipes.getMessage() + "\nOs ingredientes foram salvos em\n Perfil -> Ingredientes salvos");
            }

            @Override
            public void onFailure(Call<MessageDto> call, Throwable throwable) {
                warningModal("Erro", throwable.getMessage());
            }
        });
    }

    public void warningModal(String titleText, String descriptionText) {
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

        title.setText(titleText);
        description.setText(descriptionText);
        closeModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionCard.dismiss();
            }
        });
        descriptionCard.show();
    }
}