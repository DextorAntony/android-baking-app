

package com.example.android.bakingapp.ui.detail;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.IngredientsListItemBinding;
import com.example.android.bakingapp.model.Ingredient;

import java.util.List;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    
    private List<Ingredient> mIngredients;
    
    private String mRecipeName;
    
    private List<Integer> mIndices;

    
    private final IngredientsAdapterOnClickHandler mOnClickHandler;

    
    public interface IngredientsAdapterOnClickHandler {
        void onIngredientClick(int ingredientIndex);
    }

    
    public IngredientsAdapter(List<Ingredient> ingredients, IngredientsAdapterOnClickHandler onClickHandler,
                              String recipeName, List<Integer> indices) {
        mIngredients = ingredients;
        mOnClickHandler = onClickHandler;
        mRecipeName = recipeName;
        mIndices = indices;
    }

    
    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        IngredientsListItemBinding ingredientsItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.ingredients_list_item, parent, false);
        return new IngredientsViewHolder(ingredientsItemBinding);
    }

    
    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        Ingredient ingredient = mIngredients.get(position);
        holder.bind(ingredient, position);
    }

    
    @Override
    public int getItemCount() {
        if (null == mIngredients) return 0;
        return mIngredients.size();
    }

    
    public void addAll(List<Ingredient> ingredients) {
        mIngredients.clear();
        mIngredients.addAll(ingredients);
        notifyDataSetChanged();
    }

    
    public void setIndices(List<Integer> indices) {
        mIndices = indices;
        notifyDataSetChanged();
    }

    
    public class IngredientsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        
        private IngredientsListItemBinding mIngredientsItemBinding;

        
        public IngredientsViewHolder(IngredientsListItemBinding ingredientsItemBinding) {
            super(ingredientsItemBinding.getRoot());
            mIngredientsItemBinding = ingredientsItemBinding;

            // Call setOnClickListener on the ImageView
            mIngredientsItemBinding.ivAdd.setOnClickListener(this);
        }

        
        void bind(Ingredient ingredient, int position) {
            // Set the quantity, measure, ingredient to the TextView
            mIngredientsItemBinding.tvQuantity.setText(String.valueOf(ingredient.getQuantity()));
            mIngredientsItemBinding.tvMeasure.setText(ingredient.getMeasure());
            mIngredientsItemBinding.tvIngredient.setText(ingredient.getIngredient());

            // Change the image based on whether or not the ingredient exists in the shopping list
            if (mIndices.contains(position)) {
                // If the ingredient is in the shopping list, display a checked image.
                mIngredientsItemBinding.ivAdd.setImageResource(R.drawable.checked);
            } else {
                // Otherwise, display an unchecked image.
                mIngredientsItemBinding.ivAdd.setImageResource(R.drawable.unchecked);
            }
        }

        
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mOnClickHandler.onIngredientClick(adapterPosition);
        }
    }
}
