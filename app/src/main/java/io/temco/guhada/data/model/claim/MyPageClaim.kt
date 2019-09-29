package io.temco.guhada.data.model.claim

import com.google.gson.annotations.SerializedName
import io.temco.guhada.data.model.base.BasePageModel
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class MyPageClaim : BasePageModel(), Serializable {

    @SerializedName("content")
    var content : ArrayList<Content> = arrayListOf()

    inner class Content {
        @SerializedName("inquiry")
        var inquiry : Inquiry = Inquiry()

        @SerializedName("item")
        var item : Item = Item()

        var totalPages : Int = -1

        var pageNumber : Int = -1

        override fun toString(): String {
            return "Content(inquiry=$inquiry, item=$item, totalPages=$totalPages, pageNumber=$pageNumber)"
        }
    }

    inner class Item{
        @SerializedName("brandId")
        var brandId : Long = 0L

        @SerializedName("brandName")
        var brandName : String = ""

        @SerializedName("sellerId")
        var sellerId : Long = 0L

        @SerializedName("sellerName")
        var sellerName : String = ""

        @SerializedName("productId")
        var productId : Long = 0L

        @SerializedName("productName")
        var productName : String = ""

        @SerializedName("imageName")
        var imageName : String = ""

        @SerializedName("imageUrl")
        var imageUrl : String = ""

        @SerializedName("dealId")
        var dealId : Long = 0L

        @SerializedName("dealName")
        var dealName : String = ""

        @SerializedName("totalStock")
        var totalStock : Long = 0L

        @SerializedName("sellPrice")
        var sellPrice : Double = 0.0

        @SerializedName("discountRate")
        var discountRate : Int = 0

        @SerializedName("discountPrice")
        var discountPrice : Double = 0.0

        @SerializedName("shipExpenseType")
        var shipExpenseType : String = ""

        @SerializedName("productSeason")
        var productSeason : String = ""

        @SerializedName("options")
        var options : List<Options> = listOf()

        inner class Options {
            @SerializedName("type")
            var type : String = ""

            @SerializedName("attributes")
            var attributes : Array<String> = arrayOf()

            override fun toString(): String {
                return "Options(type=$type, attributes=${Arrays.toString(attributes)})"
            }
        }

        override fun toString(): String {
            return "Item(brandId=$brandId, brandName='$brandName', sellerId=$sellerId, sellerName='$sellerName', productId=$productId, productName='$productName', imageName='$imageName', imageUrl='$imageUrl', dealId=$dealId, dealName='$dealName', totalStock=$totalStock, sellPrice=$sellPrice, discountRate=$discountRate, discountPrice=$discountPrice, shipExpenseType='$shipExpenseType', productSeason='$productSeason', options=$options)"
        }

    }

    inner class Inquiry{
        @SerializedName("id")
        var id : Long = 0L

        @SerializedName("productId")
        var productId : Long = 0L

        @SerializedName("status")
        var status : String = ""

        @SerializedName("inquiry")
        var inquiry : String = ""

        @SerializedName("inquirer")
        var inquirer : Long = 0L

        @SerializedName("nickname")
        var nickname : String = ""

        @SerializedName("reply")
        var reply = ""

        @SerializedName("replier")
        var replier : Long = 0L

        @SerializedName("replyAt")
        var replyAt : Long? = null

        @SerializedName("replyUpdated")
        var replyUpdated : String = ""

        @SerializedName("enable")
        var enable : Boolean = false

        @SerializedName("createdAt")
        var createdAt : Long = 0L

        @SerializedName("updatedAt")
        var updatedAt : Long = 0L

        @SerializedName("private")
        var private : Boolean = false

        override fun toString(): String {
            return "Inquiry(id=$id, productId=$productId, status='$status', inquiry='$inquiry', inquirer=$inquirer, nickname='$nickname', reply='$reply', replier=$replier, replyAt=$replyAt, replyUpdated='$replyUpdated', enable=$enable, createdAt=$createdAt, updatedAt=$updatedAt, private=$private)"
        }


    }

    override fun toString(): String {
        return "MyPageClaim(pageable=$pageable, pageSize=$pageSize, totalElements=$totalElements, totalPages=$totalPages, last=$last, first=$first, size=$size, number=$number, numberOfElements=$numberOfElements, empty=$empty, sort=$sort)"
    }


}