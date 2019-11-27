package io.temco.guhada.data.model.main


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 (new-arrivals 신상품 목록 조회,plus-item플러스 아이템 목록 조회) 등의 recycler item data
 */
class SubTitleLayout(index: Int,
                       type: HomeType,
                       var title : String,
                       var listSize : Array<Int>,
                       var isShowTabs : Boolean,
                       var subTitleIndex : Int,
                       var currentSubTitleIndex : Int) : MainBaseModel(index, type,2)