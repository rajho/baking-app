package com.example.android.baky.ui.recipe;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.android.baky.R;
import com.example.android.baky.data.local.AppDatabase;
import com.example.android.baky.data.local.models.Step;
import com.example.android.baky.databinding.ActivityRecipeDetailBinding;
import com.example.android.baky.ui.RecipeListActivity;
import com.example.android.baky.ui.fragments.MasterListFragment;
import com.example.android.baky.ui.fragments.StepFragment;
import com.example.android.baky.ui.step.StepActivity;
import com.example.android.baky.widget.IngredientsWidgetProvider;

import static com.example.android.baky.ui.fragments.StepFragment.STEP_FRAGMENT_TAG;

public class RecipeDetailActivity extends AppCompatActivity
    implements MasterListFragment.OnStepClickListener, StepFragment.OnButtonClickListener {
  public static final String EXTRA_RECIPE_ID       = "EXTRA_RECIPE_ID";
  public static final String EXTRA_STEP_ID         = "EXTRA_STEP_ID";
  public static final String EXTRA_STEP_SHORT_DESC = "EXTRA_STEP_SHORT_DESC";
  public static final String PREF_RECIPE_ID        = "com.example.android.baky.PREF_RECIPE_ID";

  private static final int                         DEFAULT_INVALID_ID = -1;
  private              ActivityRecipeDetailBinding mBinding;
  private              int                         mRecipeId;
  private              AppDatabase                 mDb;


  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBinding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
    setContentView(mBinding.getRoot());

    mDb = AppDatabase.getInstance(this);

    if (savedInstanceState != null) {
      mRecipeId = savedInstanceState.getInt(EXTRA_RECIPE_ID);
      loadFragments();
    } else {
      Intent receivingIntent = getIntent();
      if (receivingIntent != null) {

        if (receivingIntent.hasExtra(RecipeListActivity.EXTRA_RECIPE_ID)) {
          mRecipeId = receivingIntent.getIntExtra(
              RecipeListActivity.EXTRA_RECIPE_ID,
              DEFAULT_INVALID_ID
          );
          saveRecipeIdInSettings();
          updateWidget();
          loadStepsInRecipe();
          loadFragments();
        }

        if (receivingIntent.hasExtra(RecipeListActivity.EXTRA_RECIPE_NAME)) {
          String recipeName = receivingIntent.getStringExtra(RecipeListActivity.EXTRA_RECIPE_NAME);
          setUpActionBar(recipeName);
        }
      }
    }
  }

  private void updateWidget() {
    Intent intent = new Intent(this, IngredientsWidgetProvider.class);
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
    // since it seems the onUpdate() is only fired on that:
    int[] ids =
        AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(
            getApplication(),
            IngredientsWidgetProvider.class
        ));
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
    sendBroadcast(intent);

    AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(ids, R.id.ingredients_list);
  }

  private void saveRecipeIdInSettings() {
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(this);

    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(PREF_RECIPE_ID, mRecipeId);
    editor.apply();
  }

  private void setUpActionBar(String title) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(title);
    }
  }

  private void loadStepsInRecipe() {
    mDb.mStepDao().getStepsByRecipeId(mRecipeId).observe(this, steps -> {
      if (mTwoPane) {
        setStepFragment(steps.get(0));
      }
    });
  }


  private void loadFragments() {
    FragmentManager fragmentManager = getSupportFragmentManager();

    MasterListFragment masterListFragment = new MasterListFragment();
    masterListFragment.setRecipeId(mRecipeId);
    fragmentManager.beginTransaction().replace(R.id.list_container, masterListFragment).commit();

    mTwoPane = findViewById(R.id.step_container) != null;
  }

  @Override
  public void onStepSelected(Step step) {
    if (mTwoPane) {
      setStepFragment(step);
    } else {
      Intent intent = new Intent(this, StepActivity.class);
      intent.putExtra(EXTRA_RECIPE_ID, step.getRecipeId());
      intent.putExtra(EXTRA_STEP_ID, step.getId());
      intent.putExtra(EXTRA_STEP_SHORT_DESC, step.getShortDescription());
      startActivity(intent);
    }
  }

  private void setStepFragment(Step step) {

    StepFragment stepFragment = new StepFragment();
    stepFragment.setStepId(step.getId());

    getSupportFragmentManager().beginTransaction()
                               .replace(R.id.step_container, stepFragment, STEP_FRAGMENT_TAG)
                               .commit();


  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    outState.putInt(EXTRA_RECIPE_ID, mRecipeId);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onNextOrPreviousSelected(int newStepNumber) {
    // NA
  }
}