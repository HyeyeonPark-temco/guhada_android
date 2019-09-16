package io.temco.guhada.data.model.order

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import io.temco.guhada.data.model.option.Option
import io.temco.guhada.data.model.option.OptionInfo
import java.io.Serializable

/**
 * @see Order
 * @see PurchaseOrder
 * @author Hyeyeon Park
 */
open class OrderItemResponse : Serializable {
    var cartItemId = 0L

    // SELLER
    var sellerId = 0L
    var sellerName = ""

    // CATEGORY
    var lcategoryId = 0
    var mcategoryId = 0
    var scategoryId = 0
    var dcategoryId = 0

    var lCategoryId = 0L
    var mCategoryId = 0L
    var sCategoryId = 0L
    var dCategoryId = 0L

    // BRAND
    var brandId = 0L
    var brandName = ""

    // PRODUCT
    var productId: Long = 0
    var season = ""
    var dealId: Long = 0
    var dealName = ""
    var imageName = "" // 대표 이미지 파일 명
    var imageUrl = "" // 대표 이미지 URL
    var totalStock = 0
    var sellPrice = 0
    var discountPrice = 0 // 할인가 (판매가와 같은 경우는 할인이 안 됨을 의미함)
    var discountDiffPrice = 0 // 할인 적용 금액 (판매가 - 할인가)
    var shipExpense = 0
    var orderValidStatus = "" // 상품 상태 ("VALID", "NOT_SALE", "NOT_DISPLAY", "SOLD_OUT", "CHANGE_PRICE", "CHANGE_OPTION")
    var quantity = 0 // 구매 수량

    var optionInfo: OptionInfo = OptionInfo()

//    constructor(parcel: Parcel) : this() {
//        cartItemId = parcel.readLong()
//        sellerId = parcel.readLong()
//        sellerName = parcel.readString() ?: ""
//        lcategoryId = parcel.readInt()
//        mcategoryId = parcel.readInt()
//        scategoryId = parcel.readInt()
//        dcategoryId = parcel.readInt()
//        lCategoryId = parcel.readLong()
//        mCategoryId = parcel.readLong()
//        sCategoryId = parcel.readLong()
//        dCategoryId = parcel.readLong()
//        brandId = parcel.readLong()
//        brandName = parcel.readString() ?: ""
//        productId = parcel.readLong()
//        season = parcel.readString() ?: ""
//        dealId = parcel.readLong()
//        dealName = parcel.readString() ?: ""
//        imageName = parcel.readString() ?: ""
//        imageUrl = parcel.readString() ?: ""
//        totalStock = parcel.readInt()
//        sellPrice = parcel.readInt()
//        discountPrice = parcel.readInt()
//        discountDiffPrice = parcel.readInt()
//        shipExpense = parcel.readInt()
//        orderValidStatus = parcel.readString() ?: ""
//        quantity = parcel.readInt()
//        optionInfo = parcel.readSerializable() as?OptionInfo ?: OptionInfo()
//    }
//
//    override fun writeToParcel(dest: Parcel?, flags: Int) {
//        dest?.writeLong(cartItemId)
//        dest?.writeLong(brandId)
//        dest?.writeLong(sellerId)
//        dest?.writeLong(productId)
//        dest?.writeLong(dealId)
//        dest?.writeLong(lCategoryId)
//        dest?.writeLong(mCategoryId)
//        dest?.writeLong(dCategoryId)
//        dest?.writeLong(sCategoryId)
//
//        dest?.writeString(brandName)
//        dest?.writeString(season)
//        dest?.writeString(dealName)
//        dest?.writeString(imageName)
//        dest?.writeString(imageUrl)
//        dest?.writeString(orderValidStatus)
//
//        dest?.writeInt(totalStock)
//        dest?.writeInt(sellPrice)
//        dest?.writeInt(discountPrice)
//        dest?.writeInt(discountDiffPrice)
//        dest?.writeInt(shipExpense)
//        dest?.writeInt(quantity)
//
//        dest?.writeSerializable(optionInfo)
//    }
//
//    override fun describeContents(): Int = 0
//
//    companion object CREATOR : Parcelable.Creator<OrderItemResponse> {
//        override fun createFromParcel(parcel: Parcel): OrderItemResponse {
//            return OrderItemResponse(parcel)
//        }
//
//        override fun newArray(size: Int): Array<OrderItemResponse?> {
//            return arrayOfNulls(size)
//        }
//    }
}

