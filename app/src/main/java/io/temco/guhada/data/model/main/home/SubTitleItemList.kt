package io.temco.guhada.data.model.main.home


import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.main.HomeType
import io.temco.guhada.data.model.main.MainBaseModel
import java.util.*

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 (new-arrivals 신상품 목록 조회,plus-item플러스 아이템 목록 조회) 등의 recycler item data
 */
class SubTitleItemList(index: Int,
                       type: HomeType,
                       var title : String,
                       var listSize : Array<Int>,
                       var currentSubTitleIndex : Int,
                       var homeDeal : HomeDeal) : MainBaseModel(index, type,2){

    override fun toString(): String {
        if(CustomLog.flag)return "SubTitleItemList(title='$title', listSize=${Arrays.toString(listSize)}, currentSubTitleIndex=$currentSubTitleIndex, homeDeal=$homeDeal)"
        else return ""
    }
}

