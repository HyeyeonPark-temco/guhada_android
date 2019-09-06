package io.temco.guhada.data.model

class SellerInquireOrder {

    var purchaseId = 0L
    var dealId = 0L
    var productId = 0L
    var orderProdGroupId = 0L
    var imageName = ""
    var imageUrl = ""
    var brandName = ""
    var season = ""
    var productName = ""
    /*var optionAttribute1": null,
    var optionAttribute2": null,
    var optionAttribute3": null,*/
    var quantity = ""
    var status = ""
    var statusText = ""

    override fun toString(): String {
        return "SellerInquireOrder(purchaseId=$purchaseId, dealId=$dealId, productId=$productId, orderProdGroupId=$orderProdGroupId, imageName='$imageName', imageUrl='$imageUrl', brandName='$brandName', season='$season', productName='$productName', quantity='$quantity', status='$status', statusText='$statusText')"
    }

}