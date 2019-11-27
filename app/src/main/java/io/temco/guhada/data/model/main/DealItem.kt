package io.temco.guhada.data.model.main

import io.temco.guhada.data.model.Deal

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 (new-arrivals 신상품 목록 조회,plus-item플러스 아이템 목록 조회) 등의 recycler item data
 */
class DealItem(index: Int, type: HomeType, var deal : Deal) : MainBaseModel(index, type,1)