package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

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
class MyPageUserInfoViewModel(val context: Context) : BaseObservableViewModel() {
    val repository: MyPageUserInfoRepository = MyPageUserInfoRepository(this)

    var userId: Long = 0L
    var defaultSnsType = "NAVER"
    var userEmail = ""

    var checkPasswordConfirm = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.checkPasswordConfirm)
        }

    // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
    var mypageUserInfoLoginCheckType = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mypageUserInfoLoginCheckType)
        }


    // 유저 닉네임 중복 체크
    var mNickName = ""
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

    fun userCheck(listener: OnCallBackListener) {
        repository.userData(listener)
    }

    fun getUserByNickName() {
        isNickNameFocus = false
        UserServer.getUserByNickName(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        mNickNameBg = ObservableInt(BaseApplication.getInstance().resources.getColor(R.color.brick))
                        mNickNameCheckIconVisible = ObservableBoolean(true)
                        mIsNicknameValid = ObservableBoolean(false)
                        notifyPropertyChanged(BR.mIsNicknameValid)
                        notifyPropertyChanged(BR.mNickNameCheckIconVisible)
                        notifyPropertyChanged(BR.mNickNameBg)
                    },
                    dataNotFoundTask = {
                        mNickNameBg = ObservableInt(BaseApplication.getInstance().resources.getColor(R.color.common_blue_purple))
                        mNickNameCheckIconVisible = ObservableBoolean(true)
                        mIsNicknameValid = ObservableBoolean(true)
                        notifyPropertyChanged(BR.mIsNicknameValid)
                        notifyPropertyChanged(BR.mNickNameCheckIconVisible)
                        notifyPropertyChanged(BR.mNickNameBg)
                    })
        }, nickName = mNickName)
    }
}


class MyPageUserInfoRepository(val mViewModel: MyPageUserInfoViewModel) {

    fun userLoginTypeCheck(listener: OnCallBackListener) {
        UserServer.checkExistSnsUser2(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<Any>).data
                        if (CustomLog.flag) CustomLog.L("userLoginTypeCheck value", it)
                        var typeName = (it as BaseModel<Any>).result.split("_")[0]
                        if (CustomLog.flag) CustomLog.L("userLoginTypeCheck value", "typeName", typeName)
                        if ("email".equals(typeName, ignoreCase = true)) {
                            mViewModel.mypageUserInfoLoginCheckType.set(0)
                        } else if ("naver".equals(typeName, ignoreCase = true)) {
                            mViewModel.mypageUserInfoLoginCheckType.set(1)
                        } else if ("kakao".equals(typeName, ignoreCase = true)) {
                            mViewModel.mypageUserInfoLoginCheckType.set(2)
                        } else if ("facebook".equals(typeName, ignoreCase = true)) {
                            mViewModel.mypageUserInfoLoginCheckType.set(3)
                        } else if ("google".equals(typeName, ignoreCase = true)) {
                            mViewModel.mypageUserInfoLoginCheckType.set(4)
                        }
                        listener.callBackListener(true, value)
                    },
                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = { listener.callBackListener(false, it) }
            )
        }, mViewModel.defaultSnsType, mViewModel.userId.toString(), mViewModel.userEmail)
    }


    fun userData(listener: OnCallBackListener) {
        UserServer.getUserById(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<User>).data
                        if (CustomLog.flag) CustomLog.L("userLoginTypeCheck value", it)
                        mViewModel.userEmail = value.email
                        listener.callBackListener(true, value)
                    },
                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") },
                    dataIsNull = { listener.callBackListener(false, it) }
            )
        }, mViewModel.userId.toInt())
    }

}