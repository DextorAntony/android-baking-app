

package com.example.android.bakingapp.ui.detail;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.StepsListItemBinding;
import com.example.android.bakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.util.List;


public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    
    private List<Step> mSteps;

    
    private final StepsAdapterOnClickHandler mOnClickHandler;

    
    public interface StepsAdapterOnClickHandler {
        void onItemClick(int stepIndex);
    }

    
    public StepsAdapter(List<Step> steps, StepsAdapterOnClickHandler onClickHandler) {
        mSteps = steps;
        mOnClickHandler = onClickHandler;
    }

    
    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StepsListItemBinding stepsItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.steps_list_item, parent, false);
        return new StepsViewHolder(stepsItemBinding);
    }

    
    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        Step step = mSteps.get(position);
        holder.bind(step, position);
    }

    
    @Override
    public int getItemCount() {
        if (null == mSteps) return 0;
        return mSteps.size();
    }

    
    public void addAll(List<Step> steps) {
        mSteps.clear();
        mSteps.addAll(steps);
        notifyDataSetChanged();
    }

    
    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        
        private StepsListItemBinding mStepsItemBinding;


        
        public StepsViewHolder(StepsListItemBinding stepsItemBinding) {
            super(stepsItemBinding.getRoot());
            mStepsItemBinding = stepsItemBinding;
            // Call setOnClickListener on the View
            itemView.setOnClickListener(this);
        }

        
        void bind(Step step, int position) {
            // Get the step ID that matches to the step index.
            // (e.g. Step ID of Yellow cake from  8 to 13 does not match to the position)
            int stepId = getCorrectStepId(step, position);
            // Set the step ID
            mStepsItemBinding.tvStepId.setText(String.valueOf(stepId));
            // Set the short description
            mStepsItemBinding.tvStepShortDescription.setText(step.getShortDescription());

            String thumbnailUrl = step.getThumbnailUrl();
            // Check if the thumbnail has a valid URL.
            // (e.g. The Step 5 of Nutella Pie has an "mp4" in the thumbnail URL)
            if (thumbnailUrl.isEmpty() || thumbnailUrl.contains(
                    itemView.getContext().getString(R.string.mp4))) {
                // Hide ImageView thumbnail
                mStepsItemBinding.ivThumbnail.setVisibility(View.GONE);
            } else {
                // If the thumbnail URL exists, make sure ImageView visible and
                // use the Picasso library to upload the thumbnail
                mStepsItemBinding.ivThumbnail.setVisibility(View.VISIBLE);
                Picasso.with(itemView.getContext())
                        .load(thumbnailUrl)
                        .error(R.drawable.recipe_error_image)
                        .placeholder(R.drawable.recipe_error_image)
                        .into(mStepsItemBinding.ivThumbnail);
            }
        }

        
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mOnClickHandler.onItemClick(adapterPosition);
        }

        
        private int getCorrectStepId(Step step, int position) {
            int stepId = step.getStepId();
            // If the step ID does not correspond to the step index, replace step ID with
            // the step index.
            if (stepId != position) {
                stepId = position;
            }
            return stepId;
        }

    }
}
