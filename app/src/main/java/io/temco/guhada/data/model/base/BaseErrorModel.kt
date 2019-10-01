package io.temco.guhada.data.model.base

import com.google.gson.annotations.SerializedName
import io.temco.guhada.common.util.CustomLog

data class BaseErrorModel(var code : Int,
                          var url : String,
                          var Message : Message){

    override fun toString(): String {
        if(CustomLog.flag) return "BaseErrorModel(code=$code, url='$url', Message=$Message)"
        else return ""
    }
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

    override fun toString(): String {
        if(CustomLog.flag)return "Message(result='$result', message='$message', resultCode=$resultCode, error='$error')"
        else return ""
    }


}