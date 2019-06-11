package io.temco.guhada.data.server

import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginServer {
    companion object {

        /**
         * 네이버 유저 프로필 가져오기 API
         */
        @JvmStatic
        fun getNaverProfile(listener: OnServerListener, accessToken: String) {
            val NAVER_API_SUCCESS = "00"
            val call = RetrofitManager.createService(Type.Server.NAVER_PROFILE, LoginService::class.java).getNaverProfile("Bearer $accessToken")
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<NaverResponse> {
                override fun onResponse(call: Call<NaverResponse>, response: Response<NaverResponse>) {
                    if (response.isSuccessful) {
                        val naverResponse = response.body()
                        if (naverResponse != null && naverResponse.resultCode != null) {
                            if (naverResponse.resultCode == NAVER_API_SUCCESS) {
                                listener.onResult(true, naverResponse.user)
                            }
                        } else {
                            listener.onResult(false, response.message())
                        }
                    } else {
                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<NaverResponse>, t: Throwable) {
                    CommonUtil.debug("[NAVER] FAILED: " + t.message)
                }
            })
        }

        /**
         * 회원가입 API
         */
        @JvmStatic
        fun signUp(listener: OnServerListener, user: User) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).signUp(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    if (response.isSuccessful) {
                        listener.onResult(true, response.body())
                    } else {
                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 이메일 로그인 API
         */
        @JvmStatic
        fun signIn(listener: OnServerListener, user: User) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).signIn(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, 1, object : Callback<BaseModel<Token>> {
                override fun onResponse(call: Call<BaseModel<Token>>, response: Response<BaseModel<Token>>) {
                    if (response.isSuccessful) {
                        listener.onResult(true, response.body())
                    } else {
                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel<Token>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 유저 정보 가져오기 API
         */
        @JvmStatic
        fun findUserId(listener: OnServerListener, name: String, phoneNumber: String) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).findUserId(name, phoneNumber)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<User>> {
                override fun onResponse(call: Call<BaseModel<User>>, response: Response<BaseModel<User>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<User>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 이메일로 인증번호 전송하기 API
         */
        @JvmStatic
        fun verifyEmail(listener: OnServerListener, user: User) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).verifyEmail(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 핸드폰 번호로 인증번호 전송하기 API
         */
        @JvmStatic
        fun verifyPhone(listener: OnServerListener, user: User) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).verifyPhone(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 인증번호 검증 API
         */
        @JvmStatic
        fun verifyNumber(listener: OnServerListener, verification: Verification) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).verifyNumber(verification)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 인증번호로 비밀번호 재설정하기 API
         */
        @JvmStatic
        fun changePassword(listener: OnServerListener, verification: Verification) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).changePassword(verification)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 본인인증으로 비밀번호 재설정하기 API
         */
        @JvmStatic
        fun changePasswordByIdentifying(listener: OnServerListener, verification: Verification) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java, true).changePasswordByIdentifying(verification)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 나이스 본인인증 호출 토큰 가져오기 API
         */
        @JvmStatic
        fun getVerifyPhoneToken(listener: OnServerListener) {
            val call = RetrofitManager.createService(Type.Server.LOCAL, LoginService::class.java).verifyToken
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<String>> {
                override fun onResponse(call: Call<BaseModel<String>>, response: Response<BaseModel<String>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<String>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 중복된 이메일인지 검증 API
         */
        @JvmStatic
        fun checkEmail(listener: OnServerListener, email: String) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).checkEmail(email)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /***
         * 중복된 핸드폰 번호인지 검증 API
         */
        @JvmStatic
        fun checkPhone(listener: OnServerListener, phoneNumber: String) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).checkPhone(phoneNumber)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 구글 로그인 API
         */
        @JvmStatic
        fun googleLogin(listener: OnServerListener, user: SnsUser) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).googleLogin(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Token>> {
                override fun onResponse(call: Call<BaseModel<Token>>, response: Response<BaseModel<Token>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        listener.onResult(true, result)
                    } else {

                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel<Token>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 네이버 로그인 API
         */
        @JvmStatic
        fun naverLogin(user: SnsUser, listener: OnServerListener) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).naverLogin(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Token>> {
                override fun onResponse(call: Call<BaseModel<Token>>, response: Response<BaseModel<Token>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        listener.onResult(true, result)
                    } else {
                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel<Token>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 카카오톡 로그인 API
         */
        @JvmStatic
        fun kakaoLogin(user: SnsUser, listener: OnServerListener) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).kakaoLogin(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Token>> {
                override fun onResponse(call: Call<BaseModel<Token>>, response: Response<BaseModel<Token>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        listener.onResult(true, result)
                    } else {
                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel<Token>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 페이스북 로그인 API
         */
        @JvmStatic
        fun facebookLogin(listener: OnServerListener, user: SnsUser) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).facebookLogin(user)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Token>> {
                override fun onResponse(call: Call<BaseModel<Token>>, response: Response<BaseModel<Token>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        listener.onResult(true, result)
                    } else {
                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<BaseModel<Token>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }
    }
}