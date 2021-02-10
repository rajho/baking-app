package com.example.android.baky.data.local.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class,
                                  parentColumns = "id",
                                  childColumns = "recipe_id"),
        indices = {
            @Index("recipe_id"),
            @Index(value = {"ingredient", "recipe_id"}, unique = true)
        }
)
public class Ingredient {
  @PrimaryKey(autoGenerate = true)
  private int    id;
  private float  quantity;
  private String measure;
  private String ingredient;

  @ColumnInfo(name = "recipe_id")
  private int recipeId;

  public Ingredient(int id, float quantity, String measure, String ingredient, int recipeId) {
    this.id         = id;
    this.quantity   = quantity;
    this.measure    = measure;
    this.ingredient = ingredient;
    this.recipeId   = recipeId;
  }

  @Ignore
  public Ingredient(float quantity, String measure, String ingredient, int recipeId) {
    this.quantity   = quantity;
    this.measure    = measure;
    this.ingredient = ingredient;
    this.recipeId   = recipeId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public float getQuantity() {
    return quantity;
  }

  public void setQuantity(float quantity) {
    this.quantity = quantity;
  }

  public String getMeasure() {
    return measure;
  }

  public void setMeasure(String measure) {
    this.measure = measure;
  }

  public String getIngredient() {
    return ingredient;
  }

  public void setIngredient(String ingredient) {
    this.ingredient = ingredient;
  }

  public int getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(int recipeId) {
    this.recipeId = recipeId;
  }
}
