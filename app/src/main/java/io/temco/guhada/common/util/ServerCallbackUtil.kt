package io.temco.guhada.common.util

import com.google.gson.Gson
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.flag.ResultCode
import io.temco.guhada.data.model.base.BaseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Retrofit2 Response 관련 Util
 * @author Hyeyeon Park
 */
class ServerCallbackUtil {
    /**
     * Common Retrofit2 Response Callback
     * @param successTask (required) API 호출 성공 시 수행할 메서드
     * @param failedTask (optional) API 호출 실패 시 수행할 메서드
     * @param failedMessage (optional)  API 호출 실패 시 보여줄 메시지
     * @author Hyeyeon Park
     */
    open class ServerResponseCallback<T>() : Callback<T> {
        private lateinit var failedTask: (Throwable) -> Unit
        private lateinit var successTask: (Response<T>) -> Unit
        private var failedMessage = BaseApplication.getInstance().getString(R.string.common_message_error)

        constructor(successTask: (Response<T>) -> Unit) : this() {
            this.successTask = successTask
        }

        constructor(successTask: (Response<T>) -> Unit, failedMessage: String) : this() {
            this.failedMessage = failedMessage
            this.successTask = successTask
        }

        constructor(successTask: (Response<T>) -> Unit, failedTask: (Throwable) -> Unit) : this() {
            this.successTask = successTask
            this.failedTask = failedTask
        }

        constructor(successTask: (Response<T>) -> Unit, failedTask: (Throwable) -> Unit, failedMessage: String) : this() {
            this.failedMessage = failedMessage
            this.successTask = successTask
            this.failedTask = failedTask
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            CommonUtil.debug(t.message ?: failedMessage)
            if (::failedTask.isInitialized) failedTask(t)
         //   else ToastUtil.showMessage(t.message ?: failedMessage)
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

    companion object {
        /**
         * Retrofit2 resultCode 별 task 분기
         * @author Hyeyeon Park
         *
         * 19.07.18
         * @author park jungho
         * 에러 처리 관련 수정
         */
        fun executeByResultCode(success: Boolean, o: Any,
                                successTask: (BaseModel<*>) -> Unit,
                                failedTask: (BaseModel<*>) -> Unit = {
                                    CommonUtil.debug(o as String)
                                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_error))
                                },
                                dataNotFoundTask: () -> Unit = {},
                                productNotFoundTask: (BaseModel<*>) -> Unit = {}) {
            if (success) {
                val model = o as BaseModel<*>
                when (model.resultCode) {
                    ResultCode.SUCCESS.flag -> successTask(model)
                    ResultCode.DATA_NOT_FOUND.flag -> dataNotFoundTask()
                    ResultCode.PRODUCT_RESOURCE_NOT_FOUND.flag -> productNotFoundTask(model)
                }
            } else {
                // modify ------------------------------------
                if (o is String) {
                    var gson = Gson()
                    if (CustomLog.flag) CustomLog.L("executeByResultCode", o)
                    var base = gson.fromJson<BaseModel<*>>(o, BaseModel::class.java)
                    failedTask(base)
                } else {
                    var base = BaseModel<Any>()
                    base.error = ""
                    failedTask(base)
                }
                //  -------------------------------------------
            }
        }


        /**
         * API 호출 시, AccessToken이 필요한 경우 호출
         * @param task (required) AccessToken이 존재하는 경우의 실행 함수
         * @param invalidTokenTask (optional) AccessToken이 존재하지 않는 경우의 실행 함수
         *
         * @description 토큰 expire 시, invalidTokenTask 수행 로직 추가 예정
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun callWithToken(task: (accessToken: String) -> Unit, invalidTokenTask: () -> Unit = {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
        }) {
            Preferences.getToken().let { token ->
                if (token != null && token.accessToken != null) task("Bearer ${token.accessToken}")
                else invalidTokenTask()
            }
        }


    }


}