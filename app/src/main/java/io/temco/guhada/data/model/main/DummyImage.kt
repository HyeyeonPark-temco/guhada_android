package io.temco.guhada.data.model.main

import io.temco.guhada.data.model.main.HomeType
import io.temco.guhada.data.model.main.MainBaseModel

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈화면의 더미 이미지
 */
class DummyImage(index: Int,
                type: HomeType,
                val imageRes : Int,
                val imageHeight : Int) : MainBaseModel(index, type,2)
