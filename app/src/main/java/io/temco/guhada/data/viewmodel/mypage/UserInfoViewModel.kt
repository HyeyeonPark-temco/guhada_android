package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.BankAccount
import io.temco.guhada.data.model.RefundRequest
import io.temco.guhada.data.model.Verification
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.model.user.UserSize
import io.temco.guhada.data.model.user.UserUpdateInfo
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import java.util.*

/**
 * 19.07.22
 * @author park jungho
 *
 * 회원정보수정
- 앱을 실행하여 인트로에서 관련 플래그를 리셋하고 리셋된경우 해당탭에 진입했을때 비밀번호 입력화면을 넣고 입력을 한번 해야 내부 정보를 볼 수 있도록
- 앱을 실행하여 해당 비밀번호를 입력을 한번 하면 앱을 다시 껏다 키지 않는한 회원 정보를 계속 볼 수 있다
- 만약 회원정보수정화면에서 비밀번호를 입력하고 앱을 끄고 다시 진입하면 로그인을 다시 해야 한다
- 웹 프론트 작업 된 내용 공유
○ 내 사이즈 수정 // /users/user-size
○ 비밀번호 1회 재입력은 로그인 API를 한번 더 호출하는 방식으로 처리되어있음
 *
 */
class UserInfoViewModel(val context: Context) : BaseObservableViewModel(), Observer {
    val repository: UserInfoRepository = UserInfoRepository(this)

    var oldNickname = ""
    var userId: Long = 0L
    var defaultSnsType = ""
    var userEmail = ""
    var mUser: MutableLiveData<User> = MutableLiveData()
    var mVerifyNumber = ""
    var mVerification: Verification = Verification()

    // 환불계좌 입력 여부
    var accountVerifiedIdentity = false

    var mUserSize = UserSize()

    // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
    var mypageUserInfoLoginCheckType = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageUserInfoLoginCheckType)
        }


    // 유저 닉네임 중복 체크

    var isNickNameFocus = false
    var mIsNicknameValid = ObservableBoolean(true)
        @Bindable
        get() = field
    var mNickNameCheckIconVisible = ObservableBoolean(false)
        @Bindable
        get() = field
    var mNickNameBg = ObservableInt(BaseApplication.getInstance().resources.getColor(R.color.white_four))
        @Bindable
        get() = field

    var checkGenderValue = ObservableField<String>("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.checkGenderValue)
        }

    var userBankSpinnerArrow = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userBankSpinnerArrow)
        }

    // 환불 계좌 정보
    var mRefundBanks = MutableLiveData<MutableList<PurchaseOrder.Bank>>()
    var mBankAccount = MutableLiveData<BankAccount>()
    var mIsCheckAccountAvailable = ObservableBoolean(true)
    var mRefundRequest = RefundRequest()
    var mBankNumInputAvailableTask: () -> Unit = {}

    fun userLoginType(listener: OnCallBackListener) {
        repository.checkSnsUserType(listener)
    }

    fun userCheck(listener: OnCallBackListener) {
        repository.userData(listener)
        this.mRefundRequest.addObserver(this)
    }

    fun clickGenderButton(value: String) {
        if (CustomLog.flag) CustomLog.L("clickGenderButton", "value", value)
        checkGenderValue.set(value)
        mUser.value!!.userGender = value
    }

    fun getUserByNickName(nickname: String, listener: OnCallBackListener?) {
        isNickNameFocus = false
        UserServer.getUserByNickName(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        if (nickname == oldNickname) {
                            mNickNameBg = ObservableInt(BaseApplication.getInstance().resources.getColor(R.color.common_blue_purple))
                            mNickNameCheckIconVisible = ObservableBoolean(true)
                            mIsNicknameValid = ObservableBoolean(true)
                            mUser.value!!.nickname = nickname
                            notifyPropertyChanged(BR.mIsNicknameValid)
                            notifyPropertyChanged(BR.mNickNameCheckIconVisible)
                            notifyPropertyChanged(BR.mNickNameBg)
                            listener?.callBackListener(true, "dataNotFoundTask")
                        } else {
                            mNickNameBg = ObservableInt(BaseApplication.getInstance().resources.getColor(R.color.brick))
                            mNickNameCheckIconVisible = ObservableBoolean(true)
                            mIsNicknameValid = ObservableBoolean(false)
                            mUser.value!!.nickname = oldNickname
                            notifyPropertyChanged(BR.mIsNicknameValid)
                            notifyPropertyChanged(BR.mNickNameCheckIconVisible)
                            notifyPropertyChanged(BR.mNickNameBg)
                            listener?.callBackListener(false, "successTask")
                        }
                    },
                    dataNotFoundTask = {
                        mNickNameBg = ObservableInt(BaseApplication.getInstance().resources.getColor(R.color.common_blue_purple))
                        mNickNameCheckIconVisible = ObservableBoolean(true)
                        mIsNicknameValid = ObservableBoolean(true)
                        mUser.value!!.nickname = nickname
                        notifyPropertyChanged(BR.mIsNicknameValid)
                        notifyPropertyChanged(BR.mNickNameCheckIconVisible)
                        notifyPropertyChanged(BR.mNickNameBg)
                        listener?.callBackListener(true, "dataNotFoundTask")
                    })
        }, nickName = nickname)
    }

    fun getRefundBanks() {
        UserServer.getBanks(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        mRefundBanks.postValue(it.data as MutableList<PurchaseOrder.Bank>)
                    })
        })
    }

    fun checkAccount() = when {
        mRefundRequest.refundBankCode.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_emptybankcode))
        mRefundRequest.refundBankAccountNumber.isEmpty() -> ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_emptybanknumber))
        else -> BankAccount().apply {
            this.bankNumber = mRefundRequest.refundBankAccountNumber
            this.bankCode = mRefundRequest.refundBankCode
            // 이름 추가
            //this.name = mUser.value?.userDetail?.verifiedName ?: ""
        }.let {
            if (mIsCheckAccountAvailable.get()) {
                OrderServer.checkAccount(OnServerListener { success, o ->
                    if (success) {
                        val bankAccount = (o as BaseModel<BankAccount>).data
                        if (bankAccount.result) {
                            accountVerifiedIdentity = true
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_succesbankaccount))
                            mBankAccount.postValue(bankAccount)

                            mRefundRequest.refundBankCode = bankAccount.bankCode
                            mRefundRequest.refundBankAccountNumber = bankAccount.bankNumber
                            mRefundRequest.refundBankAccountOwner = bankAccount.name

                            mUser.value!!.userDetail.bankCode = bankAccount.bankCode
                            mUser.value!!.userDetail.bankName = bankAccount.name
                            mUser.value!!.userDetail.accountNumber = bankAccount.bankNumber

                            mIsCheckAccountAvailable = ObservableBoolean(false)
                            notifyPropertyChanged(BR.mIsCheckAccountAvailable)
                            mBankNumInputAvailableTask()
                        } else {
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.requestorderstatus_refund_message_invalidbankaccount))
                        }
                    }
                }, bankAccount = it)
            }

        }
    }

    override fun update(o: Observable?, arg: Any?) {
        if (arg is String) {
            if (arg == "bankNumber") {
                mIsCheckAccountAvailable = ObservableBoolean(true)
                notifyPropertyChanged(BR.mIsCheckAccountAvailable)
                mBankNumInputAvailableTask()
            }
        }
    }


    // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
    fun setSnsType() {
        when (defaultSnsType) {
            "EMAIL" -> mypageUserInfoLoginCheckType.set(0)
            "NAVER" -> mypageUserInfoLoginCheckType.set(1)
            "KAKAO" -> mypageUserInfoLoginCheckType.set(2)
            "FACEBOOK" -> mypageUserInfoLoginCheckType.set(3)
            "GOOGLE" -> mypageUserInfoLoginCheckType.set(4)
        }
    }

    fun updateEmail(email: String, verificationNumber: String, successTask: () -> Unit) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("verificationNumber", verificationNumber)
            jsonObject.addProperty("email", email)

            UserServer.updateEmailVerify(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                    userEmail = email
                    mUser.value?.email = email
                    Preferences.setSavedId(email)
                    successTask()
                })
            }, accessToken = accessToken, jsonObject = jsonObject)
        })
    }

    /**
     * 본인인증 정보 업데이트
     */
    fun updateIdentityVerify() {
        ServerCallbackUtil.callWithToken(task = {
            UserServer.updateIdentityVerify(OnServerListener { success, o ->
                val resultCode = (o as BaseModel<*>).resultCode
                if (resultCode == ResultCode.SUCCESS.flag) {

                } else {
                    ToastUtil.showMessage(o.message ?: "유저 정보 업데이트 오류")
                }
            }, accessToken = it, verification = mVerification)
        })
    }

    /**
     * 회원탈퇴
     * @author Hyeyeon Park
     * @since 2019.11.21
     */
    fun withdraw(successTask: () -> Unit) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            UserServer.withdraw(OnServerListener { success, o ->
                if (success && o is BaseModel<*>) {
                    if (o.resultCode == ResultCode.SUCCESS.flag) {
                        successTask()
                    } else {
                        val message = if (o.message.isNullOrEmpty()) BaseApplication.getInstance().getString(R.string.common_message_servererror) else o.message
                        ToastUtil.showMessage(message)
                    }
                }
            }, accessToken = accessToken)
        })
    }
}


