package io.temco.guhada.data.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.base.BasePageModel
import java.io.Serializable

class MyPageReview : BasePageModel(){

    @SerializedName("content")
    var content : List<MyPageReviewContent> = arrayListOf()

    override fun toString(): String {
        return "MyPageReview(content=$content)"
    }

}

class MyPageReviewContent : MyPageReviewBase(){
    override val type: MyPageReviewType
        get() = MyPageReviewType.MyReview

    @SerializedName("review")
    var review = ReviewData()

    @SerializedName("reviewTexts")
    var reviewTexts = ReviewTexts()

    @SerializedName("order")
    var order = ReviewOrder()

    @SerializedName("reviewPhotos")
    var reviewPhotos : List<ReviewPhotos> = arrayListOf()

    @SerializedName("productOption")
    var productOption = ProductOption()

    @SerializedName("userSize")
    var userSize = UserSize()

    /*@SerializedName("bookmarksUserIds")
    var bookmarksUserIds = JSONObject()*/
    //"bookmarksUserIds": null


    override fun toString(): String {
        return "MyPageReviewContent(review=$review, reviewTexts=$reviewTexts, order=$order, reviewPhotos=$reviewPhotos, productOption=$productOption, userSize=$userSize)"
    }

}
class ReviewData : Serializable {
    @SerializedName("userNickname")
    var userNickname : String = ""

    @SerializedName("userId")
    var userId : Int = 0

    @SerializedName("createdAt")
    var createdAt : String = ""

    @SerializedName("productId")
    var productId : Long = 0L

    @SerializedName("textReview")
    var textReview : String = ""

    @SerializedName("productRating")
    var productRating : String = ""

    @SerializedName("orderProductGroupId")
    var orderProductGroupId : Long = 0L

    @SerializedName("sizeSatisfaction")
    var sizeSatisfaction : String = ""

    @SerializedName("colorSatisfaction")
    var colorSatisfaction : String = ""

    @SerializedName("lengthSatisfaction")
    var lengthSatisfaction : String = ""

    @SerializedName("photoCount")
    var photoCount : Int = 0

    @SerializedName("bookmarkCount")
    var bookmarkCount : Int = 0

    @SerializedName("profileImageUrl")
    var profileImageUrl : String = ""

    @SerializedName("id")
    var id : Int = 0


    fun getRating(): Float = when (productRating) {
        "HALF" -> 0.5f
        "ONE" -> 1.0f
        "ONE_HALF" -> 1.5f
        "TWO" -> 2.0f
        "TWO_HALF" -> 2.5f
        "THREE" -> 3.0f
        "THREE_HALF" -> 3.5f
        "FOUR" -> 4.0f
        "FOUR_HALF" -> 4.5f
        "FIVE" -> 5.0f
        else -> 0.0f
    }


    fun getColorSatisfaction(): Int = when (colorSatisfaction) {
        "BRIGHTER" -> 0
        "SAME" -> 1
        "DARKER" -> 2
        else -> 0
    }

    fun getLengthSatisfaction (): Int = when (lengthSatisfaction) {
        "SHORT" -> 0
        "REGULAR" -> 1
        "LONG" -> 2
        else -> 0
    }

    fun getSizeSatisfaction  (): Int = when (sizeSatisfaction) {
        "SMALL" -> 0
        "JUST_FIT" -> 1
        "LARGE" -> 2
        else -> 0
    }


    override fun toString(): String {
        return "ReviewData(userNickname='$userNickname', userId=$userId, createdAt='$createdAt', productId=$productId, textReview='$textReview', productRating='$productRating', orderProductGroupId=$orderProductGroupId, sizeSatisfaction='$sizeSatisfaction', colorSatisfaction='$colorSatisfaction', lengthSatisfaction='$lengthSatisfaction', photoCount=$photoCount, bookmarkCount=$bookmarkCount, profileImageUrl='$profileImageUrl', id=$id)"
    }
}

class ReviewTexts : Serializable  {
    @SerializedName("size")
    var size : String = ""

    @SerializedName("color")
    var color : String = ""

    @SerializedName("length")
    var length : String = ""

    override fun toString(): String {
        return "ReviewTexts(size='$size', color='$color', length='$length')"
    }
}

class ReviewOrder : Serializable {
    @SerializedName("purchaseId")
    var purchaseId : Long = 0L

    @SerializedName("productId")
    var productId : Long = 0L

