package io.temco.guhada.data.model.user

import  io.temco.guhada.data.model.Verification.Gender

/**
 * 럭키드로우 회원가입 model
 * @author Hyeyeon Park
 * @since 2019.11.13
 */
class EventUser {
    var identityVerify = IdentityVerify()
    var userSignUp = UserSignUp()
    var verification = Verification()

    inner class UserSignUp {
        var agreeCollectPersonalInfoTos = false
        var agreeEmailReception = false
        var agreePurchaseTos = false
        var agreeSaleTos = false
        var agreeSmsReception = false
        var email = ""
        var password = ""
    }

    inner class IdentityVerify {
        var birth = ""
        var diCode = ""
        var gender = Gender.MALE.code
        var identityVerifyMethod = ""
        var mobile = ""
        var name = ""
    }

    inner class Verification {
        var verificationNumber = 0
        var verificationTarget = ""
        var verificationTargetType = ""
    }
}
