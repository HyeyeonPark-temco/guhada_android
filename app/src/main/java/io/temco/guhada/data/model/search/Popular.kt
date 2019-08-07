package io.temco.guhada.data.model.search

import com.google.gson.annotations.SerializedName

class Popular{

    @SerializedName("keywords")
    var keywords : List<Keywords> = arrayListOf()

    override fun toString(): String {
        return "Popular(keywords=$keywords)"
    }

}


data class Keywords(
        @SerializedName("keyword")  var keyword : String,
        @SerializedName("rankChange") var rankChange : String){

    override fun toString(): String {
        return "Keywords(keyword='$keyword', rankChange='$rankChange')"
    }
}