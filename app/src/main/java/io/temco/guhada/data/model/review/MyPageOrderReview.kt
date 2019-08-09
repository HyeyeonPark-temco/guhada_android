package io.temco.guhada.data.model.review

class MyPageOrderReview  : MyPageReviewBase(){

    override val type: MyPageReviewType
        get() = MyPageReviewType.AvailableReview

    var brandName : String = ""
    var dealId : Long = 0L
    var discountPrice : Double = 0.0
    var expireDate : String = ""
    var expireTimestamp : Long = 0L
    var imageName : String = ""
    var imageUrl : String = ""
    var optionAttribute1 : String = ""
    var optionAttribute2 : String = ""
    var optionAttribute3 : String = ""
    var orderDate : String = ""
    var orderPrice : Double = 0.0
    var orderProdGroupId : Long = 0L
    var orderTimestamp : String = ""
    var originalPrice : Double = 0.0
    var prodName : String = ""
    var productId : Long = 0L
    var purchaseConfirm : Boolean = false
    var purchaseId : Long = 0L
    var purchaseStatus : String = ""
    var purchaseStatusText : String = ""
    var quantity : Int = 0
    var requestClaimTimestamp : Long = 0L
    var reviewId : Long = 0L
    var season : String = ""
    var sellerId : Long = 0L
    var sellerName : String = ""
    var shipCompleteDate : String = ""
    var shipCompleteTimestamp : Long = 0L
    var shipPrice : Double = 0.0
    var statusMessage : String = ""

}

