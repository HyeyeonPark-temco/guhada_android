package io.temco.guhada.data.viewmodel.mypage.repository

import android.content.Context
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.user.User

class MyPageUserInfoRepository(val context : Context) {

    private var list = SingleLiveEvent<User>()

}