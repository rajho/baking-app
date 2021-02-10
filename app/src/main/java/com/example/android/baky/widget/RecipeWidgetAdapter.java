package com.example.android.baky.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.baky.R;
import com.example.android.baky.data.network.dto.RecipeDto;
import com.example.android.baky.databinding.RecipeWidgetItemBinding;

import java.util.List;

public class RecipeWidgetAdapter extends ArrayAdapter<RecipeDto> {
  private Context mContext;
  private TextView mRecipeText;
  private RecipeWidgetItemBinding mBinding;
  private List<RecipeDto> mRecipes;
  private int mResourceLayout;

  public RecipeWidgetAdapter(@NonNull Context context, int resource, List<RecipeDto> recipes) {
    super(context, resource, recipes);
    mResourceLayout = resource;
    mContext = context;
    mRecipes = recipes;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = convertView;
    RecipeDto recipe = getItem(position);

    if (view == null) {
      view = LayoutInflater.from(mContext).inflate(mResourceLayout, parent);
    }

    mRecipeText = view.findViewById(R.id.recipe_name);
    mRecipeText.setText(recipe.getName());

    return view;
  }
}
