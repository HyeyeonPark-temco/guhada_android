package io.temco.guhada.data.model

class Product {
    var brandId: Int = 0
    var totalStock: Int = 0
    var sellPrice: Int = 0
    var discountRate: Int = 0
    var discountPrice: Int = 0
    var shipExpense: Int = 0
    var sellerId: Int = 0

    var brandName: String = ""
    var modelNumber: String = ""
    var season: String = ""
    var name: String = ""
    var shipExpenseType: String = ""
    var sellerName: String = ""
    var productStatus: String = ""
    var productNumber: String = ""
    var productOriginType: String = ""
    var originValue1: String = ""
    var originValue2: String = ""
    var asInfo: String = ""
    var asTelephone: String = ""
    var asDesc: String = ""
    var desc: String = ""
    var tag: String = ""
    var shipping: Shipping? = null

    // list
    var imageUrls: List<String>? = ArrayList()
    var productNotifies: List<Item>? = ArrayList()
    var filters: List<Item>? = ArrayList()
    var options: List<Option>? = ArrayList()

    // ids
    var lCategoryId: Int = 0
    var mCategoryId: Int = 0
    var sCategoryId: Int = 0
    var dCategoryId: Int = 0

    data class Item(var label: String = "") {
        var value: String = ""
    }

    data class Option(var dealOptionSelectId: Int = 0) {

        var stock: Int = 0
        var price: Int = 0
        var rgb1: String = ""
        var label1: String = ""
        var label2: String = ""
        var attribute1: String = ""
        var attribute2: String = ""
        var viewType: String = ""
    }

    data class Shipping(var shipExpense: Int = 0) {

        var returnShipExpense: Int = 0
        var exchangeShipExpense: Int = 0
        var claimAddressId: Int = 0

        var shipExpenseType: String = ""
        var claimShipCompanyName: String = ""

        var isQuickAvailable: Boolean = false
        var isBundleAvailable: Boolean = false
        var isIsolatedAreaAvailable: Boolean = false
    }

}


