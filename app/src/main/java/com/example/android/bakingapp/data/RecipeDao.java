

package com.example.android.bakingapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


@Dao
public interface RecipeDao {

    // ShoppingListEntry

    
    @Query("SELECT * FROM shopping_list")
    LiveData<List<ShoppingListEntry>> getAllShoppingList();

    
    @Insert
    void insertIngredient(ShoppingListEntry shoppingListEntry);

    
    @Delete
    void deleteIngredient(ShoppingListEntry shoppingListEntry);

    
    @Query("SELECT ingredient_index FROM shopping_list WHERE recipe_name = :recipeName")
    LiveData<List<Integer>> getIndices(String recipeName);
}
