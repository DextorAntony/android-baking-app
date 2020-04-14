

package com.example.android.bakingapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "shopping_list")
public class ShoppingListEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "recipe_name")
    private String recipeName;

    private double quantity;

    private String measure;

    private String ingredient;

    @ColumnInfo(name = "ingredient_index")
    private int index;

    
    @Ignore
    public ShoppingListEntry(String recipeName, double quantity, String measure, String ingredient, int index) {
        this.recipeName = recipeName;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.index = index;
    }

    
    public ShoppingListEntry(int id, String recipeName, double quantity, String measure, String ingredient, int index) {
        this.id = id;
        this.recipeName = recipeName;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getIndex() {
        return index;
    }
}

