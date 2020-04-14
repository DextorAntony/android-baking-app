

package com.example.android.bakingapp.ui.shopping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.bakingapp.data.RecipeRepository;
import com.example.android.bakingapp.data.ShoppingListEntry;

import java.util.List;


public class ShoppingViewModel extends ViewModel {

    private final RecipeRepository mRepository;
    private final LiveData<List<ShoppingListEntry>> mList;

    public ShoppingViewModel(RecipeRepository repository) {
        mRepository = repository;
        mList = mRepository.getAllShoppingList();
    }

    
    public LiveData<List<ShoppingListEntry>> getList() {
        return mList;
    }
}
