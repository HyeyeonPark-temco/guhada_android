package io.temco.guhada.data.model.event

import  io.temco.guhada.data.model.Verification.Gender
import io.temco.guhada.data.model.user.UserProfile
import java.io.Serializable

/**
 * 럭키드로우 회원가입 model
 * - 이메일 가입 유저: userSignUp, identityVerify, verification
 * - SNS 가입 유저: snsSignUp, identityVerify, verification
 * - 이메일 인증이 제외되면서 verification 필드가 api에서 삭제되었으나, 변동 가능성이 있어 관련 코드 주석 처리
 * @author Hyeyeon Park
 * @since 2019.11.13
 */
class EventUser {
    var identityVerify = IdentityVerify()
    var userSignUp = UserSignUp()
    var snsSignUp = SnsSignUp()
//    var verification = Verification()


    // 회원정보 수정 시 사용
    var email = ""
    var emailVerified = ""                  // 이메일 수정 가능 여부 판단 (true: 수정 불가)
    var validEmail = false                  // 유효한 이메일 format 여부
    var verifiedIdentityUpdated = false

    open class UserSignUp : Serializable {
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
    open class SnsSignUp : UserSignUp(), Serializable {
        var snsId = ""
        var snsType = ""
        var profileJson = UserProfile()
    }
}
