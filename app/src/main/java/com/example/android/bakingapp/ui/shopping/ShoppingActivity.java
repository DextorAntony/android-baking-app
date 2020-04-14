

package com.example.android.bakingapp.ui.shopping;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingapp.AppExecutors;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeDatabase;
import com.example.android.bakingapp.data.ShoppingListEntry;
import com.example.android.bakingapp.databinding.ActivityShoppingBinding;
import com.example.android.bakingapp.utilities.InjectorUtils;

import java.util.List;


public class ShoppingActivity extends AppCompatActivity {

    
    private RecipeDatabase mDb;

    
    private ShoppingAdapter mShoppingAdapter;

    
    private ActivityShoppingBinding mShoppingBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShoppingBinding = DataBindingUtil.setContentView(this, R.layout.activity_shopping);

        // Initialize the ShoppingAdapter
        initAdapter();

        // Get the RecipeDatabase instance
        mDb = RecipeDatabase.getInstance(getApplicationContext());

        // Setup shopping view model
        setupViewModel();

        // Setup Item touch helper to recognize when a user swipes to delete an item
        setupItemTouchHelper();

        // Display the up button in the actionbar
        showUpButton();
    }

    
    private void initAdapter() {
        // Set the layout manager to the RecyclerView
        mShoppingBinding.rvShopping.setLayoutManager(new LinearLayoutManager(this));
        mShoppingBinding.rvShopping.setHasFixedSize(true);
        // The ShoppingAdapter is responsible for displaying each shopping list item in the list.
        mShoppingAdapter = new ShoppingAdapter(this);
        // // Set adapter to the RecyclerView
        mShoppingBinding.rvShopping.setAdapter(mShoppingAdapter);
    }

    
    private void setupViewModel() {
        // Get the ViewModel from the factory
        ShoppingViewModelFactory factory = InjectorUtils.provideListViewModelFactory(this);
        ShoppingViewModel shoppingViewModel = ViewModelProviders.of(this, factory).get(ShoppingViewModel.class);

        // Update the list of ShoppingListEntries
        shoppingViewModel.getList().observe(this, new Observer<List<ShoppingListEntry>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingListEntry> shoppingListEntries) {
                mShoppingAdapter.setShoppingList(shoppingListEntries);

                // When the shopping list is empty, show an empty view, otherwise, make the
                // shopping list view visible.
                if (shoppingListEntries == null || shoppingListEntries.size() == 0) {
                    showEmptyView();
                } else {
                    showShoppingView();
                }
            }
        });
    }

    
    private void setupItemTouchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Delete shoppingListEntry at the adapter position
                        int adapterPosition = viewHolder.getAdapterPosition();
                        List<ShoppingListEntry> shoppingListEntries =
                                mShoppingAdapter.getShoppingListEntries();
                        mDb.recipeDao().deleteIngredient(shoppingListEntries.get(adapterPosition));
                    }
                });
            }
        }).attachToRecyclerView(mShoppingBinding.rvShopping);
    }

    
    private void showUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                // Navigate back to previous screen when the up button pressed
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
    private void showShoppingView() {
        // First, hide an empty view
        mShoppingBinding.tvEmptyShopping.setVisibility(View.GONE);
        // Then, make sure the shopping list data is visible
        mShoppingBinding.rvShopping.setVisibility(View.VISIBLE);
    }

    
    private void showEmptyView() {
        // First, hide the view for the shopping list
        mShoppingBinding.rvShopping.setVisibility(View.GONE);
        // Then, show an empty view
        mShoppingBinding.tvEmptyShopping.setVisibility(View.VISIBLE);
    }
}
