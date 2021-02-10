package com.example.android.baky.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.android.baky.data.local.models.Ingredient;
import com.example.android.baky.data.local.models.Recipe;

import java.util.List;

@Dao
public interface IngredientDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<Ingredient> ingredients);

  @Query("SELECT * FROM ingredient WHERE recipe_id = :recipeId")
  List<Ingredient> getIngredientsByRecipeId(int recipeId);
}
