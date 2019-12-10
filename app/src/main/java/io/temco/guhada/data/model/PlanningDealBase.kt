package io.temco.guhada.data.model

enum class PlanningDealType { Title, ImageBanner, SubTitle,Deal}

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인의 홈의 리스트에서 사용하는 기본 모델
 */
open class PlanningDealBase (val index : Int, // recyclerview list index
                          var type : PlanningDealType, // recyclerview view type
                          val gridSpanCount : Int) // gridview의 span count

class PlanningDealTitle(index : Int, // recyclerview list index
                         type : PlanningDealType, // recyclerview view type
                         gridSpanCount : Int,
                         val title : String,
                         val strDate : String) : PlanningDealBase(index, type, gridSpanCount)

class PlanningDealImageBanner(index : Int, // recyclerview list index
                              type : PlanningDealType, // recyclerview view type
                              gridSpanCount : Int,
                              val imgPath : String) : PlanningDealBase(index, type, gridSpanCount)

class PlanningDealSubTitle(index : Int, // recyclerview list index
                              type : PlanningDealType, // recyclerview view type
                              gridSpanCount : Int,
                              val maxPage : Int) : PlanningDealBase(index, type, gridSpanCount)


class PlanningDealData(index : Int, // recyclerview list index
                       type : PlanningDealType, // recyclerview view type
                       gridSpanCount : Int,
                       val deal : Deal) : PlanningDealBase(index, type, gridSpanCount)
