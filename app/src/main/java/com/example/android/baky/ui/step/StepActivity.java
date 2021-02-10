package com.example.android.baky.ui.step;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.baky.R;
import com.example.android.baky.data.local.AppDatabase;
import com.example.android.baky.data.local.models.Step;
import com.example.android.baky.ui.fragments.StepFragment;
import com.example.android.baky.ui.recipe.RecipeDetailActivity;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.android.baky.ui.fragments.StepFragment.STEP_FRAGMENT_TAG;

public class StepActivity extends AppCompatActivity implements StepFragment.OnButtonClickListener {
  private static final String STEP_ID_PARAM          = "STEP_ID_PARAM";
  private static final String RECIPE_ID_PARAM        = "RECIPE_ID_PARAM";
  private static final String STEP_SHORT_DESCRIPTION = "STEP_SHORT_DESCRIPTION";
  private static final int    DEFAULT_INVALID_ID     = -1;

  private AppDatabase mDb;
  private List<Step>  mStepsInRecipe;
  private int         mStepId;
  private int         mRecipeId;
  private String      mTitle;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_step);

    mDb = AppDatabase.getInstance(this);
    if (savedInstanceState != null) {
      mRecipeId = savedInstanceState.getInt(RECIPE_ID_PARAM);
      loadStepsInRecipe();

      mTitle = savedInstanceState.getString(STEP_SHORT_DESCRIPTION);
      setActionBarTitle();

      mStepId = savedInstanceState.getInt(STEP_ID_PARAM);
    } else {
      Intent intent = getIntent();
      if (intent != null && intent.hasExtra(RecipeDetailActivity.EXTRA_STEP_ID)) {
        mStepId = intent.getIntExtra(RecipeDetailActivity.EXTRA_STEP_ID, DEFAULT_INVALID_ID);
        setStepFragment();
      }

      if (intent != null && intent.hasExtra(RecipeDetailActivity.EXTRA_STEP_SHORT_DESC)) {
        mTitle = intent.getStringExtra(RecipeDetailActivity.EXTRA_STEP_SHORT_DESC);
        setActionBarTitle();
      }

      if (intent != null && intent.hasExtra(RecipeDetailActivity.EXTRA_RECIPE_ID)) {
        mRecipeId = intent.getIntExtra(
            RecipeDetailActivity.EXTRA_RECIPE_ID,
            DEFAULT_INVALID_ID
        );
        loadStepsInRecipe();
      }
    }
  }

  /**
   * Depends on mRecipeId
   */
  private void loadStepsInRecipe() {
    mDb.mStepDao().getStepsByRecipeId(mRecipeId).observe(this, steps -> mStepsInRecipe = steps);
  }

  /**
   * Depends on mStepId
   */
  private void setStepFragment() {
    StepFragment stepFragment = new StepFragment();
    stepFragment.setStepId(mStepId);

    getSupportFragmentManager().beginTransaction()
                               .replace(R.id.step_container, stepFragment, STEP_FRAGMENT_TAG)
                               .commit();
  }

  @Override
  public void onNextOrPreviousSelected(int newStepNumber) {
    Step newStep = null;
    List<Step> stepsFound = mStepsInRecipe.stream()
                                          .filter(s -> s.getSortOrder() == newStepNumber)
                                          .collect(Collectors.toList());

    // Existing step
    if (stepsFound.size() > 0) {
      newStep = stepsFound.get(0);
      mStepId = newStep.getId();
      setStepFragment();
    } else {
      // There are no more steps, so we go back to Step 0 in the Recipe
      List<Step> defaultStep = mStepsInRecipe.stream()
                                             .filter(s -> s.getSortOrder() == 0)
                                             .collect(Collectors.toList());
      if (defaultStep.size() > 0) {
        newStep = defaultStep.get(0);
        mStepId = newStep.getId();
        setStepFragment();
      }
    }

    if (newStep != null) {
      mTitle = newStep.getShortDescription();
      setActionBarTitle();
    }
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    outState.putInt(STEP_ID_PARAM, mStepId);
    outState.putInt(RECIPE_ID_PARAM, mRecipeId);
    outState.putString(STEP_SHORT_DESCRIPTION, mTitle);
    super.onSaveInstanceState(outState);
  }

  /**
   * Depends on mTitle
   */
  private void setActionBarTitle() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(mTitle);
    }
  }
}