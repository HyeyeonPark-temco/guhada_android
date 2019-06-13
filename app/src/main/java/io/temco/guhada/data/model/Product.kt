package io.temco.guhada.data.model

import com.google.gson.annotations.SerializedName

class Product : BaseProduct(){
    var brandId: Int = 0
    var totalStock: Int = 0
    var sellPrice: Int = 0
    var discountRate: Int = 0
    var discountPrice: Int = 0
    var shipExpense: Int = 0
    var sellerId: Int = 0

    var modelNumber: String = ""
    var season: String = ""

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
    var shipping: Shipping? = Shipping()

    // list
    var imageUrls: List<String>? = ArrayList()
    var productNotifies: List<Item>? = ArrayList()
    var filters: List<Item>? = ArrayList()

    @SerializedName("separatedOptions")
    var options: List<Option?>? = ArrayList()

    // 변경 예정
    var images: MutableList<Image> = ArrayList()

    // ids
    var lCategoryId: Int = 0
    var mCategoryId: Int = 0
    var sCategoryId: Int = 0
    var dCategoryId: Int = 0

    class Item {
        var label: String = ""
        var value: String = ""
    }

    class Option {
        var type: String = ""
        var label: String = ""
        var attributes: List<String> = ArrayList()
        var rgb: List<String> = ArrayList()
    }

    class Shipping {
        var shipExpense: Int = 0
        var returnShipExpense: Int = 0
        var exchangeShipExpense: Int = 0
        var claimAddressId: Int = 0

        var shipExpenseType: String = ""
        var claimShipCompanyName: String = ""

        var isQuickAvailable: Boolean = false
        var isBundleAvailable: Boolean = false
        var isIsolatedAreaAvailable: Boolean = false
    }

    // 변경 예정
    class Image {
        var url: String = ""
    }

}


