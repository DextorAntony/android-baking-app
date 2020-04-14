

package com.example.android.bakingapp.ui.shopping;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.bakingapp.data.RecipeRepository;


public class ShoppingViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private final RecipeRepository mRepository;

    public ShoppingViewModelFactory(RecipeRepository repository) {
        mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new ShoppingViewModel(mRepository);
    }
}
