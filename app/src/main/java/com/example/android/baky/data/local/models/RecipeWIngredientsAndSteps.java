package com.example.android.baky.data.local.models;

import android.service.controls.templates.StatelessTemplate;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeWIngredientsAndSteps {
  @Embedded
  public Recipe recipe;

  @Relation(entity = Ingredient.class, parentColumn = "id", entityColumn = "recipe_id")
  public List<Ingredient> ingredients;

  @Relation(entity = Step.class, parentColumn = "id", entityColumn = "recipe_id")
  public List<Step> steps;
}
