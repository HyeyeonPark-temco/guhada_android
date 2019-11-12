package io.temco.guhada.common;

import java.io.Serializable;

import io.temco.guhada.BuildConfig;

public class Type {
    ////////////////////////////////////////////////
    // Build
    public enum Build {

        DEV,
        QA,
        STAGE,
        RELEASE
    }

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
        ADD_SHIPPING_ADDRESS,
        TEMP_LOGOUT,
        BLOCKCHAIN,
        SEARCH_ZIP_WEBVIEW,
        CART,
        DELIVERY_DETAIL,
        RECEIPT,
        SEARCH_WORD,
        SIDE_MENU,
        PRODUCT_LIST,
        REQUEST_CANCEL_ORDER,
        REQUEST_EXCHANGE,
        REQUEST_REFUND,
        SUCCESS_CANCEL_ORDER,
        SUCCESS_EXCHANGE,
        SUCCESS_REFUND,
        REVIEW_WRITE,
        IMAGE_GET,
        REVIEW_POINT_DIALOG,
        USER_SIZE_UPDATE,
        USER_INFO,
        REPORT_WRITE,
        CONFIRM_PURCHASE,
        COUPON_DOWNLOAD,
        COUPON_SELECT,
        SELLER_INFO,
        MYPAGE_TEMP_LOGIN,
        USERCLAIM_GUHADA,
        USERCLAIM_SELLER,
        COMMUNITY_TEMP_LIST,
        IMAGE_DETAIL,
        CUSTOM_WEBVIEW,
        VERIFY,
        SHIPPING_TRACKING,
        VERIFY_EMAIL,
        SCHEME,
        LUCKY_EVENT,
        LUCKY_EVENT_DIALOG,
        PHOTO_PAGER,
        CARD_INTEREST
    }

    // Main
    public enum Main {
        CATEGORY,
        BRAND,
        HOME,
        COMMUNITY,
        MY_PAGE
    }

    /** 임시 서버 연결 여부 (true: TempServer name; false: dev) **/
    public static boolean isTempServer= false;
    public static String[] ServerTypeArray = { "http://dev.", "http://qa.", "https://stg.", "https://" };
    public static String serverType = ServerTypeArray[0];
    ////////////////////////////////////////////////
    // Server
    public enum Server {

        COMMON,
        SEARCH,
        PRODUCT,
        BBS,
        USER, // USER("http://172.30.1.30:8080"),
        CLAIM,
        ORDER,
        PAYMENT,
        BLOCKCHAIN, // BlockChain
        LOCAL, // 핸드폰 인증
        NAVER_PROFILE,
        BENEFIT,
        GATEWAY,
        STG_ORDER,
        WEB,
        SETTLE,
        SHIP;

        public static String getUrl(Server type) {
            switch (type) {
                case SEARCH:
                    return getSearchUrl();
                case PRODUCT:
                    return getProductUrl();
                case BBS:
                    return getBBSUrl();
                case USER:
                    return getUserUrl();
                case CLAIM:
                    return getClaimUrl();
                case ORDER:
                    return getOrderUrl();
                case PAYMENT:
                    return getPaymentUrl();
                case NAVER_PROFILE:
                    return "https://openapi.naver.com/";
                case LOCAL:
                    return "http://13.209.10.68/";
                case BLOCKCHAIN:
                    return "http://52.79.95.78:8080/";
                case BENEFIT:
                    return getBenefitUrl();
                case GATEWAY:
                    return getGatewayUrl();
                case STG_ORDER:
                    return "https://stg.order.guhada.com/";
                case WEB:
                    return getWebUrl();
                case SHIP:
                    return getShipUrl();
                case SETTLE:
                    return getSettleUrl();
                default:
                    return "";
            }
        }
    }

    // ProductListViewType
    public enum ProductListViewType implements Serializable {
        CATEGORY,
        BRAND,
        SEARCH,
        VIEW_MORE,
        NONE
    }

    private static String getSettleUrl(){
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://settle.guhada.com/";
            case STAGE:
                return "http://settle.guhada.com/";
            case RELEASE:
                return "http://settle.guhada.com/";
            default:
                if(isTempServer)return "http://settle.guhada.com/";/*serverType+"settle.guhada.com/";*/
                else return "http://dev.settle.guhada.com/";

        }
    }

    private static String getShipUrl(){
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.ship.guhada.com/";
            case STAGE:
                return "https://stg.ship.guhada.com/";
            case RELEASE:
                return "https://ship.guhada.com/";
            default:
                if(isTempServer)return serverType+"ship.guhada.com/";
                else return "http://dev.ship.guhada.com/";
        }
    }

    private static String getSearchUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.search.guhada.com:9090/";
            case STAGE:
                return "https://stg.search.guhada.com:9090/";
            case RELEASE:
                return "https://search.guhada.com/";
            default:
                if(isTempServer)return serverType+"search.guhada.com/";
                else return "http://dev.search.guhada.com:9090/";
        }
    }

    private static String getProductUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.product.guhada.com/";
            case STAGE:
                return "https://stg.product.guhada.com/";
            case RELEASE:
                return "https://product.guhada.com/";
            default:
                if(isTempServer)return serverType+"product.guhada.com/";
                else return "http://dev.product.guhada.com/";
        }
    }

    private static String getBBSUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.bbs.guhada.com/";
            case STAGE:
                return "https://stg.bbs.guhada.com/";
            case RELEASE:
                return "https://bbs.guhada.com/";
            default:
                if(isTempServer)return serverType+"bbs.guhada.com/";
                else return "http://dev.bbs.guhada.com/";
        }
    }

    private static String getUserUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.user.guhada.com/";
            case STAGE:
                return "https://stg.user.guhada.com/";
            case RELEASE:
                return "https://user.guhada.com/";
            default:
                if(isTempServer)return serverType+"user.guhada.com/";
                else return "http://dev.user.guhada.com/";
        }
    }

    private static String getClaimUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.claim.guhada.com/";//:8081
            case STAGE:
                return "https://stg.claim.guhada.com/";//:8081
            case RELEASE:
                return "https://claim.guhada.com/";
            default:
                if(isTempServer)return serverType+"claim.guhada.com/";
                else return "http://dev.claim.guhada.com/";//:8081
        }
    }

    private static String getOrderUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.order.guhada.com/";
            case STAGE:
                return "https://stg.order.guhada.com/";
            case RELEASE:
                return "https://order.guhada.com/";
            default:
                if(isTempServer)return serverType+"order.guhada.com/";
                else return "http://dev.order.guhada.com/";
        }
    }

    private static String getPaymentUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.payment.guhada.com:8081/";
            case STAGE:
                return "https://stg.payment.guhada.com/";
            case RELEASE:
                return "https://payment.guhada.com/";
            default:
                if(isTempServer)return serverType+"payment.guhada.com/";
                else return "http://dev.payment.guhada.com:8081/";
        }
    }

    private static String getBenefitUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.benefit.guhada.com/";
            case STAGE:
                return "https://stg.benefit.guhada.com/";
            case RELEASE:
                return "https://benefit.guhada.com/";
            default:
                if(isTempServer)return serverType+"benefit.guhada.com/";
                else return "http://dev.benefit.guhada.com" +"/";
        }
    }

    private static String getGatewayUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.gateway.guhada.com/";
            case STAGE:
                return "https://stg.gateway.guhada.com/";
            case RELEASE:
                return "https://gateway.guhada.com/";
            default:
                if(isTempServer)return serverType+"gateway.guhada.com/";
                else return "http://dev.gateway.guhada.com/";
        }
    }


    private static String getWebUrl() {
        switch (BuildConfig.BuildType) {
            case QA:
                return "http://qa.guhada.com/";
            case STAGE:
                return "https://stg.guhada.com/";
            case RELEASE:
                return "https://www.guhada.com/";
            default:
                if(isTempServer)return serverType+"guhada.com/";
                else return "http://dev.guhada.com/";
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
    public enum Category implements Serializable {

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
        MARKS("SCORE"), // 평점순
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
        COLOR2("COLOR"),
        TEXT_BUTTON("TEXT_BUTTON"),
        RGB("RGB"),
        TEXT("TEXT");

        private String type;

        ProductOption(String type) {
            this.type = type;
        }

        public static String get(ProductOption type) {
            switch (type) {
                case COLOR:
                    return COLOR.type;
                case COLOR2:
                    return COLOR2.type;
                case TEXT_BUTTON:
                    return TEXT_BUTTON.type;
                case TEXT:
                    return TEXT.type;
                case RGB:
                    return RGB.type;
                default:
                    return NONE.type;
            }
        }

        /**
         * @Author park jungho
         * 19.07.18
         * RGB 값추가 - 메인 홈에서 deal에 사용
         */
        public static ProductOption getType(String type) {
            if (type.equalsIgnoreCase(COLOR.type)) {
                return COLOR;
            } else if (type.equalsIgnoreCase(COLOR2.type)) {
                return COLOR2;
            } else if (type.equalsIgnoreCase(TEXT_BUTTON.type)) {
                return TEXT_BUTTON;
            } else if (type.equalsIgnoreCase(TEXT.type)) {
                return TEXT;
            } else if (type.equalsIgnoreCase(RGB.type)) {
                return RGB;
            } else {
                return NONE;
            }
        }
    }

    ////////////////////////////////////////////////
    // DateFormat
    public enum DateFormat {

        TYPE_1("yyyy.MM.dd"),
        TYPE_2("yyyy-MM-dd"),
        TYPE_3("yyyy-MM-dd HH:mm:ss"),
        TYPE_4("yy.MM.dd HH:mm"),
        TYPE_5("yy.MM.dd HH:mm");

        private String type;

        DateFormat(String type) {
            this.type = type;
        }

        public static String get(DateFormat type) {
            switch (type) {
                case TYPE_2:
                    return TYPE_2.type;
                case TYPE_3:
                    return TYPE_3.type;
                case TYPE_4:
                    return TYPE_4.type;
                case TYPE_5:
                    return TYPE_5.type;
                default:
                    return TYPE_1.type;
            }
        }
    }

    ////////////////////////////////////////////////
    // Tag
    public enum Tag {

        TYPE_NORMAL(0),
        TYPE_FULL(1);

        private int type;

        Tag(int type) {
            this.type = type;
        }

        public static int get(Tag type) {
            switch (type) {
                case TYPE_FULL:
                    return TYPE_FULL.type;
                default:
                    return TYPE_NORMAL.type;
            }
        }

        public static Tag getType(int type) {
            if (type == TYPE_FULL.type) {
                return TYPE_FULL;
            } else {
                return TYPE_NORMAL;
            }
        }
    }

    ////////////////////////////////////////////////


    ////////////////////////////////////////////////

    /**
     * @author park jungho
     * 19.07.31
     * <p>
     * USER BOOKMARK 회원 북마크 target
     * http://dev.user.guhada.com/swagger-ui.html#/USER_BOOKMARK
     */
    public enum BookMarkTarget {
        PRODUCT("PRODUCT"),
        DEAL("DEAL"),
        BBS("BBS"),
        COMMENT("COMMENT"),
        STORE("STORE"),
        REVIEW("REVIEW"),
        SELLER("SELLER");


        private String type;

        BookMarkTarget(String type) {
            this.type = type;
        }

        public static String get(BookMarkTarget type) {
            switch (type) {
                case PRODUCT:
                    return PRODUCT.type;
                case DEAL:
                    return DEAL.type;
                case BBS:
                    return BBS.type;
                case COMMENT:
                    return COMMENT.type;
                case STORE:
                    return STORE.type;
                case REVIEW:
                    return REVIEW.type;
                case SELLER:
                    return SELLER.type;
                default:
                    return PRODUCT.type;

            }
        }

        public static BookMarkTarget getType(String type) {
            if (type.equalsIgnoreCase(PRODUCT.type)) {
                return PRODUCT;
            } else if (type.equalsIgnoreCase(DEAL.type)) {
                return DEAL;
            } else if (type.equalsIgnoreCase(BBS.type)) {
                return BBS;
            } else if (type.equalsIgnoreCase(COMMENT.type)) {
                return COMMENT;
            } else if (type.equalsIgnoreCase(STORE.type)) {
                return STORE;
            } else if (type.equalsIgnoreCase(REVIEW.type)) {
                return REVIEW;
            } else if (type.equalsIgnoreCase(SELLER.type)) {
                return SELLER;
            } else {
                return PRODUCT;
            }
        }
    }


    /**
     * @author park jungho
     * 19.08.14
     * <p>
     * USER BOOKMARK 회원 북마크 target
     * http://dev.user.guhada.com/swagger-ui.html#/USER_BOOKMARK
     */
    public enum PointResultDialogType implements Serializable {
        COMPLETE_PAYMENT("COMPLETE_PAYMENT"),
        REVIEW_WRITE("REVIEW_WRITE"),
        SIZE_UPDATE("SIZE_UPDATE"),
        NONE("NONE");


        private String type;

        PointResultDialogType(String type) {
            this.type = type;
        }

        public static String get(PointResultDialogType type) {
            switch (type) {
                case COMPLETE_PAYMENT:
                    return COMPLETE_PAYMENT.type;
                case REVIEW_WRITE:
                    return REVIEW_WRITE.type;
                case SIZE_UPDATE:
                    return SIZE_UPDATE.type;
                default:
                    return NONE.type;

            }
        }

        public static PointResultDialogType getType(String type) {
            if (type.equalsIgnoreCase(COMPLETE_PAYMENT.type)) {
                return COMPLETE_PAYMENT;
            } else if (type.equalsIgnoreCase(REVIEW_WRITE.type)) {
                return REVIEW_WRITE;
            } else if (type.equalsIgnoreCase(SIZE_UPDATE.type)) {
                return SIZE_UPDATE;
            } else {
                return NONE;
            }
        }
    }

    // ProductListViewType
    public enum SerchFilterCondition implements Serializable {
        NEW,
        BEST,
        PLUS
    }

    ////////////////////////////////////////////////
}