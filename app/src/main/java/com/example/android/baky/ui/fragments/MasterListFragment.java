package com.example.android.baky.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.baky.data.local.AppDatabase;
import com.example.android.baky.data.local.models.Ingredient;
import com.example.android.baky.data.local.models.Step;
import com.example.android.baky.databinding.FragmentMasterListBinding;
import com.example.android.baky.databinding.RecipeStepItemBinding;

import java.util.List;

public class MasterListFragment extends Fragment {
  private static final String RECIPE_PARAM = "recipe-param";

  private FragmentMasterListBinding mBinding;
  private int                 mRecipeId;
  private AppDatabase         mDb;
  private OnStepClickListener mCallback;

  public MasterListFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mBinding = FragmentMasterListBinding.inflate(inflater, container, false);

    if (savedInstanceState != null) {
      mRecipeId = savedInstanceState.getInt(RECIPE_PARAM);
    }

    mDb = AppDatabase.getInstance(getContext());
    fetchRecipeById();

    return mBinding.getRoot();
  }

  private void fetchRecipeById() {
    mDb.mRecipeDao().getFullRecipeById(mRecipeId).observe(this, recipeWIngredientsAndSteps -> {
      loadIngredientsUI(recipeWIngredientsAndSteps.ingredients);
      loadStepsUI(recipeWIngredientsAndSteps.steps);
    });
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    try {
      mCallback = (OnStepClickListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
    }
  }

  public void setRecipeId(int recipeId) {
    mRecipeId = recipeId;
  }

  private void loadIngredientsUI(List<Ingredient> ingredientsList) {
    StringBuilder ingredients = new StringBuilder();
    for (Ingredient i : ingredientsList) {
      ingredients.append(i.getQuantity())
                 .append(" ")
                 .append(i.getMeasure())
                 .append(" ")
                 .append(i.getIngredient())
                 .append("\n");
    }
    mBinding.textRecipeIngredients.setText(ingredients);
  }

  private void loadStepsUI(List<Step> steps) {
    for (Step step : steps) {
      RecipeStepItemBinding stepItemBinding = RecipeStepItemBinding.inflate(getLayoutInflater());
      stepItemBinding.textRecipeStep.setText(step.getShortDescription());
      stepItemBinding.textRecipeStep.setOnClickListener(v -> mCallback.onStepSelected(step));
      mBinding.layoutRecipeSteps.addView(stepItemBinding.getRoot());
    }
  }

  public interface OnStepClickListener {
    void onStepSelected(Step step);
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(RECIPE_PARAM, mRecipeId);
  }
}
