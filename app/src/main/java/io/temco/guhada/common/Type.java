package io.temco.guhada.common;

public class Type {

    // View
    public enum View {
        TEST,
        SPLASH,
        MAIN,
        STORY,
        PROFILE,
        MY_PAGE,
        LOGIN,
        JOIN,
        FIND_ACCOUNT
    }

    // Server
    public enum Server {

        COMMON(""),
        SEARCH("http://dev.search.guhada.com:9090/"),
        PRODUCT("http://dev.product.guhada.com:8080/"),
        BBS("http://dev.bbs.guhada.com:8081/"),
        USER("http://dev.user.guhada.com:8080/"),
        CLAIM("http://dev.claim.guhada.com:8081/"),
        ORDER("http://dev.order.guhada.com:8080/"),
        PAYMENT("http://dev.payment.guhada.com:8081/"),

        NAVER_PROFILE("https://openapi.naver.com/");

        private String url;

        Server(String url) {
            this.url = url;
        }

        public static String getUrl(Server type) {
            switch (type) {
                case SEARCH:
                    return SEARCH.url;
                case PRODUCT:
                    return PRODUCT.url;
                case BBS:
                    return BBS.url;
                case USER:
                    return USER.url;
                case CLAIM:
                    return CLAIM.url;
                case ORDER:
                    return ORDER.url;
                case PAYMENT:
                    return PAYMENT.url;
                case NAVER_PROFILE:
                    return NAVER_PROFILE.url;
                default:
                    return COMMON.url;
            }
        }
    }


    // RESULT CODE
    public enum ResultCode {
        ALREADY_SIGNED_UP(6006),
        ALREADY_EXIST_EMAIL(6001),
        INVALID_PASSWORD(6002);

        private int code;

        ResultCode(int code) {
        }

        public static int getResultCode(ResultCode code) {
            switch (code) {
                case ALREADY_SIGNED_UP:
                    return ALREADY_SIGNED_UP.code;
                case ALREADY_EXIST_EMAIL:
                    return ALREADY_EXIST_EMAIL.code;
                case INVALID_PASSWORD:
                    return INVALID_PASSWORD.code;
                default:
                    return 0;
            }
        }
    }
}