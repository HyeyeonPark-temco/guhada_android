package io.temco.guhada.common;

public class Flag {
    public static class RequestCode {
        public static final int BASE = 100;
        public static final int GO_TO_MAIN = -9999;
        public static final int GO_TO_MAIN_HOME = -9997;
        public static final int GO_TO_MAIN_COMUNITY = -9996;
        public static final int GO_TO_MAIN_MYPAGE = -9995;
        public static final int IMAGE_GALLERY = 5001;
        public static final int POINT_RESULT_DIALOG = 5003;
        public static final int VERIFY_PHONE = 10001;

        public static final int KAKAO_LOGIN = 10002;
        public static final int NAVER_LOGIN = 10003;
        public static final int FACEBOOK_LOGIN = 64206;
        public static final int GOOGLE_LOGIN = 10005;
        public static final int RC_GOOGLE_LOGIN = 10006;

        public static final int WRITE_CLAIM = 10007;
        public static final int LOGIN = 101;//10008;
        public static final int PAYMENT = 10009;
        public static final int PAYMENT_WEBVIEW= 10010;
        public static final int SHIPPING_ADDRESS = 10011;
        public static final int EDIT_SHIPPING_ADDRESS = 10012;
        public static final int ADD_SHIPPING_ADDRESS = 10013;
        public static final int SEARCH_ZIP = 10014;
        public static final int MODIFY_CLAIM = 10015;
        public static final int PRODUCT_DETAIL = 10016;
        public static final int SEARCH_WORD = 10017;
        public static final int SIDE_MENU = 10018;
        public static final int SIDE_MENU_FROM_PRODUCT_FILTER = 10019;
        public static final int REVIEW_WRITE = 10020;
        public static final int REVIEW_MODIFY = 10021;
        public static final int USER_SIZE = 10022;

        public static final int CONFIRM_PURCHASE = 10023;
        public static final int CANCEL_ORDER = 10024;
        public static final int EXCHANGE = 10025;
        public static final int DELIVERY = 10026;

        public static final int COMMUNITY_DETAIL = 10027;
        public static final int REPORT = 10029;
        public static final int COMMUNITY_DETAIL_WRT_MOD = 10031;
        public static final int COMMUNITY_DETAIL_TEMP_LIST = 10032;
        public static final int USER_CLAIM_GUHADA = 10033;
        public static final int USER_CLAIM_SELLER = 10034;
    }

    public static class ResultCode {
        public static final int SUCCESS = 200;
        public static final int GO_TO_MAIN = -9999;
        public static final int GO_TO_MAIN_HOME = -9997;
        public static final int GO_TO_MAIN_COMUNITY = -9996;
        public static final int GO_TO_MAIN_MYPAGE = -9995;
        public static final int ORDER_NOT_VALID_ERROR = 1001;
        public static final int DATA_NOT_FOUND = 5004;
        public static final int NOT_FOUND_VERIFY_INFO = 5400;
        public static final int ALREADY_EXIST_EMAIL = 6001;
        public static final int SIGNUP_INVALID_PASSWORD = 6002;
        public static final int SIGNIN_INVALID_PASSWORD = 6003;
        public static final int EXPIRED_VERIFICATION_NUMBER = 6004;
        public static final int ALREADY_SIGNED_UP = 6006;
        public static final int INVALID_VERIFICATION_NUMBER = 6007;
        public static final int USER_NOT_FOUND = 6016;
        public static final int NEED_TO_LOGIN = 6017;
        public static final int PRODUCT_RESOURCE_NOT_FOUND = 8404;

    }
}
