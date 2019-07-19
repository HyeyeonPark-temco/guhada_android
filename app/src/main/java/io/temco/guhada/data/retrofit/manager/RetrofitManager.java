package io.temco.guhada.data.retrofit.manager;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import io.temco.guhada.BuildConfig;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    // -------- LOCAL VALUE --------
    private static RetrofitManager instance;
    private Retrofit mManager;
    private Type.Server mCurrentType;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    private RetrofitManager(Type.Server type) {
        mCurrentType = type;
        mManager = getRetrofit(getClient(
                getCache(BaseApplication.getInstance()),
                getInterceptor(), false));
    }

    /**
     * RetrofitManager constructor
     *
     * @param type
     * @param isLogged 로그 여부
     */
    private RetrofitManager(Type.Server type, boolean isLogged) {
        mCurrentType = type;
        mManager = getRetrofit(getClient(
                getCache(BaseApplication.getInstance()),
                getInterceptor(), isLogged));
    }


    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public static <S> S createService(Type.Server type, Class<S> service) {
        if (instance == null) {
            instance = new RetrofitManager(type);
        } else {
            if (type != instance.mCurrentType) {
                instance = new RetrofitManager(type);
            }
        }
        return instance.mManager.create(service);
    }

    /**
     * createService method
     *
     * @param type
     * @param service
     * @param isLogged 로그 여부
     * @param <S>
     * @return
     */
    public static <S> S createService(Type.Server type, Class<S> service, boolean isLogged) {
        if (instance == null) {
            instance = new RetrofitManager(type, isLogged);
        } else {
            if (type != instance.mCurrentType) {
                instance = new RetrofitManager(type, isLogged);
            }
        }
        return instance.mManager.create(service);
    }



    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Retrofit getRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Type.Server.getUrl(mCurrentType))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private static Converter.Factory createGsonConverter(java.lang.reflect.Type type, Object typeAdapter) {
        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("HomeListRepository GetBaseModelDeserializer","createGsonConverter");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);
    }

    private Retrofit getRetrofitCommon(OkHttpClient okHttpClient, java.lang.reflect.Type type, Object typeAdapter) {
        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("HomeListRepository GetBaseModelDeserializer","getRetrofitCommon");
        return new Retrofit.Builder()
                .baseUrl(Type.Server.getUrl(mCurrentType))
                //.addConverterFactory(createGsonConverter(type, typeAdapter))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    /**
     * @author park jungho
     * BaseResponseInterceptor 추가
     */
    // Client
    private OkHttpClient getClient(Cache cache, Interceptor interceptor, boolean isLogging) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);
        builder.connectTimeout(5, TimeUnit.SECONDS);
        // builder.writeTimeout(15, TimeUnit.SECONDS)
        // builder.readTimeout(15, TimeUnit.SECONDS)
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new BaseResponseInterceptor());
        if (isLogging) builder.addInterceptor(getLoggingInterceptor());
        return builder.build();
    }

    /*// Client
    private OkHttpClient getClientCommon(Cache cache, Interceptor interceptor, boolean isLogging) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
         builder.cache(cache);
         builder.connectTimeout(5, TimeUnit.SECONDS);
        // builder.writeTimeout(15, TimeUnit.SECONDS)
        // builder.readTimeout(15, TimeUnit.SECONDS)
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new BaseResponseInterceptor(true));
        if (isLogging) builder.addInterceptor(getLoggingInterceptor());
        return builder.build();
    }*/

    // Cache
    private Cache getCache(Application application) {
        return new Cache(application.getCacheDir(), 10 * 1024 * 1024); // 10MB
    }

    // Interceptor
    private Interceptor getInterceptor() {
        return chain -> {
            final Request.Builder builder = chain.request().newBuilder();
            // Common
            builder.header("X-Guhada-accessTime", String.valueOf(System.currentTimeMillis())); // Time
            builder.header("X-Guhada-country", CommonUtil.getSystemCountryCode()); // Country
            builder.header("X-Guhada-language", Preferences.getLanguage()); // Language
            builder.header("X-Guhada-platform", "AOS"); // Platform
            builder.header("X-Guhada-version", BuildConfig.VERSION_NAME); // Version
            // Mobile
            builder.header("X-Guhada-model", Build.DEVICE); // Device Model // Build.MANUFACTURER
            builder.header("X-Guhada-os-version", Build.VERSION.RELEASE); // Device Version
            Log.d("Interceptor","getInterceptor "+CommonUtil.getSystemCountryCode());
            Log.d("Interceptor","getInterceptor "+Preferences.getLanguage());
            return chain.proceed(builder.build());
        };
    }

    // Logging
    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    ////////////////////////////////////////////////
}