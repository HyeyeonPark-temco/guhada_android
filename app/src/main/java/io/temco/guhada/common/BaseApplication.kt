package io.temco.guhada.common

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.kakao.auth.KakaoSDK
import io.temco.guhada.common.sns.kakao.KakaoSDKAdapter
import io.temco.guhada.data.di.appModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApplication : Application() {
    companion object {
        lateinit var mApplication: BaseApplication

        @JvmStatic
        fun getInstance(): BaseApplication = this.mApplication
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = this

        // Preference
        Preferences.init(applicationContext)

        // KAKAO
        KakaoSDK.init(KakaoSDKAdapter())

        // FACEBOOK
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        // KOIN
        startKoin { module { appModule } }
    }
}