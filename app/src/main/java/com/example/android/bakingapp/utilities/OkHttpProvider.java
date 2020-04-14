

package com.example.android.bakingapp.utilities;

import com.example.android.bakingapp.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

import static com.example.android.bakingapp.utilities.Constant.CONNECT_TIMEOUT_TEN;
import static com.example.android.bakingapp.utilities.Constant.READ_TIMEOUT_TWENTY;


public abstract class OkHttpProvider {

    private static OkHttpClient sInstance = null;

    public static OkHttpClient getOkHttpInstance() {
        if (sInstance == null) {
            sInstance = new OkHttpClient();

            Timber.e("Instance class created");
            OkHttpClient.Builder okHttpClientBuilder = sInstance.newBuilder();
            okHttpClientBuilder.connectTimeout(CONNECT_TIMEOUT_TEN, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(READ_TIMEOUT_TWENTY, TimeUnit.SECONDS);

            if (BuildConfig.DEBUG) {
                Timber.e("Debug build");
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpClientBuilder.addInterceptor(interceptor);
            }
        }
        return sInstance;
    }
}
