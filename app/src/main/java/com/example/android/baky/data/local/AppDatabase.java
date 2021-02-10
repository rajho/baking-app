package com.example.android.baky.data.local;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.android.baky.data.local.dao.IngredientDao;
import com.example.android.baky.data.local.dao.RecipeDao;
import com.example.android.baky.data.local.dao.StepDao;
import com.example.android.baky.data.local.models.Ingredient;
import com.example.android.baky.data.local.models.Recipe;
import com.example.android.baky.data.local.models.Step;

@Database(entities = {Recipe.class, Ingredient.class, Step.class},
          version = 1,
          exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
  private static final String      LOG_TAG = AppDatabase.class.getSimpleName();
  private static final String      DATABASE_NAME = "baky";
  public static        AppDatabase sInstance;

  public static AppDatabase getInstance(Context context) {
    if (sInstance == null) {
      synchronized (AppDatabase.class) {
        Log.d(LOG_TAG, "Creating new database instance");
        sInstance = Room.databaseBuilder(context.getApplicationContext(),
            AppDatabase.class,
            DATABASE_NAME
        ).build();
      }
    }
    Log.d(LOG_TAG, "Getting the database instance");
    return sInstance;
  }

  public abstract RecipeDao mRecipeDao();

  public abstract IngredientDao mIngredientDao();

  public abstract StepDao mStepDao();


}
