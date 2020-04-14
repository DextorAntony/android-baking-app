

package com.example.android.bakingapp.ui.detail;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.AppExecutors;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeDatabase;
import com.example.android.bakingapp.data.ShoppingListEntry;
import com.example.android.bakingapp.databinding.FragmentMasterListIngredientsBinding;
import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.utilities.Constant.EXTRA_RECIPE;


public class MasterListIngredientsFragment extends Fragment implements
        IngredientsAdapter.IngredientsAdapterOnClickHandler {

    
    private Recipe mRecipe;

    
    private IngredientsAdapter mIngredientsAdapter;

    
    private FragmentMasterListIngredientsBinding mIngredientBinding;

    
    private IndicesViewModel mIndicesViewModel;

    
    private RecipeDatabase mDb;

    
    private List<Integer> mIndices;

    
    public MasterListIngredientsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instantiate FragmentMasterListIngredientsBinding using DataBindingUtil
        mIngredientBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_master_list_ingredients, container, false);
        View rootView = mIngredientBinding.getRoot();

        // Get the recipe data from the MainActivity
        mRecipe = getRecipeData();

        // Initialize a IngredientAdapter
        initAdapter();

        // Display the number of servings in the two-pane tablet case
        setNumServingsTwoPane(mRecipe);
        // Display the number of ingredients in the two-pane tablet case
        setNumIngredientsTwoPane(mRecipe);

        // Get the RecipeDatabase instance
        mDb = RecipeDatabase.getInstance(this.getContext());

        return rootView;
    }

    
    private void setNumServingsTwoPane(Recipe recipe) {
        // This tvServings will only initially exist in the two-pane tablet case
        if (mIngredientBinding.tvServings != null) {
            String numServings = getString(R.string.servings_label) + getString(R.string.space)
                    + String.valueOf(recipe.getServings());
            mIngredientBinding.tvServings.setText(numServings);
        }
    }

    
    private void setNumIngredientsTwoPane(Recipe recipe) {
        // The TextView tvIngredientsLabel will only initially exist in the two-pane tablet case
        if (mIngredientBinding.tvIngredientsLabel != null) {
            String numIngredients = getString(R.string.ingredients_label) +
                    getString(R.string.space) + getString(R.string.open_parenthesis)
                    + recipe.getIngredients().size() + getString(R.string.close_parenthesis);
            mIngredientBinding.tvIngredientsLabel.setText(numIngredients);
        }
    }

    
    private void initAdapter() {
        // Create a new list of the ingredient and indices
        List<Ingredient> ingredients = new ArrayList<>();
        mIndices = new ArrayList<>();

        // The IngredientsAdapter is responsible for displaying each ingredient in the list
        mIngredientsAdapter = new IngredientsAdapter(ingredients, this,
                mRecipe.getName(), mIndices);

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        // Set the layout manager to the RecyclerView
        mIngredientBinding.rvIngredients.setLayoutManager(layoutManager);
        // Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mIngredientBinding.rvIngredients.setHasFixedSize(true);

        // Set the IngredientsAdapter to the RecyclerView
        mIngredientBinding.rvIngredients.setAdapter(mIngredientsAdapter);

        // Add a list of ingredients to the IngredientsAdapter
        mIngredientsAdapter.addAll(mRecipe.getIngredients());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the recipe data from the MainActivity
        mRecipe = getRecipeData();

        // Setup indices view model
        setupIndicesViewModel(getActivity());
    }

    
    private Recipe getRecipeData() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_RECIPE)) {
                // Receive the Recipe object which contains ID, name, ingredients, steps, servings,
                // and image of the recipe
                Bundle b = intent.getBundleExtra(EXTRA_RECIPE);
                mRecipe = b.getParcelable(EXTRA_RECIPE);
            }
        }
        return mRecipe;
    }

    
    private void setupIndicesViewModel(Context context) {
        // Get the ViewModel from the factory
        IndicesViewModelFactory factory = InjectorUtils.provideIndicesViewModelFactory(
                context, mRecipe.getName());
        mIndicesViewModel = ViewModelProviders.of(this, factory).get(IndicesViewModel.class);

        // Update the list of ingredient indices
        mIndicesViewModel.getIndices().observe(this, new Observer<List<Integer>>() {
            @Override
            public void onChanged(@Nullable List<Integer> integers) {
                mIndices = integers;
                mIngredientsAdapter.setIndices(mIndices);
            }
        });
    }

    
    @Override
    public void onIngredientClick(final int ingredientIndex) {
        // Get ShoppingListEntry based on current recipe name and ingredient
        final ShoppingListEntry currentShoppingListEntry = getCurrentShoppingListEntry(ingredientIndex);

        if (!mIndices.contains(ingredientIndex)) {
            // If the current ingredient index does not exist in the list of indices, insert it into the database.
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.recipeDao().insertIngredient(currentShoppingListEntry);
                }
            });

            // Show a snack bar message when a user adds an ingredient to a shopping list
            Snackbar.make(mIngredientBinding.getRoot(), R.string.snackbar_added,
                    Snackbar.LENGTH_SHORT).show();

        } else {

        }
    }

    
    private ShoppingListEntry getCurrentShoppingListEntry(int ingredientIndex) {
        // Get the ingredient
        Ingredient ingredient = mRecipe.getIngredients().get(ingredientIndex);

        return new ShoppingListEntry(mRecipe.getName(), ingredient.getQuantity(),
                ingredient.getMeasure(), ingredient.getIngredient(), ingredientIndex);
    }

}
