

package com.example.android.bakingapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.bakingapp.data.RecipeRepository;
import com.example.android.bakingapp.model.Recipe;

import java.util.List;


public class MainActivityViewModel extends ViewModel {

    private final RecipeRepository mRepository;
    private LiveData<List<Recipe>> mRecipes;

    public MainActivityViewModel(RecipeRepository repository) {
        mRepository = repository;
        mRecipes = mRepository.getRecipeListFromNetwork();
    }

    
    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    
    public void setRecipes() {
        mRecipes = mRepository.getRecipeListFromNetwork();
    }
}
