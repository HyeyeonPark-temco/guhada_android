package io.temco.guhada.data.model.shipping

/**
 * 배송 조회-배송 상태 상세 정보 model
 * @author Hyeyeon Park
 * @since 2019.09.26
 */
class ShippingTrackingDetail {
    var trackingInfoId = 0L
    var code: String? = ""           // 배송상태 코드
    var kind = ""           // 진행상태
    var level = 0          // 진행단계
    var where = ""          // 진행위치지점 (json 요청시)
    var manName = ""        // 배송기사 이름
    var manPic = ""         // 배송기사 전화번호
    var telno = ""          // 진행위치(지점)전화번호 (json 요청시)
    var telno2 = ""         // 배송기사 전화번호 (json 요청시)
    var remark: String? = ""         // 비고
    var time = 0L           // 진행시간 (json 요청시)
    var timeString = ""     // 진행시간

    fun getTimeText() = timeString.split(" ")[1]
    fun getDateText() = timeString.split(" ")[0]
    fun getWhereText(): String {
        return if (manName.isEmpty()) where
        else "$where ($manName $telno)"
    }
}





