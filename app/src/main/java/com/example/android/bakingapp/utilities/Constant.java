

package com.example.android.bakingapp.utilities;

public final class Constant {

    private Constant() {
        // Restrict instantiation
    }

    
    static final String BAKING_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    
    public static final String EXTRA_RECIPE = "recipe";

    
    public static final String EXTRA_STEP_INDEX = "step_index";

    
    public static final String SAVE_STEP = "save_step";
    public static final String STATE_STEP_INDEX = "state_step_index";
    public static final String STATE_PLAYBACK_POSITION = "state_playback_position";
    public static final String STATE_CURRENT_WINDOW = "state_current_window";
    public static final String STATE_PLAY_WHEN_READY = "state_play_when_ready";

    
    public static final String BAKING_NOTIFICATION_CHANNEL_ID = "baking_notification_channel_id";
    
    public static final int BAKING_PENDING_INTENT_ID = 0;
    
    public static final int BAKING_NOTIFICATION_ID = 20;

    
    public static final float PLAYER_PLAYBACK_SPEED = 1f;
    public static final int REWIND_INCREMENT = 3000;
    public static final int FAST_FORWARD_INCREMENT = 3000;
    public static final int START_POSITION = 0;

    
    public static final String DATABASE_NAME = "recipe";

    
    public static final int NUMBER_OF_THREADS_THREE = 3;

    
    public static final String[] TAP_TITLE = new String[] {"Ingredients", "Steps"};
    
    public static final int INGREDIENTS = 0;
    public static final int STEPS = 1;
    
    public static final int PAGE_COUNT = TAP_TITLE.length;

    
    public static final int GRID_COLUMN_WIDTH = 520;
    
    public static final int GRID_COLUMN_WIDTH_DEFAULT = 48;
    
    public static final int GRID_SPAN_COUNT = 1;

    
    public static final int POSITION_ZERO = 0;
    public static final int POSITION_ONE = 1;
    public static final int POSITION_TWO = 2;
    static final int POSITION_THREE = 3;
    static final int NUM_POSITION_FOUR = 4;

    
    public static final int RECIPE_IMAGE_PADDING = 140;

    
    static final int CONNECT_TIMEOUT_TEN = 10;
    static final int READ_TIMEOUT_TWENTY = 20;

    
    public static final String NAME_OKHTTP= "OkHttp";

    
    public static final String RECIPE_NAME_AT_ZERO = "Nutella Pie";
    public static final String RECIPE_NAME_AT_ONE = "Brownies";

    
    public static final String DEFAULT_STRING = "";
    public static final int DEFAULT_INTEGER = 1;
    public static final int DEFAULT_INTEGER_FOR_SERVINGS = 8;

    
    public static final int WIDGET_PENDING_INTENT_ID = 0;
}
