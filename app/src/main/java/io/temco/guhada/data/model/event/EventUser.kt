package io.temco.guhada.data.model.event

import  io.temco.guhada.data.model.Verification.Gender
import io.temco.guhada.data.model.user.UserProfile

/**
 * 럭키드로우 회원가입 model
 * - 이메일 가입 유저: userSignUp, identityVerify, verification
 * - SNS 가입 유저: snsSignUp, identityVerify, verification
 *
 * @author Hyeyeon Park
 * @since 2019.11.13
 */
class EventUser {
    var identityVerify = IdentityVerify()
    var verification = Verification()
    var userSignUp = UserSignUp()
    var snsSignUp = SnsSignUp()

    open inner class UserSignUp {
        var agreeCollectPersonalInfoTos = false
        var agreeEmailReception = false
        var agreePurchaseTos = false
        var agreeSaleTos = false
        var agreeSmsReception = false
        var email = ""
        var password = ""
    }

    /**
     * @see io.temco.guhada.data.model.Verification.Gender
     * @see io.temco.guhada.data.model.Verification.IdentityVerifyMethod
     */
    inner class IdentityVerify {
        var birth = ""
        var diCode = ""
        var gender = Gender.MALE.code
        var identityVerifyMethod = ""
        var mobile = ""
        var name = ""
    }

    /**
     * @see  io.temco.guhada.common.enum.VerificationType
     */
    inner class Verification {
        var verificationNumber = 0
        var verificationTarget = ""
        var verificationTargetType = ""
    }

    /**
     * @see io.temco.guhada.common.enum.SnsLoginType
     */
    inner class SnsSignUp : UserSignUp() {
        var snsId = ""
        var snsType = ""
        var profileJson = UserProfile()
    }
}
