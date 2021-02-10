package com.example.android.baky.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.lifecycle.Observer;

import com.example.android.baky.R;
import com.example.android.baky.data.local.AppDatabase;
import com.example.android.baky.data.local.models.Ingredient;
import com.example.android.baky.ui.recipe.RecipeDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ListViewWidgetService extends RemoteViewsService {

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    // String[] ingredientsArray = intent.getStringArrayExtra("prueba");

    return new ListViewRemoteViewsFactory(
        this.getApplicationContext()
    );
  }
}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory,
                                            SharedPreferences.OnSharedPreferenceChangeListener {
  private final Context          mContext;
  private       List<Ingredient> mIngredients = new ArrayList<>();
  private       AppDatabase      mDb;
  private SharedPreferences mSharedPreferences;


  public ListViewRemoteViewsFactory(Context context) {
    mContext = context;
  }

  @Override
  public void onCreate() {
    // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
    // for example downloading or creating content etc, should be deferred to onDataSetChanged()
    // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR. (Executed
    // in main thread)
    mDb = AppDatabase.getInstance(mContext);
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

  }

  @Override
  public void onDataSetChanged() {
    // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
    // on the collection view corresponding to this factory. You can do heaving lifting in
    // here, synchronously. For example, if you need to process an image, fetch something
    // from the network, etc., it is ok to do it here, synchronously. The widget will remain
    // in its current state while work is being done here, so you don't need to worry about
    // locking up the widget.
    int recipeId = mSharedPreferences.getInt(
        RecipeDetailActivity.PREF_RECIPE_ID,
        -1
    );

    if (recipeId != -1) {
      mIngredients = mDb.mIngredientDao().getIngredientsByRecipeId(recipeId);
    }
  }

  @Override
  public void onDestroy() {
    mIngredients.clear();

  }

  @Override
  public int getCount() {
    return mIngredients.size();
  }

  @Override
  public RemoteViews getViewAt(int position) {
    Ingredient ingredient = mIngredients.get(position);
    String measurePerIngredient =
        ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getIngredient();

    RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_item);
    rv.setTextViewText(R.id.recipe_name, measurePerIngredient);

    return rv;
  }

  @Override
  public RemoteViews getLoadingView() {
    // You can create a custom loading view (for instance when getViewAt() is slow.) If you
    // return null here, you will get the default loading view.
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(RecipeDetailActivity.PREF_RECIPE_ID)) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
      int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(
          mContext,
          IngredientsWidgetProvider.class
      ));
      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredients_list);
    }
  }
}