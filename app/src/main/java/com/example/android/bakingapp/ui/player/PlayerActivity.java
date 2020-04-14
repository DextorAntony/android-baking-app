

package com.example.android.bakingapp.ui.player;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.ActivityPlayerBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;

import static com.example.android.bakingapp.utilities.Constant.EXTRA_RECIPE;
import static com.example.android.bakingapp.utilities.Constant.EXTRA_STEP_INDEX;


public class PlayerActivity extends AppCompatActivity {

    
    private Step mStep;

    
    private Recipe mRecipe;

    
    private ActivityPlayerBinding mPlayerBinding;

    
    private int mStepIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayerBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);

        // Get recipe and step index from intent
        getRecipeAndStepIndex();

        // Set the title for a selected recipe
        setTitle(mRecipe.getName());
        // Show back arrow button in the actionbar
        showUpButton();

        // Only create a new fragment when there is no previously saved state
        if (savedInstanceState == null) {

            // Create a new StepDetailFragment
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            // Get the step
            mStep = mRecipe.getSteps().get(mStepIndex);
            // Set the step and step index
            stepDetailFragment.setStep(mStep);
            stepDetailFragment.setStepIndex(mStepIndex);

            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }
    }

    
    private void getRecipeAndStepIndex() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_STEP_INDEX)) {
                // Get the correct step index from the intent
                Bundle b = intent.getBundleExtra(EXTRA_STEP_INDEX);
                mStepIndex = b.getInt(EXTRA_STEP_INDEX);
            }
            if (intent.hasExtra(EXTRA_RECIPE)) {
                // Get the recipe from the intent
                Bundle b = intent.getBundleExtra(EXTRA_RECIPE);
                mRecipe = b.getParcelable(EXTRA_RECIPE);
            }
        }
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
            case R.id.home:
                // Navigate back to DetailActivity when the up button pressed
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
