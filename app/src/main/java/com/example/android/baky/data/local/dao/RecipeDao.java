package com.example.android.baky.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.android.baky.data.local.models.Recipe;
import com.example.android.baky.data.local.models.RecipeWIngredientsAndSteps;

import java.util.List;


@Dao
public interface RecipeDao {

  @Query("SELECT * FROM recipe")
  LiveData<List<Recipe>> getAll();

  @Update(onConflict = OnConflictStrategy.IGNORE)
  void updateRecipe(Recipe recipe);

  @Update
  void deleteRecipe(Recipe recipe);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<Recipe> recipes);

  @Transaction
  @Query("SELECT * FROM recipe WHERE id = :id")
  LiveData<RecipeWIngredientsAndSteps> getFullRecipeById(int id);
}
