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
        CATEGORY,
        LOGIN,
        JOIN,
        FIND_ACCOUNT,
        TERMS,
        VERIFY_PHONE // 임시
    }

    // Main
    public enum Main {
        CATEGORY,
        BRAND,
        HOME,
        COMMUNITY,
        MY_PAGE
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

        NAVER_PROFILE("https://openapi.naver.com/"),

        // 핸드폰 인증
        LOCAL("http://13.209.10.68/");

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
                case LOCAL :
                    return LOCAL.url;
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


    // List
    public enum List {

        CONTENTS(0),
        HEADER(1),
        FOOTER(2);

        private int type;

        List(int type) {
            this.type = type;
        }

        public static int get(List type) {
            switch (type) {
                case HEADER:
                    return HEADER.type;
                case FOOTER:
                    return FOOTER.type;
                default:
                    return CONTENTS.type;
            }
        }

        public static List getType(int type) {
            if (type == HEADER.type) {
                return HEADER;
            } else if (type == FOOTER.type) {
                return FOOTER;
            } else {
                return CONTENTS;
            }
        }
    }

    // Category
    public enum Category {

        NORMAL(0), // 일반
        ALL(1), // 전체보기
        GRID(2), // Grid
        GRID_COLOR(3); // Grid 컬러

        private int type;

        Category(int type) {
            this.type = type;
        }

        public static int get(Category type) {
            switch (type) {
                case ALL:
                    return ALL.type;
                case GRID:
                    return GRID.type;
                case GRID_COLOR:
                    return GRID_COLOR.type;
                default:
                    return NORMAL.type;
            }
        }

        public static Category getType(int type) {
            if (type == ALL.type) {
                return ALL;
            } else if (type == GRID.type) {
                return GRID;
            } else if (type == GRID_COLOR.type) {
                return GRID_COLOR;
            } else {
                return NORMAL;
            }
        }
    }

    public enum CategoryData {

        NONE(0),
        FEMALE(1),  // 여성
        MALE(2), // 남성
        KIDS(3); // 키즈

        private int type;

        CategoryData(int type) {
            this.type = type;
        }

        public static int get(CategoryData type) {
            switch (type) {
                case FEMALE:
                    return FEMALE.type;
                case MALE:
                    return MALE.type;
                case KIDS:
                    return KIDS.type;
                default:
                    return NONE.type;
            }
        }

        public static CategoryData getType(int type) {
            if (type == FEMALE.type) {
                return FEMALE;
            } else if (type == MALE.type) {
                return MALE;
            } else if (type == KIDS.type) {
                return KIDS;
            } else {
                return NONE;
            }
        }
    }
}