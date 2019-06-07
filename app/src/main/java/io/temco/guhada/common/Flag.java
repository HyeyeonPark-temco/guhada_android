package io.temco.guhada.common;

public class Flag {
    public static class RequestCode {
        public static final int VERIFY_PHONE = 10001;
        public static final int KAKAO_LOGIN = 10002;
        public static final int NAVER_LOGIN = 10003;
        public static final int FACEBOOK_LOGIN = 1004;
        public static final int GOOGLE_LOGIN = 10005;
        public static final int RC_GOOGLE_LOGIN = 10006;
        public static final int WRITE_CLAIM = 10007;
    }

    public static class ResultCode {
        public static final int SUCCESS = 200;

        public static final int DATA_NOT_FOUND = 5004;
        public static final int ALREADY_EXIST_EMAIL = 6001;
        public static final int SIGNUP_INVALID_PASSWORD = 6002;
        public static final int SIGNIN_INVALID_PASSWORD = 6003;
        public static final int EXPIRED_VERIFICATION_NUMBER = 6004;
        public static final int ALREADY_SIGNED_UP = 6006;
        public static final int INVALID_VERIFICATION_NUMBER = 6007;
        public static final int USER_NOT_FOUND = 6016;
        public static final int NEED_TO_LOGIN = 6017;
    }
}
