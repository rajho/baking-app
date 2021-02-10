package com.example.android.baky.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.baky.R;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {


  static void updateAppWidget(Context context,
      AppWidgetManager appWidgetManager,
      int appWidgetId) {

    // Set up the intent that starts the Listview Service, which will
    // provide the views for this collection
    Intent listServiceIntent = new Intent(context, ListViewWidgetService.class);
    listServiceIntent.setData(Uri.parse(listServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
    views.setRemoteAdapter(R.id.ingredients_list, listServiceIntent);
    views.setEmptyView(R.id.ingredients_list, R.id.empty_view);

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }
}