package io.temco.guhada.data.model

import com.google.gson.annotations.SerializedName

class BookMarkProduct {

    @SerializedName("deals")
    var deals : MutableList<Deal> = mutableListOf()

    @SerializedName("totalPage")
    var totalPage : Int = 0

    @SerializedName("totalElements")
    var totalElements : Int = 0



}