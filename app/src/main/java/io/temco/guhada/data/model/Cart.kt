package io.temco.guhada.data.model

class Cart {
    var brandName: String = ""
    var cartValidStatus: ValidStatus = ValidStatus()
    var dealName: String = ""
    var dealUrl: String = ""
    var imageName: String = ""
    var imageUrl: String = ""
    var season: String = ""
    var sellerName: String = ""

    var dealId: Long = 0
    var sellerId: Long = 0
    var cartItemId: Long = 0

    var currentQuantity: Int = 0
    var discountDiffPrice: Int = 0
    var discountPrice: Int = 0
    var maxQuantity: Int = 0
    var minQuantity: Int = 0
    var priority: Int = 0
    var selectedCartOption: Product.OptionInfo = Product.OptionInfo()
    var sellPrice: Int = 0
    var shipExpense: Int = 0
    var totalStock: Int = 0

    class ValidStatus {
        var status = false
        var cartErrorMessage = ""
    }
}
