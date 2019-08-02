package io.temco.guhada.data.model

import com.google.gson.JsonObject

/**
 * bookmark 등록 post data
 * @author park jungho
 * 19.08.01
 */
class BookMarkResponse {

    var target : String = ""
    var targetId : Long = 0L

    constructor(target: String, targetId: Long) {
        this.target = target
        this.targetId = targetId
    }


    fun getProductBookMarkRespose() : JsonObject {
        //var gson = Gson()
        val resposeData = JsonObject()
        resposeData.addProperty("target",target)
        resposeData.addProperty("targetId",targetId)
        return resposeData/* gson.toJson(resposeData)*/
    }
}