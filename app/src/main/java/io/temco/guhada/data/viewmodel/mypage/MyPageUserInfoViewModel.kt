package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 회원정보수정
    - 앱을 실행하여 인트로에서 관련 플래그를 리셋하고 리셋된경우 해당탭에 진입했을때 비밀번호 입력화면을 넣고 입력을 한번 해야 내부 정보를 볼 수 있도록
    - 앱을 실행하여 해당 비밀번호를 입력을 한번 하면 앱을 다시 껏다 키지 않는한 회원 정보를 계속 볼 수 있다
    - 만약 회원정보수정화면에서 비밀번호를 입력하고 앱을 끄고 다시 진입하면 로그인을 다시 해야 한다
    - 웹 프론트 작업 된 내용 공유
        ○ 내 사이즈 수정 // /users/user-size
        ○ 비밀번호 1회 재입력은 로그인 API를 한번 더 호출하는 방식으로 처리되어있음
 *
 */
class MyPageUserInfoViewModel (val context : Context) : BaseObservableViewModel() {



}