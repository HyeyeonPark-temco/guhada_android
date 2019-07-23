package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 메인
    - 웹페이지의 회원정보 상단 구성이 마이페이지 메인에 첫 보라색 화면 구성과 같도록
    - 하단의 약관, 회사 정보 footer는 마이페이지 메인 화면에만 있고 나머지 탭에는 없는 구조로
    - 메인 상단에 알림 아이콘은 새로운 창이 떠서 알림설정으로 이동예정(미구현)
    - 메인 상단에 톱니 아이콘은 일단 회원정보 수정탭으로 이동, 나중에 앱설정 화면으로 이동 될 수 있다
    - 메인에 주문배송을 누르면 그냥 화면 전환처리로 적용
    - 웹 프론트 작업 된 내용 공유
        ○ (제일 마지막에 적용해야함 기능 미확정 및 미구현 내용 있음)
        ○ 메인에 썸네일 이미지 없음
        ○ 주문 배송, 쿠폰, 포인트, 토큰 // API 나중에 합쳐주는걸로 협의 아직 미적용
 *
 */
class MyPageMainViewModel (val context : Context) : BaseObservableViewModel() {



}