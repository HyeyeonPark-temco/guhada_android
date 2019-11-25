package io.temco.guhada.data.model.main


/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 (new-arrivals 신상품 목록 조회,plus-item플러스 아이템 목록 조회) 등의 recycler item data
 */
class ViewMoreLayout(index: Int,
                     type: HomeType,
                     var currentSubTitleIndex: Int,
                     var moreType : String) : MainBaseModel(index, type,2)