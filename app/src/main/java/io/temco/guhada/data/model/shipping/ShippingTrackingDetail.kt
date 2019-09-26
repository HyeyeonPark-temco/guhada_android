package io.temco.guhada.data.model.shipping

/**
 * 배송 조회-배송 상태 상세 정보 model
 * @author Hyeyeon Park
 * @since 2019.09.26
 */
class ShippingTrackingDetail {
    var code = ""           // 배송상태 코드
    var kind = ""           // 진행상태
    var level = ""          // 진행단계
    var manName = ""        // 배송기사 이름
    var manPic = ""         // 배송기사 전화번호
    var remark = ""         // 비고
    var telno = ""          // 진행위치(지점)전화번호 (json 요청시)
    var telno2 = ""         // 배송기사 전화번호 (json 요청시)
    var time = ""           // 진행시간 (json 요청시)
    var timeString = ""     // 진행시간
    var trans_telno = ""    // 진행위치(지점)전화번호 (xml 요청시)
    var trans_telno2 = ""   // 배송기사 전화번호 (xml 요청시)
    var trans_time = ""     // 진행시간 (xml 요청시)
    var trans_where = ""    // 진행위치지점 (xml 요청시)
    var where = ""          // 진행위치지점 (json 요청시)
}
