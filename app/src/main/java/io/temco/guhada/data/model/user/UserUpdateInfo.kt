package io.temco.guhada.data.model.user

import android.text.TextUtils
import com.google.gson.annotations.Expose
import io.temco.guhada.common.util.CustomLog

class UserUpdateInfo {

    var agreeEmailReception = false
    var agreeSmsReception = false

    @Expose
    var email : String? = null

    @Expose
    var nickname : String? = null

    @Expose
    var password : String? = null

    @Expose
    var accountHolder : String? = null

    @Expose
    var accountNumber : String? = null

    @Expose
    var bankCode : String? = null

    @Expose
    var bankName : String? = null

    var inputUserSize = false

    @Expose
    var userSizeParam : UserSizeParam? = null

    var identityVerifyParam = IdentityVerifyParam()

    var verifiedIdentity = false

    fun setData(user : User, pass : String?, userSize: UserSize?, account : Boolean, verifiedIdentity : Boolean){
        agreeEmailReception = user.userDetail.agreeEmailReception
        agreeSmsReception = user.userDetail.agreeSmsReception
        email = if(TextUtils.isEmpty(user.email)) null else user.email
        nickname = if(TextUtils.isEmpty(user.nickname)) null else user.nickname
        password = if(TextUtils.isEmpty(pass)) null else pass
        // 인증을 했는주 여부
        this.verifiedIdentity = verifiedIdentity

        identityVerifyParam.birth = if(TextUtils.isEmpty(user.birth))null else user.birth
        identityVerifyParam.diCode = if(TextUtils.isEmpty(user.userDetail.diCode ?: null))null else user.userDetail.diCode ?: null
        identityVerifyParam.gender = if(TextUtils.isEmpty(user.userGender))null else user.userGender
        identityVerifyParam.identityVerifyMethod = if(TextUtils.isEmpty(user.userDetail.identityVerifyMethod ?: null))null else user.userDetail.identityVerifyMethod ?: null
        identityVerifyParam.mobile = if(TextUtils.isEmpty(user.mobile))null else user.mobile
        identityVerifyParam.name = if(TextUtils.isEmpty(user.name))null else user.name

        // 유저 사이즈 업데이트 여부
        inputUserSize = userSize != null
        if(userSize!=null){
            userSizeParam = UserSizeParam()
            userSizeParam!!.bottom = userSize.bottom
            userSizeParam!!.height = userSize.height
            userSizeParam!!.shoe = userSize.shoe
            userSizeParam!!.top = userSize.top
            userSizeParam!!.weight = userSize.weight
        }else userSizeParam = null

        if(account){
            this.accountHolder = user.userDetail.accountHolder
            this.accountNumber = user.userDetail.accountNumber
            this.bankCode = user.userDetail.bankCode
            this.bankName = user.userDetail.bankName
        }
    }

    override fun toString(): String {
        if(CustomLog.flag)return "UserUpdateInfo(accountHolder='$accountHolder', agreeEmailReception=$agreeEmailReception, agreeSmsReception=$agreeSmsReception, \n" +
                "bankCode='$bankCode', bankName='$bankName', email='$email', inputUserSize=$inputUserSize, " + "nickname='$nickname', password='$password'" +
                "\n, identityVerifyParam=$identityVerifyParam\n, userSizeParam=$userSizeParam, verifiedIdentity=$verifiedIdentity)"
        else return ""
    }

}

class IdentityVerifyParam {
    var birth : String? = null
    @Expose
    var diCode : String? = null
    @Expose
    var gender : String? = null
    @Expose
    var identityVerifyMethod : String? = null
    @Expose
    var mobile : String? = null
    @Expose
    var name : String? = null

    override fun toString(): String {
        if(CustomLog.flag)return "IdentityVerifyParam(birth='$birth', diCode='$diCode', gender='$gender', identityVerifyMethod='$identityVerifyMethod', mobile='$mobile', name='$name')"
        else return ""
    }

}


class UserSizeParam {
    var bottom = 0
    var height = 0
    var shoe = 0
    var top = ""
    var weight = 0

    override fun toString(): String {
        if(CustomLog.flag)return "UserSizeParam(bottom=$bottom, height=$height, shoe=$shoe, top='$top', weight=$weight)"
        else return ""
    }

}