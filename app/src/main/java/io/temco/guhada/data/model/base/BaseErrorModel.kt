package io.temco.guhada.data.model.base

import com.google.gson.annotations.SerializedName

data class BaseErrorModel(var code : Int,
                          var url : String,
                          var Message : Message){

}



class Message{

    @SerializedName("result")
    var result: String = ""

    @SerializedName("message")
    var message: String = ""

    @SerializedName("resultCode")
    var resultCode: Int = 0

    @SerializedName("error")
    var error: String = ""



}