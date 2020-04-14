

package com.example.android.bakingapp.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.bakingapp.data.RecipeRepository;

import java.util.List;


public class IndicesViewModel extends ViewModel {

    private final RecipeRepository mRepository;

    
    private LiveData<List<Integer>> mIndices;

    public IndicesViewModel(RecipeRepository repository, String recipeName) {
        mRepository = repository;
        mIndices = mRepository.getIndices(recipeName);
    }

    
    public LiveData<List<Integer>> getIndices() {
        return mIndices;
    }
}
