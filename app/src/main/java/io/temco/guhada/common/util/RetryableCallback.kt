package io.temco.guhada.common.util

import android.util.Log
import io.temco.guhada.common.Preferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

/**
 *  Custom Retrofit Retryable Callback
 *  [임시] 토큰 만료 시 1회 재요청
 *  @param call Retrofit Call<T>
 *  @param recall Retrofit Call<T> 실패 시 호출할 call (optional)
 *  @param totalRetries 재시도 횟수
 */
abstract class RetryableCallback<T>(private var call: Call<T>) : Callback<T> {
    private var retryCount = 0
    private var totalRetries = 1
    private lateinit var recall: Call<T>

    constructor(call: Call<T>, totalRetries: Int) : this(call) {
        this.call = call
        this.totalRetries = totalRetries
    }

    constructor(call: Call<T>, recall: Call<T>, totalRetries: Int) : this(call) {
        this.call = call
        this.recall = recall
        this.totalRetries = totalRetries
    }


    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (!APIHelper.isCallSuccess(response = response)) {
            if (response.code() == 401 || response.code() == 403) {
                Log.e("RETRYING-onResponse", "$retryCount/$totalRetries")
                if (retryCount++ < totalRetries) {
                    retry()
                } else {
                    Preferences.clearToken()
                    if (::recall.isInitialized) {
                        recall.clone().enqueue(this)
                    }
                }
            } else {
                this@RetryableCallback.onFinalResponse(call, response)
            }
        } else {
            this@RetryableCallback.onFinalResponse(call, response)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        try {
            val errorCode = (t as HttpException).code()
            if (errorCode == 401 || errorCode == 403) {
                if (retryCount++ < totalRetries) {
                    Log.e("RETRYING-onFailure", "$retryCount/$totalRetries")
                    retry()
                } else {
                    Preferences.clearToken()
                    if (::recall.isInitialized) {
                        recall.clone().enqueue(this)
                    }
                }
            }
        } catch (e: Exception) {
            CommonUtil.debug(e.message)
        }
    }


    open fun onFinalResponse(call: Call<T>, response: Response<T>) {
        Log.e("onFinalResponse", "")
//        CommonUtil.debug("onFinalResponse-토큰 만료")
//        Preferences.clearToken()
//        retry()

        // 테스트 필요
//            BaseApplication.getInstance().startActivity(Intent(BaseApplication.getInstance(), LoginActivity::class.java))
    }

    open fun onFinalFailure(call: Call<T>, t: Throwable) {
        try {
            // val errorCode = (t as HttpException).code()
            Log.e("onFinalFailure", "")
//            CommonUtil.debug("onFinalFailure-토큰 만료")
//            Preferences.clearToken()
//            retry()
            // 테스트 필요
//                BaseApplication.getInstance().startActivity(Intent(BaseApplication.getInstance(), LoginActivity::class.java))

        } catch (e: Exception) {
            CommonUtil.debug(e.message)
        }
    }

    private fun retry() = call.clone().enqueue(this)

    sealed class APIHelper {
        companion object {
            private val DEFAULT_RETRIES = 1

            @JvmStatic
            fun <T> enqueueWithRetry(call: Call<T>, retryCount: Int, callback: Callback<T>) {
                call.enqueue(object : RetryableCallback<T>(call, retryCount) {
                    override fun onFinalResponse(call: Call<T>, response: Response<T>) = callback.onResponse(call, response)
                    override fun onFinalFailure(call: Call<T>, t: Throwable) = callback.onFailure(call, t)
                })
            }

            @JvmStatic
            fun <T> enqueueWithRetry(call: Call<T>, recall: Call<T>, retryCount: Int, callback: Callback<T>) {
                call.enqueue(object : RetryableCallback<T>(call, recall, retryCount) {
                    override fun onFinalResponse(call: Call<T>, response: Response<T>) = callback.onResponse(call, response)
                    override fun onFinalFailure(call: Call<T>, t: Throwable) = callback.onFailure(call, t)
                })
            }


            @JvmStatic
            fun <T> enqueueWithRetry(call: Call<T>, callback: Callback<T>) = enqueueWithRetry(call, DEFAULT_RETRIES, callback)

            @JvmStatic
            fun <T> isCallSuccess(response: Response<T>): Boolean = response.code() in 200 until 400

        }
    }
}