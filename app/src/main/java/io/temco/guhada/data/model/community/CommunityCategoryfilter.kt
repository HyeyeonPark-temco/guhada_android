package io.temco.guhada.data.model.community

import com.google.gson.annotations.SerializedName
import io.temco.guhada.common.util.CustomLog
import java.io.Serializable

class CommunityCategoryfilter : Serializable {

    @SerializedName("id")
    var id = 0
    /*var communityCategoryId = 0*/
    var name = ""
    override fun toString(): String {
        if(CustomLog.flag)return "CommunityCategoryfilter(id='$id', name='$name')"
        else return ""
    }


}