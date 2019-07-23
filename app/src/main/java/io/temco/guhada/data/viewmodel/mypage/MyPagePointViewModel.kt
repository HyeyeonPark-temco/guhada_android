package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 포인트
    - 포인트 충전은 일단 제거함
    - 할 수 있으면 일단 레이아웃은 일단 만들고 숨김으로 나중에는 추가됨
    - 전체 / 충전 버튼 제거
    - 사용가능한포인트 레이아웃 제거
    - 적립예정/적립포인트/충전포인트 -> 적립예정/적립포인트
    - 나머지 레이아웃은 그대로
    - 웹 프론트 작업 된 내용 공유
        ○ 포인트 // /point-summary-controller/getPointSummaryUsingGET
        ○ 충전 조회  // /histories
        ○ 사용가능, 적립 /summary
 *
 */
class MyPagePointViewModel (val context : Context) : BaseObservableViewModel() {



}