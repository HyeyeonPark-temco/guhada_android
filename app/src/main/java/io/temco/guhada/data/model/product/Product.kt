package io.temco.guhada.data.model.product

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.option.Option
import io.temco.guhada.data.model.option.OptionInfo

/**
 * 상품 정보 클래스
 * @author Hyeyeon Park
 */
class Product : BaseProduct() {
    // PRODUCT
    var brandId: Int = 0
    var modelNumber: String = ""
    var productStatus: String = ""
    var productNumber: String = ""
    var productOriginType: String = ""
    var originValue1: String = ""
    var originValue2: String = ""
    var totalStock: Int = 0

    // SHIPPING
    var shipping: Shipping? = Shipping()
    var shipExpenseType: String = ""

    // AS (배송, 교환, 환불 정보)
    var asInfo: String = ""
    var asTelephone: String = ""
    var asDesc: String = ""

    // DESCRIPTION
    var desc: String = ""
    var tag: String = ""

    // SELLER
    var sellerId: Long = 0
    var sellerName: String = ""

    // PRICE
    var sellPrice: Int = 0
    var discountRate: Int = 0
    var discountPrice: Int = 0
    var shipExpense: Int = 0

    // LIST
    var imageUrls: List<String>? = ArrayList()
    var productNotifies: List<Item>? = ArrayList()
    var filters: List<Item>? = ArrayList()

    // OPTION
    @SerializedName("options")
    var optionInfos: List<OptionInfo>? = ArrayList()

    @SerializedName("separatedOptions")
    var options: List<Option?>? = ArrayList()

    // CATEGORY
    var lCategoryId: Int = 0
    var mCategoryId: Int = 0
    var sCategoryId: Int = 0
    var dCategoryId: Int = 0

    class Item {
        var label: String = ""
        var value: String = ""
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


        fun isFreeShipping(): Boolean {
            return if (shipExpenseType != null && "" != shipExpenseType) "FREE" == shipExpenseType else false
        }
    }

    // 변경 예정
    class Image {
        var url: String = ""
    }

    override fun toString(): String {
        return "Product(brandId=$brandId, modelNumber='$modelNumber', productStatus='$productStatus', productNumber='$productNumber', productOriginType='$productOriginType', originValue1='$originValue1', originValue2='$originValue2', totalStock=$totalStock, shipping=$shipping, shipExpenseType='$shipExpenseType', asInfo='$asInfo', asTelephone='$asTelephone', asDesc='$asDesc', desc='$desc', tag='$tag', sellerId=$sellerId, sellerName='$sellerName', sellPrice=$sellPrice, discountRate=$discountRate, discountPrice=$discountPrice, shipExpense=$shipExpense, imageUrls=$imageUrls, productNotifies=$productNotifies, filters=$filters, optionInfos=$optionInfos, options=$options, lCategoryId=$lCategoryId, mCategoryId=$mCategoryId, sCategoryId=$sCategoryId, dCategoryId=$dCategoryId)"
    }


}


