package io.temco.guhada.data.model.community

import java.util.*
import kotlin.collections.ArrayList

class CommunityDetail {

    var id = 0L
    var categoryId = 0
    var categoryFilterId = -1
    var imageUrl : String? = null
    var title : String? = null
    var contents : String? = null
    var use = false
    var delete = false
    var deletedAt : String? = null
    var deletedTimestamp : Long? = null
    var like = false
    var bookmark = false
    var commentCount = 0
    var hitCount = 0
    var likeCount = 0
    var brandId : Long? = null
    var brandName : String? = null
    var dealId : Long? = null
    var dealName : String? = null
    var dspCreatedAt : String? = null
    var createdTimestamp : Long? = null
    var currentTimestamp : Long? = null
    var userId = 0L
    var createUserInfo = CreateUserInfo()

    var userDetail : UserDetail = UserDetail()
    override fun toString(): String {
        return "CommunityDetail(id=$id, categoryId=$categoryId, categoryFilterId=$categoryFilterId, imageUrl=$imageUrl, title=$title, contents=$contents, use=$use, delete=$delete, deletedAt=$deletedAt, deletedTimestamp=$deletedTimestamp, like=$like, bookmark=$bookmark, commentCount=$commentCount, hitCount=$hitCount, likeCount=$likeCount, brandId=$brandId, brandName=$brandName, dealId=$dealId, dealName=$dealName, dspCreatedAt=$dspCreatedAt, createdTimestamp=$createdTimestamp, currentTimestamp=$currentTimestamp, userId=$userId, createUserInfo=$createUserInfo)"
    }

}


class CreateUserInfo {
    var id = 0L
    var email : String? = null
    var name : String? = null
    var userType : String? = null
    var emailVerify = false
    var nickname : String? = null
    var mobile : String? = null
    var profileImageUrl : String? = null
    var zip : String? = null
    var address : String? = null
    var roadAddress : String? = null
    var detailAddress : String? = null
    var birth : String? = null
    var gender : String? = null
    var userStatus : String? = null
    var withdrawalAt : String? = null
    var createdAt = arrayOf<Int>()
    var userDetail : UserDetail = UserDetail()
    var roles : ArrayList<Roles> = arrayListOf()

    override fun toString(): String {
        return "CreateUserInfo(id=$id, email=$email, name=$name, userType=$userType, emailVerify=$emailVerify, nickname=$nickname, mobile=$mobile, profileImageUrl=$profileImageUrl, zip=$zip, address=$address, roadAddress=$roadAddress, detailAddress=$detailAddress, birth=$birth, gender=$gender, userStatus=$userStatus, withdrawalAt=$withdrawalAt, createdAt=${Arrays.toString(createdAt)}, userDetail=$userDetail, roles=$roles)"
    }

}


class UserDetail {
    var id = 0L
    var verifiedIdentity = false
    var verifiedName : String? = null
    var identityVerifyMethod : String? = null
    var identityVerifiedAt : String? = null
    var adult = false
    var adultCertificationAt : String? = null
    var adultCertificationExpireAt : String? = null
    var agreeCollectPersonalInfoTos = false
    var agreeCollectPersonalInfoTosAt = arrayOf<Int>()
    var agreePurchaseTos = false
    var agreePurchaseTosAt = arrayOf<Int>()
    var agreeSaleTos = false
    var agreeSaleTosAt = arrayOf<Int>()
    var agreeSmsReception = false
    var smsAgreementUpdatedAt = arrayOf<Int>()
    var agreeEmailReception = false
    var emailAgreementUpdatedAt = arrayOf<Int>()
    var interestLocation1 : String? = null
    var interestLocation2 : String? = null
    var interestLocation3 : String? = null
    var agreeSavingAccount = false
    var bankCode : String? = null
    var bankName : String? = null
    var accountHolder : String? = null
    var adultProductOpen = false
    var ciCode : String? = null
    var diCode : String? = null
    var createdAt = arrayOf<Int>()
    var updatedAt = arrayOf<Int>()
    var updatedBy : String? = null

    override fun toString(): String {
        return "UserDetail(id=$id, verifiedIdentity=$verifiedIdentity, verifiedName=$verifiedName, identityVerifyMethod=$identityVerifyMethod, identityVerifiedAt=$identityVerifiedAt, adult=$adult, adultCertificationAt=$adultCertificationAt, adultCertificationExpireAt=$adultCertificationExpireAt, agreeCollectPersonalInfoTos=$agreeCollectPersonalInfoTos, agreeCollectPersonalInfoTosAt=${Arrays.toString(agreeCollectPersonalInfoTosAt)}, agreePurchaseTos=$agreePurchaseTos, agreePurchaseTosAt=${Arrays.toString(agreePurchaseTosAt)}, agreeSaleTos=$agreeSaleTos, agreeSaleTosAt=${Arrays.toString(agreeSaleTosAt)}, agreeSmsReception=$agreeSmsReception, smsAgreementUpdatedAt=${Arrays.toString(smsAgreementUpdatedAt)}, agreeEmailReception=$agreeEmailReception, emailAgreementUpdatedAt=${Arrays.toString(emailAgreementUpdatedAt)}, interestLocation1=$interestLocation1, interestLocation2=$interestLocation2, interestLocation3=$interestLocation3, agreeSavingAccount=$agreeSavingAccount, bankCode=$bankCode, bankName=$bankName, accountHolder=$accountHolder, adultProductOpen=$adultProductOpen, ciCode=$ciCode, diCode=$diCode, createdAt=${Arrays.toString(createdAt)}, updatedAt=${Arrays.toString(updatedAt)}, updatedBy=$updatedBy)"
    }

}

class Roles {
    var type : String? = null
    var roleName : String? = null
    var description : String? = null
    var authorities : ArrayList<Authorities> = arrayListOf()

    override fun toString(): String {
        return "Roles(type=$type, roleName=$roleName, description=$description, authorities=$authorities)"
    }

}

class Authorities {
    var id = 0L
    var type : String? = null
    var description : String? = null
    var authority : String? = null

    override fun toString(): String {
        return "Authorities(id=$id, type=$type, description=$description, authority=$authority)"
    }

}






