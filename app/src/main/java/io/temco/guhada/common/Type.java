package io.temco.guhada.common;

public class Type {

    ////////////////////////////////////////////////
    // View
    public enum View {
        TEST,
        SPLASH,
        MAIN,
        STORY,
        PROFILE,
        MY_PAGE,
        CATEGORY,
        BRAND,
        PRODUCT_DETAIL,
        LOGIN,
        JOIN,
        FIND_ACCOUNT,
        TERMS,
        VERIFY_PHONE,
        CLAIM,
        PAYMENT,
        PAYMENT_RESULT,
        PAYMENT_WEBVIEW,
        SHIPPING_ADDRESS,
        TEMP_LOGOUT
    }

    // Main
    public enum Main {
        CATEGORY,
        BRAND,
        HOME,
        COMMUNITY,
        MY_PAGE
    }

    ////////////////////////////////////////////////
    // Server
    public enum Server {

        COMMON(""),
        SEARCH("http://dev.search.guhada.com:9090/"),
        PRODUCT("http://dev.product.guhada.com:8080/"),
        BBS("http://dev.bbs.guhada.com:8081/"),
        USER("http://dev.user.guhada.com:8080/"),
        //        USER("http://172.30.1.30:8080"),
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
                case LOCAL:
                    return LOCAL.url;
                default:
                    return COMMON.url;
            }
        }
    }

    ////////////////////////////////////////////////
    // Language
    public enum Language {

        KOREA("ko"),
        AMERICA("en");

        private String type;

        Language(String type) {
            this.type = type;
        }

        public static String get(Language type) {
            switch (type) {
                case AMERICA:
                    return AMERICA.type;

                default:
                    return KOREA.type;
            }
        }

        public static Language getType(String type) {
            if (type.equals(AMERICA.type)) {
                return AMERICA;
            } else {
                return KOREA;
            }
        }
    }

    ////////////////////////////////////////////////
    // List
    public enum List {

        CONTENTS(0),
        HEADER(1),
        FOOTER(2),
        MORE(3);

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
                case MORE:
                    return MORE.type;
                default:
                    return CONTENTS.type;
            }
        }

        public static List getType(int type) {
            if (type == HEADER.type) {
                return HEADER;
            } else if (type == FOOTER.type) {
                return FOOTER;
            } else if (type == MORE.type) {
                return MORE;
            } else {
                return CONTENTS;
            }
        }
    }

    public enum Grid {

        ONE(1),
        TWO(2),
        THREE(3);

        private int type;

        Grid(int type) {
            this.type = type;
        }

        public static int get(Grid type) {
            switch (type) {
                case ONE:
                    return ONE.type;
                case TWO:
                    return TWO.type;
                case THREE:
                    return THREE.type;
                default:
                    return 0;
            }
        }

        public static Grid getType(int type) {
            if (type == TWO.type) {
                return TWO;
            } else if (type == THREE.type) {
                return THREE;
            } else {
                return ONE;
            }
        }
    }

    ////////////////////////////////////////////////
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

    ////////////////////////////////////////////////
    // Product
    public enum ProductOrder {

        NEW_PRODUCT("DATE"), // 신상품순
        MARKS(""), // 평점순
        LOW_PRICE("PRICE_ASC"), // 낮은가격순
        HIGH_PRICE("PRICE_DESC"); // 높은가격순

        private String type;

        ProductOrder(String type) {
            this.type = type;
        }

        public static String get(ProductOrder type) {
            switch (type) {
                case MARKS:
                    return MARKS.type;
                case LOW_PRICE:
                    return LOW_PRICE.type;
                case HIGH_PRICE:
                    return HIGH_PRICE.type;
                default:
                    return NEW_PRODUCT.type;
            }
        }
    }

    public enum ProductOption {

        NONE(""),
        COLOR("RGB_BUTTON"),
        TEXT_BUTTON("TEXT_BUTTON"),
        TEXT("TEXT");

        private String type;

        ProductOption(String type) {
            this.type = type;
        }

        public static String get(ProductOption type) {
            switch (type) {
                case COLOR:
                    return COLOR.type;
                case TEXT_BUTTON:
                    return TEXT_BUTTON.type;
                case TEXT:
                    return TEXT.type;
                default:
                    return NONE.type;
            }
        }

        public static ProductOption getType(String type) {
            if (type.equals(COLOR.type)) {
                return COLOR;
            } else if (type.equals(TEXT_BUTTON.type)) {
                return TEXT_BUTTON;
            } else if (type.equals(TEXT.type)) {
                return TEXT;
            } else {
                return NONE;
            }
        }
    }

    ////////////////////////////////////////////////
}