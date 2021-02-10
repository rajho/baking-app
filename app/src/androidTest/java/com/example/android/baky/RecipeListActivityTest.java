package com.example.android.baky;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.android.baky.idlingResource.SimpleIdlingResource;
import com.example.android.baky.ui.RecipeListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {
  @Rule
  public ActivityScenarioRule<RecipeListActivity> mActivityRule = new ActivityScenarioRule<>(
      RecipeListActivity.class);

  @Before
  public void registerIdlingResource() {
    IdlingRegistry.getInstance()
                  .register(SimpleIdlingResource.getInstance().getCountingIdlingResource());
  }

  @Test
  public void activity_hasRecipes() {
    onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
  }

  @Test
  public void recipe_opensRecipeDetail() {
    onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(
        withText("Nutella Pie")), click()));

    onView(withId(R.id.list_container)).check(matches(isDisplayed()));
  }

  @After
  public void unregisterIdlingResource() {
    IdlingRegistry.getInstance()
                  .unregister(SimpleIdlingResource.getInstance().getCountingIdlingResource());
  }
}
