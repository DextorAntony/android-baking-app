

package com.example.android.bakingapp.ui.main;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.RecipeListItemBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.utilities.BakingUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.bakingapp.utilities.Constant.RECIPE_IMAGE_PADDING;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    
    private final RecipeAdapterOnClickHandler mOnClickHandler;

    
    public interface RecipeAdapterOnClickHandler {
        void  onItemClick(Recipe recipe);
    }

    
    private List<Recipe> mRecipeList;

    
    public RecipeAdapter(List<Recipe> recipeList, RecipeAdapterOnClickHandler onClickHandler) {
        mRecipeList = recipeList;
        mOnClickHandler = onClickHandler;
    }

    
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecipeListItemBinding recipeItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(recipeItemBinding);
    }

    
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipeList.get(position);
        holder.bind(recipe, position);
    }


    
    @Override
    public int getItemCount() {
        if (null == mRecipeList) return 0;
        return mRecipeList.size();
    }

    
    public void addAll(List<Recipe> recipeList) {
        mRecipeList.clear();
        mRecipeList.addAll(recipeList);
        notifyDataSetChanged();
    }

    
    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        
        private RecipeListItemBinding mRecipeItemBinding;

        
        public RecipeViewHolder(RecipeListItemBinding recipeItemBinding) {
            super(recipeItemBinding.getRoot());
            mRecipeItemBinding = recipeItemBinding;

            // Call setOnClickListener on the View
            itemView.setOnClickListener(this);
        }

        
        void bind(Recipe recipe, int position) {
            // Set the name of the recipe
            mRecipeItemBinding.tvName.setText(recipe.getName());

            String imageUrl = recipe.getImage();
            if (imageUrl.isEmpty()) {
                // If the image URL does not exist, set background color of the ImageView differently
                // depending on the position.
                int imageColorResourceId = BakingUtils.getImageBackGroundColor(itemView.getContext(), position);
                mRecipeItemBinding.ivImage.setBackgroundColor(imageColorResourceId);

                // Set image resource differently depending on the position
                int imageResourceId = BakingUtils.getImageResource(position);
                mRecipeItemBinding.ivImage.setImageResource(imageResourceId);

                // Set padding for resizing image
                mRecipeItemBinding.ivImage.setPadding(RECIPE_IMAGE_PADDING,
                        RECIPE_IMAGE_PADDING, RECIPE_IMAGE_PADDING, RECIPE_IMAGE_PADDING);

            } else {
                // If the image URL exists, use the Picasso library to upload the image
                Picasso.with(itemView.getContext())
                        .load(imageUrl)
                        .error(R.drawable.recipe_error_image)
                        .placeholder(R.drawable.recipe_error_image)
                        .into(mRecipeItemBinding.ivImage);
            }

            // Set the background color of the TextView displaying the recipe name
            int textColorResourceId = BakingUtils.getTextBackGroundColor(itemView.getContext(), position);
            mRecipeItemBinding.tvName.setBackgroundColor(textColorResourceId);

        }

        
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = mRecipeList.get(adapterPosition);
            mOnClickHandler.onItemClick(recipe);
        }
    }
}
