package io.temco.guhada.common.enum

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

    enum class Product(val eventName: String) {
        View_Product("View_Product"),
        Buy_Product("Buy_Product")
    }

    enum class Cart(val eventName: String) {
        Add_To_Cart("Add_To_Cart")
    }


    enum class MainEvent(val eventName: String) {
        View_Time_Deal_Product("View_Time_Deal_Product"),
        View_Lucky_Event_Product("luckydraw"),
    }
}

















