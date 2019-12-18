package io.temco.guhada.data.model

import io.temco.guhada.common.util.CustomLog

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
                         val detail : PlanningDetailData,
                         val title : String,
                         val link : String,
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



class PlanningDetailData{
    var id = 0
    var mainUse = false
    var title = ""
    var largeImageUrl : Any? = null
    var mediumImageUrl : Any? = null
    var mobileImageUrl : Any? = null
    var communityImageUrl : Any? = null
    var link : Any? = null
    var backgroundColor : Any? = null
    var mobileAppLink : Any? = null
    var startDate : String = ""
    var endDate : String = ""
    var detailTitle : String = ""
    var totalItemCount : Int = 0
    var planListDetails : ArrayList<Deal> = arrayListOf()

    override fun toString(): String {
        if(CustomLog.flag)return "PlanningDetailData(id=$id, mainUse=$mainUse, title='$title', largeImageUrl=$largeImageUrl, mediumImageUrl=$mediumImageUrl, mobileImageUrl=$mobileImageUrl, communityImageUrl=$communityImageUrl, link=$link, backgroundColor=$backgroundColor, mobileAppLink=$mobileAppLink)"
        else return ""
    }
}



class PlanListDetail {
    var dealId = 0
    var sellerId = 0
    var dealName = ""
    var productStatus = ""
    var sellPrice : Double = 0.0
    var discountType = ""
    var discountValue : Double = 0.0
    var nameDefault = ""
    var season = ""
    var nickname = ""
    var shipExpenseType = ""
    var imgUrl = ""
}