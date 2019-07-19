package io.temco.guhada.data.model.main

import com.google.gson.annotations.SerializedName
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.main.HomeType
import io.temco.guhada.data.model.main.MainBaseModel

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 (new-arrivals 신상품 목록 조회,plus-item플러스 아이템 목록 조회) 등의 제품 리스트
 */
class HomeDeal {

    @SerializedName("ALL")
    var allList: List<Deal>? = null

    @SerializedName("WOMEN")
    var womenList: List<Deal>? = null

    @SerializedName("MEN")
    var menList: List<Deal>? = null

    @SerializedName("KIDS")
    var kidsList: List<Deal>? = null

    override fun toString(): String {
        if(CustomLog.flag)return "HomeDeal(allList=$allList, womenList=$womenList, menList=$menList, kidsList=$kidsList)"
        else return ""
    }


}