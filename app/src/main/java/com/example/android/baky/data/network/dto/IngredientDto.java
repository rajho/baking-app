package com.example.android.baky.data.network.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.baky.data.local.models.Ingredient;

public class IngredientDto implements Parcelable {
  private float quantity;
  private String measure;
  private String ingredient;

  @SuppressWarnings("unused")
  public IngredientDto(int quantity, String measure, String ingredient) {
    this.quantity   = quantity;
    this.measure    = measure;
    this.ingredient = ingredient;
  }

  protected IngredientDto(Parcel in) {
    quantity   = in.readFloat();
    measure    = in.readString();
    ingredient = in.readString();
  }

  public static final Creator<IngredientDto> CREATOR = new Creator<IngredientDto>() {
    @Override
    public IngredientDto createFromParcel(Parcel in) {
      return new IngredientDto(in);
    }

    @Override
    public IngredientDto[] newArray(int size) {
      return new IngredientDto[size];
    }
  };

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeFloat(quantity);
    dest.writeString(measure);
    dest.writeString(ingredient);
  }

  public Ingredient asDatabaseModel(int recipeId){
    return new Ingredient(quantity, measure, ingredient, recipeId);
  }
}
