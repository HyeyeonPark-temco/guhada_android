package io.temco.guhada.data.model.shipping

/**
 * 배송 조회-배송 상태 정보 model
 * @author Hyeyeon Park
 * @since 2019.09.26
 */
class ShippingTrackingInfo {
    var adUrl = ""              // 택배사에서 광고용으로 사용하는 주소
    var complete = ""           // 배송 완료 여부(true or false)
    var completeYN = ""         // 배송 완료 여부 (Y,N)
    var estimate = ""           // 배송예정 시간
    var invoiceNo = ""          // 운송장 번호
    var itemImage = ""          // 상품 이미지 url
    var itemName = ""           // 상품 명
    var level = ""              // 진행단계 [level 1: 배송준비중, 2: 집화완료, 3: 배송중, 4: 지점 도착, 5: 배송출발, 6:배송 완료]
    var orderNumber = ""        // 주분 번호
    var productInfo = ""        // 상품 정보
    var receiverAddr = ""       // 받는 사람 주소
    var receiverName = ""       // 받는 사람
    var recipient = ""          // 수령인 정보
    var result = ""             // 조회 결과
    var senderName = ""         // 보내는 사람
    var zipCode = ""            // 우편 번호

    var firstDetail = ""        //
    var lastDetail = ""         // 배송 완료 후 정보
    var lastStateDetail = ""    // 배송 완료 후 정보
    var trackingDetails = mutableListOf<ShippingTrackingDetail>()
}