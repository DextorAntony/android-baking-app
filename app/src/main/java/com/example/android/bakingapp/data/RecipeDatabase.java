

package com.example.android.bakingapp.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.android.bakingapp.utilities.Constant;

import timber.log.Timber;



// List of the entry class and associated TypeConverters
@Database(entities = {ShoppingListEntry.class}, version = 1, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static RecipeDatabase sInstance;

    public static RecipeDatabase getInstance(Context context) {
        Timber.d("Getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        RecipeDatabase.class, Constant.DATABASE_NAME).build();
                Timber.d("Made new database");
            }
        }
        return sInstance;
    }

    // The associated DAOs for the database
    public abstract RecipeDao recipeDao();
}
