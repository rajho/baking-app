package com.example.android.baky.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.example.android.baky.data.local.AppDatabase;
import com.example.android.baky.data.local.models.Ingredient;
import com.example.android.baky.data.local.models.Recipe;
import com.example.android.baky.data.local.models.Step;
import com.example.android.baky.data.network.dto.RecipeDto;
import com.example.android.baky.data.network.NetworkApi;
import com.example.android.baky.databinding.ActivityRecipeListBinding;
import com.example.android.baky.idlingResource.SimpleIdlingResource;
import com.example.android.baky.ui.recipe.RecipeDetailActivity;
import com.example.android.baky.utilities.AppExecutors;
import com.example.android.baky.utilities.RecipesJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeListActivity extends AppCompatActivity
    implements RecipeListAdapter.RecipesAdapterOnClickHandler,
               LoaderManager.LoaderCallbacks<List<RecipeDto>> {
  private static final String LOG_TAG           = RecipeListActivity.class.getSimpleName();
  public static final  String EXTRA_RECIPE_ID   = "EXTRA_RECIPE_ID";
  public static final  String EXTRA_RECIPE_NAME = "EXTRA_RECIPE_NAME";

  private static final int                       RECIPES_LOADER = 1;
  private              RecipeListAdapter         mAdapter;
  private              ActivityRecipeListBinding mBinding;
  private              AppDatabase               mDb;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = ActivityRecipeListBinding.inflate(getLayoutInflater());
    setContentView(mBinding.getRoot());

    mDb = AppDatabase.getInstance(this);

    mAdapter = new RecipeListAdapter(this);
    mBinding.recyclerView.setAdapter(mAdapter);

    loadNetworkRecipesData();
    loadDBRecipesData();
  }

  private void loadDBRecipesData() {
    mDb.mRecipeDao().getAll().observe(this, recipes -> {
      if (recipes.isEmpty()) {
        showErrorMessage();
      } else {
        mAdapter.setData(recipes);
        mBinding.loadingIndicator.setVisibility(View.GONE);
        showRecipesView();
      }
    });
  }

  private void loadNetworkRecipesData() {
    if (!connectedToInternet()) {
      showErrorMessage();
      Log.d(RecipeListActivity.class.getSimpleName(), "Device is not connected to internet");
    }

    LoaderManager           loaderManager     = LoaderManager.getInstance(this);
    Loader<List<RecipeDto>> recipesListLoader = loaderManager.getLoader(RECIPES_LOADER);

    if (recipesListLoader == null) {
      loaderManager.initLoader(RECIPES_LOADER, null, this);
    } else {
      loaderManager.restartLoader(RECIPES_LOADER, null, this);
    }
  }


  private void showRecipesView() {
    mBinding.errorMessageText.setVisibility(View.INVISIBLE);
    mBinding.recyclerView.setVisibility(View.VISIBLE);
  }

  private void showErrorMessage() {
    mBinding.recyclerView.setVisibility(View.INVISIBLE);
    mBinding.errorMessageText.setVisibility(View.VISIBLE);
  }

  @Override
  public void onClickRecipe(int recipeId, String recipeName) {
    Intent recipeDetailintent = new Intent(this, RecipeDetailActivity.class);
    recipeDetailintent.putExtra(EXTRA_RECIPE_ID, recipeId);
    recipeDetailintent.putExtra(EXTRA_RECIPE_NAME, recipeName);
    startActivity(recipeDetailintent);
  }

  @NonNull
  @Override
  public Loader<List<RecipeDto>> onCreateLoader(int id, @Nullable Bundle args) {
    return new AsyncTaskLoader<List<RecipeDto>>(this) {
      List<RecipeDto> mRecipes;

      @Override
      protected void onStartLoading() {
        super.onStartLoading();
        SimpleIdlingResource.getInstance().increment();

        if (mRecipes != null) {
          deliverResult(mRecipes);
        } else {
          mBinding.recyclerView.setVisibility(View.INVISIBLE);
          mBinding.loadingIndicator.setVisibility(View.VISIBLE);
          forceLoad();
        }
      }

      @Nullable
      @Override
      public List<RecipeDto> loadInBackground() {
        try {
          URL    recipesURL   = NetworkApi.buildAPIURL();
          String jsonResponse = NetworkApi.getResponseFromHttpUrl(recipesURL);

          return RecipesJsonUtils.getRecipesListFromJson(jsonResponse);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }

      @Override
      public void deliverResult(@Nullable List<RecipeDto> data) {
        mRecipes = data;
        super.deliverResult(data);
      }
    };
  }


  @Override
  public void onLoadFinished(@NonNull Loader<List<RecipeDto>> loader, List<RecipeDto> data) {
    AppExecutors.getInstance().diskIO().execute(() -> {
      List<Recipe>     recipes     = new ArrayList<>();
      List<Ingredient> ingredients = new ArrayList<>();
      List<Step>       steps       = new ArrayList<>();

      //noinspection SimplifyStreamApiCallChains
      data.stream().forEach(recipeDto -> {
        recipes.add(recipeDto.asDatabaseModel());
        ingredients.addAll(recipeDto.getIngredients()
                                    .stream()
                                    .map(i -> i.asDatabaseModel(recipeDto.getId()))
                                    .collect(Collectors.toList()));
        steps.addAll(recipeDto.getSteps()
                              .stream()
                              .map(s -> s.asDatabaseModel(recipeDto.getId()))
                              .collect(Collectors.toList()));
      });

      mDb.mRecipeDao().insertAll(recipes);
      mDb.mIngredientDao().insertAll(ingredients);
      mDb.mStepDao().insertAll(steps);
    });

    mBinding.loadingIndicator.setVisibility(View.INVISIBLE);
    SimpleIdlingResource.getInstance().decrement();
  }

  @Override
  public void onLoaderReset(@NonNull Loader<List<RecipeDto>> loader) {

  }

  private boolean connectedToInternet() {
    ConnectivityManager cm             = getSystemService(ConnectivityManager.class);
    Network             currentNetwork = cm.getActiveNetwork();
    return currentNetwork != null;
  }
}