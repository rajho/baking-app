package com.example.android.baky.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.baky.R;
import com.example.android.baky.data.local.models.Recipe;
import com.example.android.baky.databinding.RecipeListItemBinding;


import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {
  private final RecipesAdapterOnClickHandler mClickHandler;

  private List<Recipe> mRecipes;


  public RecipeListAdapter(RecipesAdapterOnClickHandler clickHandler) {
    mClickHandler = clickHandler;
  }

  public void setData(List<Recipe> recipes) {
    mRecipes = recipes;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View mItemView = LayoutInflater.from(parent.getContext())
                                   .inflate(R.layout.recipe_list_item, parent, false);
    return new RecipeViewHolder(mItemView);
  }

  @Override
  public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
    Recipe recipe = mRecipes.get(position);
    holder.mBinding.recipeText.setText(recipe.getName());
  }

  @Override
  public int getItemCount() {
    if (mRecipes == null) {
      return 0;
    }

    return mRecipes.size();
  }

  public interface RecipesAdapterOnClickHandler {
    void onClickRecipe(int recipeId, String recipeName);
  }

  public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final RecipeListItemBinding mBinding;

    public RecipeViewHolder(@NonNull View itemView) {
      super(itemView);
      mBinding = RecipeListItemBinding.bind(itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      int    position   = getAdapterPosition();
      Recipe recipe = mRecipes.get(position);
      mClickHandler.onClickRecipe(recipe.getId(), recipe.getName());
    }
  }
}
