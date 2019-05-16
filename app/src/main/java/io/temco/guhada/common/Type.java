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
        LOGIN
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
        PAYMENT("http://dev.payment.guhada.com:8081/");

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
                default:
                    return COMMON.url;
            }
        }
    }
}