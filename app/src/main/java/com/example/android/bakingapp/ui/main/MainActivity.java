

package com.example.android.bakingapp.ui.main;

import android.appwidget.AppWidgetManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.bakingapp.ConnectionStateMonitor;
import com.example.android.bakingapp.ConnectivityReceiver;
import com.example.android.bakingapp.GridAutofitLayoutManager;
import com.example.android.bakingapp.MyApp;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.ActivityMainBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.ui.detail.DetailActivity;
import com.example.android.bakingapp.utilities.BakingUtils;
import com.example.android.bakingapp.utilities.InjectorUtils;
import com.example.android.bakingapp.widget.RecipeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.example.android.bakingapp.utilities.Constant.EXTRA_RECIPE;
import static com.example.android.bakingapp.utilities.Constant.GRID_COLUMN_WIDTH;


public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler,
        ConnectivityReceiver.ConnectivityReceiverListener {

    
    private RecipeAdapter mRecipeAdapter;

    
    private List<Recipe> mRecipeList;

    
    private ActivityMainBinding mMainBinding;

    
    private MainActivityViewModel mMainViewModel;
    private ConnectionStateMonitor mConnectionStateMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set up Timber
        Timber.plant(new Timber.DebugTree());

        // Create a LayoutManager and RecipeAdapter and set them to the RecyclerView
        initAdapter();

        // Observe data and update UI
        setupViewModel();

        // Set the SwipeRefreshLayout triggered by a swipe gesture
        setRefreshLayout();

        // Check internet connection
        checkConnection();

        mConnectionStateMonitor = new ConnectionStateMonitor();
        checkConnectionStateMonitor();
    }

    
    private void initAdapter() {
        // A GridAutofitLayoutManager is responsible for calculating the amount of GridView columns
        // based on screen size and positioning item views within a RecyclerView into a grid layout.
        // Reference: @see "https://codentrick.com/part-4-android-recyclerview-grid/"
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(
                this, GRID_COLUMN_WIDTH);
        // Set the layout manager to the RecyclerView
        mMainBinding.rv.setLayoutManager(layoutManager);

        // Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mMainBinding.rv.setHasFixedSize(true);

        // Create an empty ArrayList
        mRecipeList = new ArrayList<>();

        // The RecipeAdapter is responsible for displaying each recipe in the list.
        mRecipeAdapter = new RecipeAdapter(mRecipeList, this);
        // Set adapter to the RecyclerView
        mMainBinding.rv.setAdapter(mRecipeAdapter);
    }

    
    private void setupViewModel() {
        // Get the MainActivityViewModel from the factory
        MainViewModelFactory factory = InjectorUtils.provideMainViewModelFactory(this);
        mMainViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        // Update the list of recipes
        updateUI();
    }

    
    private void updateUI() {
        // Retrieve live data object using getRecipes() method from the ViewModel
        mMainViewModel.getRecipes().observe(MainActivity.this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipe) {
                if (recipe != null) {
                    mRecipeAdapter.addAll(recipe);
                }
            }
        });
    }

    
    private void setRefreshLayout() {
        // Set the colors used in the progress animation
        mMainBinding.swipeRefresh.setColorSchemeColors(
                getResources().getColor(R.color.image_mint),
                getResources().getColor(R.color.image_light_green),
                getResources().getColor(R.color.image_yellow),
                getResources().getColor(R.color.image_pink));

        // Set the listener to be notified when a refresh is triggered
        mMainBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // Called when a swipe gesture triggers a refresh
            @Override
            public void onRefresh() {
                // Set a new value for the list of recipes
                mMainViewModel.setRecipes();
                // When refreshing, observe data and update UI
                updateUI();

                // Hide refresh progress
                mMainBinding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(Recipe recipe) {
        // Wrap the parcelable into a bundle
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_RECIPE, recipe);

        // Update the list of ingredients using SharedPreferences each time the user selects the
        // recipe
        updateSharedPreference(recipe);

        // Send the update broadcast to the app widget
        sendBroadcastToWidget();

        // Create the Intent the will start the DetailActivity
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        // Pass the bundle through Intent
        intent.putExtra(EXTRA_RECIPE, b);
        // Once the Intent has been created, start the DetailActivity
        startActivity(intent);
    }

    
    private void updateSharedPreference(Recipe recipe) {
        // Get a instance of SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get the editor object
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the ingredient list and convert the list to string
        String ingredientString = BakingUtils.toIngredientString(recipe.getIngredients());

        // Save the string used for displaying in the app widget
        editor.putString(getString(R.string.pref_ingredient_list_key), ingredientString);
        editor.putString(getString(R.string.pref_recipe_name_key), recipe.getName());

        // Convert the list of the steps to String
        String stepString = BakingUtils.toStepString(recipe.getSteps());

        // Save the recipe data used for launching the DetailActivity
        editor.putInt(getString(R.string.pref_recipe_id_key), recipe.getId());
        editor.putString(getString(R.string.pref_step_list_key), stepString);
        editor.putString(getString(R.string.pref_image_key), recipe.getImage());
        editor.putInt(getString(R.string.pref_servings_key), recipe.getServings());

        editor.apply();
    }

    
    private void sendBroadcastToWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

        Intent updateAppWidgetIntent = new Intent();
        updateAppWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateAppWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(updateAppWidgetIntent);
    }

    
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    
    private void checkConnectionStateMonitor() {

        mConnectionStateMonitor.enable(this);
    }

    
    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = getString(R.string.snackbar_connected_internet);
        } else {
            message = getString(R.string.snackbar_no_internet);
        }

        Snackbar snackbar = Snackbar.make(mMainBinding.rv, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register connection status listener
        MyApp.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConnectionStateMonitor.unregister(this);
    }

    
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
