

package com.example.android.bakingapp.ui.detail;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.example.android.bakingapp.data.RecipeRepository;


public class IndicesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final RecipeRepository mRepository;
    private final String mRecipeName;

    public IndicesViewModelFactory(RecipeRepository repository, String recipeName) {
        mRepository = repository;
        mRecipeName = recipeName;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new IndicesViewModel(mRepository, mRecipeName);
    }
}
