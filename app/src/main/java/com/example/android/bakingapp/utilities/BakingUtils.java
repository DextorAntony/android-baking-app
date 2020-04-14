

package com.example.android.bakingapp.utilities;

import android.content.Context;
import androidx.core.content.ContextCompat;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static com.example.android.bakingapp.utilities.Constant.NUM_POSITION_FOUR;
import static com.example.android.bakingapp.utilities.Constant.POSITION_ONE;
import static com.example.android.bakingapp.utilities.Constant.POSITION_THREE;
import static com.example.android.bakingapp.utilities.Constant.POSITION_TWO;
import static com.example.android.bakingapp.utilities.Constant.POSITION_ZERO;

public class BakingUtils {

    
    public static int getImageBackGroundColor(Context context, int position) {
        int imageColorResourceId;

        switch (position % NUM_POSITION_FOUR) {
            case POSITION_ZERO:
                imageColorResourceId = R.color.image_mint;
                break;
            case POSITION_ONE:
                imageColorResourceId = R.color.image_light_green;
                break;
            case POSITION_TWO:
                imageColorResourceId = R.color.image_yellow;
                break;
            case POSITION_THREE:
                imageColorResourceId = R.color.image_pink;
                break;
            default:
                imageColorResourceId = R.color.image_light_blue;
                break;
        }
        return ContextCompat.getColor(context, imageColorResourceId);
    }

    
    public static int getTextBackGroundColor(Context context, int position) {
        int textColorResourceId;
        switch (position % NUM_POSITION_FOUR) {
            case POSITION_ZERO:
                textColorResourceId = R.color.text_mint;
                break;
            case POSITION_ONE:
                textColorResourceId = R.color.text_light_green;
                break;
            case POSITION_TWO:
                textColorResourceId = R.color.text_yellow;
                break;
            case POSITION_THREE:
                textColorResourceId = R.color.text_pink;
                break;
            default:
                textColorResourceId = R.color.text_light_blue;
                break;
        }
        return ContextCompat.getColor(context, textColorResourceId);
    }

    
    public static int getImageResource(int position) {
        int imageResourceId;
        switch (position % NUM_POSITION_FOUR) {
            case POSITION_ZERO:
                imageResourceId = R.drawable.pie;
                break;
            case POSITION_ONE:
                imageResourceId = R.drawable.cake_with_berries;
                break;
            case POSITION_TWO:
                imageResourceId = R.drawable.valentine_cake;
                break;
            case POSITION_THREE:
                imageResourceId = R.drawable.cake_with_cherry;
                break;
            default:
                imageResourceId = R.drawable.cake_with_cherry;
                break;
        }
        return imageResourceId;
    }

    
    public static List<Ingredient> toIngredientList(String ingredientString) {
        if (ingredientString == null) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Ingredient>>() {}.getType();
        return gson.fromJson(ingredientString, listType);
    }

    
    public static String toIngredientString(List<Ingredient> ingredientList) {
        if (ingredientList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Ingredient>>() {}.getType();
        return gson.toJson(ingredientList, listType);
    }

    
    public static List<Step> toStepList(String stepString) {
        if (stepString == null) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        Type stepListType = new TypeToken<List<Step>>() {}.getType();
        return gson.fromJson(stepString, stepListType);
    }

    
    public static String toStepString(List<Step> stepList) {
        if (stepList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type stepListType = new TypeToken<List<Step>>() {}.getType();
        return gson.toJson(stepList, stepListType);
    }

}
