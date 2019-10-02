package io.temco.guhada.data.model.user

import com.google.gson.annotations.Expose
import io.temco.guhada.common.util.CustomLog

class UserUpdateInfo {

    var agreeEmailReception = false
    var agreeSmsReception = false

    var email = ""
    var nickname = ""
    var password = ""


    @Expose
    var accountHolder : String? = null
    @Expose
    var bankCode : String? = null
    @Expose
    var bankName : String? = null

    var inputUserSize = false
    var userSizeParam = UserSizeParam()
    var identityVerifyParam = IdentityVerifyParam()
    var verifiedIdentity = false

    fun setData(user : User, userSize: UserSize?){
        agreeEmailReception = user.agreeEmailReception
        agreeSmsReception = user.agreeSmsReception
        email = user.email
        nickname = user.nickname

        verifiedIdentity = false

        identityVerifyParam.birth = user.birth
        identityVerifyParam.diCode = user.userDetail.diCode ?: ""
        identityVerifyParam.gender = user.userGender
        identityVerifyParam.identityVerifyMethod = ""
        identityVerifyParam.mobile = user.mobile
        identityVerifyParam.name = user.name

        inputUserSize = userSize != null
        if(userSize!=null){
            userSizeParam.bottom = userSize.bottom
            userSizeParam.height = userSize.height
            userSizeParam.shoe = userSize.shoe
            userSizeParam.top = userSize.top
            userSizeParam.weight = userSize.weight
        }

        accountHolder = user.name
        bankCode = user.userDetail.bankCode
    }

    override fun toString(): String {
        if(CustomLog.flag)return "UserUpdateInfo(accountHolder='$accountHolder', agreeEmailReception=$agreeEmailReception, agreeSmsReception=$agreeSmsReception, bankCode='$bankCode', bankName='$bankName', email='$email', identityVerifyParam=$identityVerifyParam, inputUserSize=$inputUserSize, nickname='$nickname', password='$password', userSizeParam=$userSizeParam, verifiedIdentity=$verifiedIdentity)"
        else return ""
    }


}

class IdentityVerifyParam {
    var birth = ""
    var diCode = ""
    var gender = ""
    var identityVerifyMethod = ""
    var mobile = ""
    var name = ""
}


class UserSizeParam {
    var bottom = 0
    var height = 0
    var shoe = 0
    var top = ""
    var weight = 0
}