    /*@SerializedName("orderDate")
    var orderDate : String = ""*/

    @SerializedName("orderTimestamp")
    var orderTimestamp : Long = 0L

    @SerializedName("brandName")
    var brandName : String = ""

    @SerializedName("season")
    var season : String = ""

    @SerializedName("prodName")
    var prodName : String = ""

    @SerializedName("imageName")
    var imageName : String = ""

    @SerializedName("imageUrl")
    var imageUrl : String = ""

    @SerializedName("optionAttribute1")
    var optionAttribute1 : String = ""

    @SerializedName("optionAttribute2")
    var optionAttribute2 : String = ""

    @SerializedName("optionAttribute3")
    var optionAttribute3 : String = ""

    @SerializedName("quantity")
    var quantity : Int = 0

    @SerializedName("discountPrice")
    var discountPrice : Int = 0

    @SerializedName("originalPrice")
    var originalPrice : Int = 0

    @SerializedName("orderPrice")
    var orderPrice : Int = 0

    @SerializedName("shipPrice")
    var shipPrice : Int = 0

    @SerializedName("sellerId")
    var sellerId : Long = 0L

    @SerializedName("sellerName")
    var sellerName : String = ""

    @SerializedName("purchaseStatus")
    var purchaseStatus : String = ""

    @SerializedName("statusMessage")
    var statusMessage : String = ""

    /*@SerializedName("expireDate")
    var expireDate : String = ""*/

    @SerializedName("expireTimestamp")
    var expireTimestamp : String = ""

    @SerializedName("orderProdGroupId")
    var orderProdGroupId : Int = 0

    @SerializedName("purchaseConfirm")
    var purchaseConfirm : Boolean = false

    /*@SerializedName("shipCompleteDate")
    var shipCompleteDate : String = ""*/

    @SerializedName("shipCompleteTimestamp")
    var shipCompleteTimestamp : String = ""

    @SerializedName("reviewId")
    var reviewId : Int = 0

    @SerializedName("dealId")
    var dealId : Long = 0L

    @SerializedName("purchaseStatusText")
    var purchaseStatusText : String = ""

    override fun toString(): String {
        return "ReviewOrder(purchaseId=$purchaseId, productId=$productId, orderTimestamp=$orderTimestamp, brandName='$brandName', season='$season', prodName='$prodName', imageName='$imageName', imageUrl='$imageUrl', optionAttribute1='$optionAttribute1', optionAttribute2='$optionAttribute2', optionAttribute3='$optionAttribute3', quantity=$quantity, discountPrice=$discountPrice, originalPrice=$originalPrice, orderPrice=$orderPrice, shipPrice=$shipPrice, sellerId=$sellerId, sellerName='$sellerName', purchaseStatus='$purchaseStatus', statusMessage='$statusMessage', expireTimestamp='$expireTimestamp', orderProdGroupId=$orderProdGroupId, purchaseConfirm=$purchaseConfirm, shipCompleteTimestamp=$shipCompleteTimestamp, reviewId=$reviewId, dealId=$dealId, purchaseStatusText='$purchaseStatusText')"
    }


}

class ReviewPhotos : Serializable {
    @SerializedName("id")
    var id : Int = 0

    @SerializedName("userProductReviewId")
    var userProductReviewId : Int = 0

    @SerializedName("reviewPhotoUrl")
    var reviewPhotoUrl : String = ""

    @SerializedName("photoOrder")
    var photoOrder : Int = 0

    override fun toString(): String {
        return "ReviewPhotos(id=$id, userProductReviewId=$userProductReviewId, reviewPhotoUrl='$reviewPhotoUrl', photoOrder=$photoOrder)"
    }
}

class ProductOption : Serializable {
    @SerializedName("color")
    var color : String = ""
    //var size": null

    override fun toString(): String {
        return "ProductOption(color='$color')"
    }
}

class UserSize : Serializable {
    @SerializedName("height")
    var height : Int = 0

    @SerializedName("weight")
    var weight : Int = 0

    @SerializedName("top")
    var top : String = ""

    @SerializedName("bottom")
    var bottom : Int = 0

    @SerializedName("shoe")
    var shoe : Int = 0

    override fun toString(): String {
        return "UserSize(height=$height, weight=$weight, top='$top', bottom=$bottom, shoe=$shoe)"
    }
}

class BookmarksUserIds {

}
