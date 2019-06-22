package io.temco.guhada.common.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Glide Module for OkHttp Timeout
 * @author Hyeyeon Park
 */
@GlideModule
class TimeOutGlideModule : AppGlideModule() {
    private val CONNECT_TIMEOUT_SECONDS: Long = 5 // glide default 2.5
    private val READ_TIMEOUT_SECONDS: Long = 10


    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        OkHttpClient.Builder().let { builder ->
            builder.connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            builder.readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            builder.build().let { okHttpClient ->
                registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
            }
        }
    }
}