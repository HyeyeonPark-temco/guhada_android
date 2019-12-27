package io.temco.guhada.data.retrofit.manager

import android.app.Application
import android.os.Build
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import io.temco.guhada.BuildConfig
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitManager() {

    // -------- LOCAL VALUE --------
    private var mManager: Retrofit? = null
    private var mCurrentType: Type.Server? = null
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    constructor (type: Type.Server) : this() {
        mCurrentType = type
        mManager = getRetrofit(getClient(
                getCache(BaseApplication.getInstance()),
                getInterceptor(), false))
    }

    constructor (type: Type.Server, isLogged: Boolean, isParseJson: Boolean) : this() {
        mCurrentType = type
        mManager = getRetrofit(getClient(
                getCache(BaseApplication.getInstance()),
                getInterceptor(), isLogged, isParseJson))
    }


    /**
     * RetrofitManager constructor
     *
     * @param type
     * @param isLogged 로그 여부
     */
    constructor(type: Type.Server, isLogged: Boolean) : this() {
        mCurrentType = type
        mManager = getRetrofit(getClient(
                getCache(BaseApplication.getInstance()),
                getInterceptor(), isLogged))
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    companion object {
        private var instance: RetrofitManager? = null
        private var parseJsonInstance: RetrofitManager? = null

        @JvmStatic
        fun <S> createService(type: Type.Server, service: Class<S>): S {
            if (instance == null) {
                instance = RetrofitManager(type)
            } else {
                if (type != instance!!.mCurrentType) {
                    instance = RetrofitManager(type)
                }
            }
            return instance!!.mManager!!.create(service)
        }

        /**
         * createService method
         *
         * @param type
         * @param service
         * @param isLogged 로그 여부
         * @param <S>
         * @return
        </S> */
        fun <S> createService(type: Type.Server, service: Class<S>, isLogged: Boolean): S {
            if (instance == null) {
                instance = RetrofitManager(type, CustomLog.flag && isLogged)
            } else {
                if (type != instance!!.mCurrentType) {
                    instance = RetrofitManager(type, CustomLog.flag && isLogged)
                }
            }
            return instance!!.mManager!!.create(service)
        }

        /**
         * createService method
         *
         * @param type
         * @param service
         * @param isLogged 로그 여부
         * @param isParseJson BaseResponseInterceptor 추가 여부
         * @param <S>
         * @return
        </S> */
        fun <S> createService(type: Type.Server, service: Class<S>, isLogged: Boolean, isParseJson: Boolean): S {
            if (parseJsonInstance == null) {
                parseJsonInstance = RetrofitManager(type, CustomLog.flag && isLogged, isParseJson)
            } else {
                if (type != parseJsonInstance!!.mCurrentType) {
                    parseJsonInstance = RetrofitManager(type, CustomLog.flag && isLogged, isParseJson)
                }
            }
            return parseJsonInstance!!.mManager!!.create(service)
        }

    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Type.Server.getUrl(mCurrentType!!))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClient)
                .build()
    }

    /**
     * @author park jungho
     * BaseResponseInterceptor 추가
     */
    // Client
    private fun getClient(cache: Cache, interceptor: Interceptor, isLogging: Boolean, isParseJson: Boolean): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.cache(cache)
        builder.connectTimeout(40, TimeUnit.SECONDS)
        // builder.writeTimeout(15, TimeUnit.SECONDS)
        // builder.readTimeout(15, TimeUnit.SECONDS)
        builder.addInterceptor(interceptor)
        if (isParseJson) builder.addInterceptor(BaseResponseInterceptor())
        if (isLogging) builder.addInterceptor(getLoggingInterceptor())
        return builder.build()
    }

    private fun getClient(cache: Cache, interceptor: Interceptor, isLogging: Boolean): OkHttpClient {
        return getClient(cache, interceptor, isLogging, true)
    }

    /*// Client
    private OkHttpClient getClientCommon(Cache cache, Interceptor interceptor, boolean isLogging) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);
        builder.connectTimeout(20, TimeUnit.SECONDS);
        // builder.writeTimeout(15, TimeUnit.SECONDS)
        // builder.readTimeout(15, TimeUnit.SECONDS)
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new BaseResponseInterceptor(true));
        if (isLogging) builder.addInterceptor(getLoggingInterceptor());
        return builder.build();
    }*/

    // Cache
    private fun getCache(application: Application): Cache {
        return Cache(application.cacheDir, (10 * 1024 * 1024).toLong()) // 10MB
    }

    // Interceptor
    private fun getInterceptor(): Interceptor {
        return Interceptor { chain ->
            val builder = chain.request().newBuilder()
            // Common
            builder.header("x-guhada-accesstime", System.currentTimeMillis().toString()) // Time
            builder.header("x-guhada-country", CommonUtil.getSystemCountryCode()) // Country (KR)
            builder.header("x-guhada-language", Preferences.getLanguage()) // Language (ko)
            builder.header("x-guhada-platform", "AOS") // Platform
            builder.header("x-guhada-version", BuildConfig.VERSION_NAME) // Version
            // Mobile
            builder.header("x-guhada-model", Build.DEVICE) // Device Model // Build.MANUFACTURER
            builder.header("x-guhada-os-version", Build.VERSION.RELEASE) // Device Version
            chain.proceed(builder.build())
        }
    }

    // Logging
    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    ////////////////////////////////////////////////
}