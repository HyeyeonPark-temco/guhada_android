package io.temco.guhada.data.model.main

enum class HomeType { MainEvent, SubTitleList, StoreLinear, GridList, Deal, Dummy,
    Keyword, BestStore, Footer, TimeDeal, EventHeader, EventList, LuckyDrawTitle ,
    LuckyDrawTime, LuckyDrawEvent, LuckyDrawFooter, SubTitleLayout, DealItemOne,
    ViewMoreLayout, MainBanner}

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인의 홈의 리스트에서 사용하는 기본 모델
 */
open class MainBaseModel (val index : Int, // recyclerview list index
                          var type : HomeType, // recyclerview view type
                          val gridSpanCount : Int) { // gridview의 span count


}