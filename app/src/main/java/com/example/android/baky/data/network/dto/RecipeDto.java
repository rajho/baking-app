package com.example.android.baky.data.network.dto;


import com.example.android.baky.data.local.models.Recipe;

import java.util.List;

public class RecipeDto {
  private int                 id;
  private String              name;
  private List<IngredientDto> ingredients;
  private List<StepDto>       steps;
  private int                 servings;
  private String              image;

  @SuppressWarnings("unused")
  /*public RecipeDto(int id,
      String name,
      List<IngredientDto> ingredients,
      List<StepDto> steps,
      int servings,
      String image) {
    this.id          = id;
    this.name        = name;
    this.ingredients = ingredients;
    this.steps       = steps;
    this.servings    = servings;
    this.image       = image;
  }*/

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<IngredientDto> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<IngredientDto> ingredients) {
    this.ingredients = ingredients;
  }

  public List<StepDto> getSteps() {
    return steps;
  }

  public void setSteps(List<StepDto> steps) {
    this.steps = steps;
  }

  public int getServings() {
    return servings;
  }

  public void setServings(int servings) {
    this.servings = servings;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Recipe asDatabaseModel(){
    return new Recipe(id, name, servings, image);
  }

}