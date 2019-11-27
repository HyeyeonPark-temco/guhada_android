package io.temco.guhada.common.enum

/**
 * 코차바 트래킹 Event name
 * @author Hyeyeon Park
 */
object TrackingEvent {

    enum class Login(val eventName: String) {
        Login_MainP_CancelButton("Login_MainP_CancelButton"),
        Login_MainP_FindIdButton("Login_MainP_FindIdButton"),
        Login_MainP_FindPwButton("Login_MainP_FindPwButton"),
        Login_MainP_InputId("Login_MainP_InputId"),
        Login_MainP_Inputpw("Login_MainP_Inputpw"),
        Login_MainP_SaveIdClick("Login_MainP_SaveIdClick"),
        Login_MainP_LoginButton("Login_MainP_LoginButton"),
        Login_MainP_SignUpButton("Login_MainP_SignUpButton"),
        Login_MainP_NaverLoginButton("Login_MainP_NaverLoginButton"),
        Login_MainP_KakaoLoginButton("Login_MainP_KakaoLoginButton"),
        Login_MainP_FacebookLoginButton("Login_MainP_FacebookLoginButton"),
        Login_MainP_GoogleLoginButton("Login_MainP_GoogleLoginButton"),
        SignUp_Page1_InputId("SignUp_Page1_InputId"),
        SignUp_Page1_Inputpw("SignUp_Page1_Inputpw")

//        Login_MainP_AutomaticLoginClick("Login_MainP_AutomaticLoginClick")    // [자동 로그인] 없음
//        Login_MainP_CheckOrderButton("Login_MainP_CheckOrderButton")          // [비회원 주문조회] 없음
    }

    /**
     * 회원가입: SignUp_Success (value: EMAIL/ KAKAO/ NAVER/ GOOGLE/ FACEBOOK)
     */
    enum class SignUp(val eventName: String){
        SUCCESS_SIGNUP("SignUp_Success")
    }

    /**
     * 상품 상세 조회: View_Product
     * 결제 완료: Buy_Product
     */
    enum class Product(val eventName: String) {
        View_Product("View_Product"),
        Buy_Product("Buy_Product")
    }

    /**
     * 장바구니 담기 완료: Add_To_Cart
     */
    enum class Cart(val eventName: String) {
        Add_To_Cart("Add_To_Cart")
    }

    /**
     * 타임딜 리스트 조회: View_Time_Deal_Product
     * 럭키드로우 리스트 조회: luckydraw
     * 럭키드로우 응모 완료: luckydraw_request
     */
    enum class MainEvent(val eventName: String) {
        View_Time_Deal_Product("View_Time_Deal_Product"),
        View_Lucky_Event_Product("luckydraw"),
        View_Lucky_Event_Request_Product("luckydraw_request")
    }
}

















