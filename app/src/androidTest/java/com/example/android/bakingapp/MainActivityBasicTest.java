

package com.example.android.bakingapp;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.android.bakingapp.ui.main.MainActivity;
import com.example.android.bakingapp.utilities.OkHttpProvider;
import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.android.bakingapp.utilities.Constant.NAME_OKHTTP;
import static com.example.android.bakingapp.utilities.Constant.POSITION_ZERO;
import static com.example.android.bakingapp.utilities.Constant.RECIPE_NAME_AT_ZERO;


@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    // Register the IdlingResource for OkHttp before the test
    @Before
    public void registerIdlingResource() {
        IdlingResource idlingResource = OkHttp3IdlingResource.create(NAME_OKHTTP,
                OkHttpProvider.getOkHttpInstance());
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void scrollToPosition_CheckRecipeName() {
        // Scroll to the position that needs to be matched
        onView(withId(R.id.rv))
                .perform(RecyclerViewActions.scrollToPosition(POSITION_ZERO));

        // Match the recipe name and check that the correct recipe name is displayed
        onView(withText(RECIPE_NAME_AT_ZERO)).check(matches(isDisplayed()));
    }

    // Unregister resources when not needed to avoid malfunction
    @After
    public void unregisterIdlingResource() {
        IdlingResource idlingResource = OkHttp3IdlingResource.create(NAME_OKHTTP,
                OkHttpProvider.getOkHttpInstance());
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
