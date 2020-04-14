

package com.example.android.bakingapp.utilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.bakingapp.utilities.Constant.BAKING_BASE_URL;


public class RetrofitClient {

    
    private static Retrofit sRetrofit = null;

    public static Retrofit getClient() {
        if (sRetrofit == null) {

            // Create the Retrofit instance using the builder
            sRetrofit = new Retrofit.Builder()
                    .client(OkHttpProvider.getOkHttpInstance())
                    // Set the base URL
                    .baseUrl(BAKING_BASE_URL)
                    // Use GsonConverterFactory class to generate an implementation of the baking interface
                    // which uses Gson for its deserialization
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }
}
