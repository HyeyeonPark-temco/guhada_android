package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * created 19.07.22
 * @author park jungho
 *
 * @author Hyeyeon Park
 * @since 2019.08.05
 *
 * 취소교환환불
    - 해당 탭의 제품 요청, 처리중, 완료 리스트
    - 제플린에 신청 입력 화면과 같은건 일단 무시하고 리스트로만 보이도록
    - 웹 프론트 작업 된 내용 공유
        ○ 주문 취소 신청  // /order-claim/order-cancel
        ○ 환불금액 정보는 데이터가 없어서 현재 구현 못함
 *
 */
class MyPageDeliveryCerViewModel (val context : Context) : BaseObservableViewModel() {

    fun onClickMore(){

    }

}