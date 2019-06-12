package io.temco.guhada.data.retrofit.manager;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import io.temco.guhada.common.Type;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
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
        mManager = getRetrofit();
//        mManager = getRetrofit(getClient(
//                getCache(BaseApplication.getInstance()),
//                getInterceptor()));
    }

    /**
     * RetrofitManager constructor
     *
     * @param type
     * @param isLogged 로그 여부
     */
    private RetrofitManager(Type.Server type, boolean isLogged) {
        mCurrentType = type;
        mManager = getRetrofit(isLogged);
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

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Type.Server.getUrl(mCurrentType))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private Retrofit getRetrofit(boolean isLogged) {
        if (isLogged) {
            return new Retrofit.Builder()
                    .baseUrl(Type.Server.getUrl(mCurrentType))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        } else {
            return new Retrofit.Builder()
                    .baseUrl(Type.Server.getUrl(mCurrentType))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    private Retrofit getRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Type.Server.getUrl(mCurrentType))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private OkHttpClient getClient(Cache cache, Interceptor interceptor) {
        return new OkHttpClient.Builder()
                // .cache(cache)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

    private OkHttpClient getClient() {
        return new OkHttpClient.Builder().addInterceptor(getLoggingInterceptor()).build();
    }

    private Cache getCache(Application application) {
        return new Cache(application.getCacheDir(), 10 * 1024 * 1024); // 10MB
    }

    private Interceptor getInterceptor() {
        return chain -> {
            final Request.Builder builder = chain.request().newBuilder();
            // .header("Accept", "application/json")
            return chain.proceed(builder.build());
        };
    }

    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    ////////////////////////////////////////////////
}
