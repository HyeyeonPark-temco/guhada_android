package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 주문배송
    - 주문 배송 화면으로 제품정보 이미지를 누르면 제품 상세 화면으로 이동 ( 이건 새로운 activity 로 떠서 위에 열림, 햄버거 아이콘은 빼도 된다고 이야기 했음)
    - 주문목록에서 주문내역상세로 이동 새로운 화면으로 처리
    - 주문배송 화면에서 취소교환환불 버튼을 누르면 별도의 팝업 또는 전체 화면이 떠서 정보를 입력하고 확인 하면 리스트 갱신
    - Api 연동은 서버에서 리뷰를 위한 개발을 해서 자세한 연동은 말고 화면개발 먼저
    - 웹 프론트 작업 된 내용 공유
        ○ 주문배송 목록 // /order/my-orders-status (취소, 교환, 반품을 제외한 내역)
        ○ 주문 취소 // /order-claim/order-cancel
        ○ 배송지 변경 // /order/order-update/shipping-address
        ○ 문의하기 // /users/my-page/inquiries
        ○ 주문자, 배송지, 결제 // /order/order-complete/{purchaseId} (한주문에 전체를 가져오기때문에 목록에서 필요한 값을 뽑아서 써야함)
        ○ 환불금액 정보는 데이터가 없어서 현재 구현 못함
        ○ 주문상태 //  /order/my-orders-status (전체)
        ○ 기간조회 // /order/my-orders/{startDt}/{endDt}/{page}
 *
 */
class MyPageDeliveryViewModel (val context : Context) : BaseObservableViewModel() {



}