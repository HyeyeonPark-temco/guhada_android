package io.temco.guhada.data.model.event

import android.text.TextUtils
import io.temco.guhada.common.util.CustomLog
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
    var acceptTerms = AcceptTerms()
    var email = ""
    var emailVerified = false                 // 이메일 수정 가능 여부 판단 (true: 수정 불가)
    var validEmail = false                  // 유효한 이메일 format 여부 (false: 이메일 필드 지우고 새로 입력받아야 함; sns의 경우 KAKAO:12345 로 email이 저장되는 경우가 있음)
                                            // 나머지 조건이 모두 충족하다는 가정하에, validEmail이 true면 회원정보 수정으로 넘기지 않음
    var verifiedIdentityUpdated = false


    open class UserSignUp : AcceptTerms(), Serializable {

        var email = ""
        var password = ""

        override fun toString(): String {
            if(CustomLog.flag)return "UserSignUp(email='$email', password='$password')"
            else return ""
        }
    }

    open class AcceptTerms : Serializable {
        var agreeCollectPersonalInfoTos = false
        var agreeEmailReception = false
        var agreePurchaseTos = false
        var agreeSaleTos = false
        var agreeSmsReception = false
        override fun toString(): String {
            if(CustomLog.flag)return "AcceptTerms(agreeCollectPersonalInfoTos=$agreeCollectPersonalInfoTos, agreeEmailReception=$agreeEmailReception, agreePurchaseTos=$agreePurchaseTos, agreeSaleTos=$agreeSaleTos, agreeSmsReception=$agreeSmsReception)"
            else return ""
        }
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
        override fun toString(): String {
            if(CustomLog.flag)return "IdentityVerify(birth='$birth', diCode='$diCode', gender='$gender', identityVerifyMethod='$identityVerifyMethod', mobile='$mobile', name='$name')"
            else return ""
        }
    }

    /**
     * @see  io.temco.guhada.common.enum.VerificationType
     */
    inner class Verification {
        var verificationNumber = 0
        var verificationTarget = ""
        var verificationTargetType = ""
        override fun toString(): String {
            if(CustomLog.flag)return "Verification(verificationNumber=$verificationNumber, verificationTarget='$verificationTarget', verificationTargetType='$verificationTargetType')"
            else return ""
        }
    }

    /**
     * @see io.temco.guhada.common.enum.SnsLoginType
     *
     */
    open class SnsSignUp : UserSignUp(), Serializable {
        var snsId = ""
        var snsType = ""
        var profileJson = UserProfile()
        override fun toString(): String {
            if(CustomLog.flag)return "SnsSignUp(snsId='$snsId', snsType='$snsType', profileJson=$profileJson)"
            else return ""
        }
    }

    override fun toString(): String {
        if(CustomLog.flag)return "EventUser(identityVerify=$identityVerify, userSignUp=$userSignUp, snsSignUp=$snsSignUp, acceptTerms=$acceptTerms, email='$email', emailVerified=$emailVerified, validEmail=$validEmail, verifiedIdentityUpdated=$verifiedIdentityUpdated)"
        else return ""
    }


    fun isUserLuckyEventCheck() : Boolean{
        var isFlag = false
        if(this.acceptTerms.agreeEmailReception && this.acceptTerms.agreeSmsReception && this.acceptTerms.agreeSaleTos &&
                (this.emailVerified || (!this.emailVerified && this.validEmail)) && (!TextUtils.isEmpty(this.identityVerify.identityVerifyMethod) && !"NONE".equals(this.identityVerify.identityVerifyMethod, true))){
            isFlag = true
        }

        return isFlag
    }


}
