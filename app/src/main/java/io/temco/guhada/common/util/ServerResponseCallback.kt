package io.temco.guhada.common.util

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Common Retrofit2 Response Callback
 * @author Hyeyeon Park
 */
open class ServerResponseCallback<T>() : Callback<T> {
    private lateinit var failedTask: (Throwable) -> Unit
    private lateinit var successTask: (Response<T>) -> Unit
    private var failedMessage = BaseApplication.getInstance().getString(R.string.common_message_error)

    constructor(successTask: (Response<T>) -> Unit) : this() {
        this.successTask = successTask
    }

    constructor(failedMessage: String, successTask: (Response<T>) -> Unit) : this() {
        this.failedMessage = failedMessage
        this.successTask = successTask
    }

    constructor(failedMessage: String, successTask: (Response<T>) -> Unit, failedTask: (Throwable) -> Unit) : this() {
        this.failedMessage = failedMessage
        this.successTask = successTask
        this.failedTask = failedTask
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        CommonUtil.debug(t.message ?: failedMessage)
        if (::failedTask.isInitialized) failedTask(t)
        else ToastUtil.showMessage(t.message ?: failedMessage)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            successTask(response)
        } else {
            CommonUtil.debug("RESPONSE CODE: [${response.code()}]")
            ToastUtil.showMessage(failedMessage)
        }
    }
}