package io.temco.guhada.data.model.review

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MyPageOrderReview {

    @SerializedName("page")
    var page : Int = 0

    @SerializedName("count")
    var count : Int = 0

    @SerializedName("totalPage")
    var totalPage : Int = 0

    @SerializedName("orderItemList")
    var orderItemList : List<ReviewAvailableOrder> = arrayListOf()

}



class ReviewAvailableOrder :  MyPageReviewBase(), Serializable {

    override val type: MyPageReviewType
        get() = MyPageReviewType.AvailableReview

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

