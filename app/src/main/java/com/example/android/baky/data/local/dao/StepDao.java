package com.example.android.baky.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.android.baky.data.local.models.Recipe;
import com.example.android.baky.data.local.models.Step;

import java.util.List;

@Dao
public interface StepDao {
  @Query("SELECT * FROM step")
  LiveData<List<Step>> getAll();

  @Update(onConflict = OnConflictStrategy.IGNORE)
  void updateStep(Step step);

  @Update
  void deleteStep(Step step);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertAll(List<Step> steps);

  @Query("SELECT * FROM step where id = :id")
  LiveData<Step> getStepById(int id);

  @Query("SELECT * FROM step where recipe_id = :recipeId")
  LiveData<List<Step>> getStepsByRecipeId(int recipeId);
}
