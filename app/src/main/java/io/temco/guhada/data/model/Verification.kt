package io.temco.guhada.data.model

/**
 * 비밀번호 재설정 시 사용 클래스
 * @see io.temco.guhada.data.viewmodel.FindPasswordViewModel
 * @author Hyeyeon Park
 */
class Verification {
    // USING CHANGE PASSWORD
    var name = ""
    var phoneNumber = ""
    var email = ""
    var newPassword = ""
    var diCode = ""
    var mobile = ""

    // USING VERIFYING EMAIL
    var verificationNumber = ""
    var verificationTarget = "" // EMAIL ADDRESS
    var verificationTargetType = "" // "EMAIL"

    // UPDATE IDENTITY VERIFY
    var gender = ""
    var birth = ""
    var identityVerifyMethod = IdentityVerifyMethod.MOBILE.code

    enum class IdentityVerifyMethod(val code: String) {
        MOBILE("MOBILE"),
        EMAIL("EMAIL"),
        NONE("NONE")        // 미인증
    }

    enum class Gender(val label: String, val code: String) {
        FEMALE("FEMALE", "0"),
        MALE("MALE", "1")
    }

}