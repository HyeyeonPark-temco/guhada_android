package io.temco.guhada.data.model.user

class UserUpdateInfo {

    var accountHolder = ""
    var agreeEmailReception = false
    var agreeSmsReception = false
    var bankCode = ""
    var bankName = ""
    var email = ""
    var identityVerifyParam = IdentityVerifyParam()
    var inputUserSize = false
    var nickname = ""
    var password = ""
    var userSizeParam = UserSizeParam()
    var verifiedIdentity = false

    fun setData(user : User, userSize: UserSize?){
        accountHolder = user.name
        agreeEmailReception = user.agreeEmailReception
        agreeSmsReception = user.agreeSmsReception
        email = user.email

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
        verifiedIdentity = false
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