

package com.example.android.bakingapp;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.appcompat.widget.Toolbar;

import com.example.android.bakingapp.ui.main.MainActivity;
import com.example.android.bakingapp.utilities.OkHttpProvider;
import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.android.bakingapp.utilities.Constant.NAME_OKHTTP;
import static com.example.android.bakingapp.utilities.Constant.POSITION_ONE;
import static com.example.android.bakingapp.utilities.Constant.RECIPE_NAME_AT_ONE;
import static org.hamcrest.core.Is.is;


@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    // Register the IdlingResource for OkHttp before the test
    @Before
    public void registerIdlingResource() {
        IdlingResource idlingResource = OkHttp3IdlingResource.create(NAME_OKHTTP,
                OkHttpProvider.getOkHttpInstance());
        IdlingRegistry.getInstance().register(idlingResource);
    }

    
    @Test
    public void clickRecyclerViewItem_OpensDetailActivity() {

        // Check that the toolbar title in the MainActivity matches app name
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        matchToolbarTitle(title);

        // Clicks on a specific RecyclerView item
        onView(withId(R.id.rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(POSITION_ONE, click()));

        // Checks that the DetailActivity opens with the correct title displayed
        matchToolbarTitle(RECIPE_NAME_AT_ONE);

    }

    
    private static void matchToolbarTitle(CharSequence title) {
        onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    
    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {

            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                // Checks the value fo the title by calling Toolbar.getTitle()
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    // Unregister resources when not needed to avoid malfunction
    @After
    public void unregisterIdlingResource() {
        IdlingResource idlingResource = OkHttp3IdlingResource.create(NAME_OKHTTP,
                OkHttpProvider.getOkHttpInstance());
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
