package io.temco.guhada.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.temco.guhada.view.adapter.ProductDetailOptionAdapter
import java.io.Serializable

open class BaseProduct : Serializable {
    var season: String = ""

    var productId: Long = 0

    var brandName: String = ""

    @SerializedName("name")
    var name: String = ""

    @Expose
    var profileUrl: String = ""

    @Expose
    var totalPrice: Int = 0

    @Expose
    var totalCount: Int = 0

    @Expose
    var optionMap: MutableMap<String, ProductDetailOptionAdapter.OptionAttr> = mutableMapOf()

    @Expose
    var dealOptionId: Long? = null
}