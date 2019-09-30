package io.temco.guhada.data.model.search

import com.google.gson.annotations.SerializedName

class Popular{

    @SerializedName("keywords")
    var keywords : List<Keywords> = arrayListOf()


}


data class Keywords(
        @SerializedName("keyword")  var keyword : String,
        @SerializedName("rankChange") var rankChange : String){


}