class UserInfoRepository(val mViewModel: UserInfoViewModel) {

    fun checkSnsUserType(listener: OnCallBackListener) {
        UserServer.checkSnsUserType(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<Any>).list
                        if (CustomLog.flag) CustomLog.L("userLoginTypeCheck value", value)
                        mViewModel.defaultSnsType = value[0] as String
                        mViewModel.setSnsType()
                        listener.callBackListener(true, value[0])
                    },
                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = {
                        mViewModel.defaultSnsType = "EMAIL"
                        mViewModel.setSnsType()
                        listener.callBackListener(true, "EMAIL")
                    }
            )
        }, mViewModel.userId)
    }


    fun userData(listener: OnCallBackListener) {
        UserServer.findUsers(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<*>).list as List<User>
                        if (CustomLog.flag) CustomLog.L("userData value", value)
                        if (!value.isNullOrEmpty() && value.size > 0) {
                            mViewModel.mUser.value = value[0]
                            mViewModel.userEmail = value[0].email
                            mViewModel.oldNickname = value[0].nickname
                            listener.callBackListener(true, value[0])
                        }
                    },
                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = { listener.callBackListener(false, it) }
            )
        }, mViewModel.userId.toInt())
    }


    fun getUserSize(listener: OnCallBackListener) {
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        UserServer.getUserSize(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var data = (o as BaseModel<*>).data as UserSize
                                        mViewModel.mUserSize = data
                                        listener.callBackListener(true, data)
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "") },
                                    failedTask = { listener.callBackListener(false, "") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "") },
                                    dataIsNull = { listener.callBackListener(false, "") }
                            )
                        }, accessToken = it)
                    }
                }, invalidTokenTask = { })
    }


    fun updateUserInfo(info: UserUpdateInfo, listener: OnCallBackListener) {
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        UserServer.updateUserInfo(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var data = (o as BaseModel<*>).data
                                        if (CustomLog.flag) CustomLog.L("updateUserInfo", "data", data)
                                        listener.callBackListener(true, data)
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") },
                                    dataIsNull = { listener.callBackListener(false, "dataIsNull") }
                            )
                        }, accessToken = it, userId = mViewModel.userId, userInfo = info)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }
}