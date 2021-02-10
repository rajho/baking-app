package com.example.android.baky.utilities;

import com.example.android.baky.data.network.dto.RecipeDto;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RecipesJsonUtils {

  public static List<RecipeDto> getRecipesListFromJson(String jsonRecipes) throws IOException {
    Moshi                        moshi       = new Moshi.Builder().build();
    Type                         listMyData  = Types.newParameterizedType(List.class, RecipeDto.class);
    JsonAdapter<List<RecipeDto>> jsonAdapter = moshi.adapter(listMyData);

    return jsonAdapter.fromJson(jsonRecipes);
  }
}
