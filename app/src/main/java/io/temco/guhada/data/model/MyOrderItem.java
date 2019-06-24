package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class MyOrderItem {

    @SerializedName("brandName")
    public String brandName; // 브랜드명

    @SerializedName("discountPrice")
    public BigDecimal discountPrice; // 할인된 가격

    @SerializedName("expireDate")
    public String expireDate; // 무통장 입금시 입금기한(무통장이 아니면경우 null)

    @SerializedName("imageName")
    public String imageName; // 대표 이미지 파일명

    @SerializedName("imageUrl")
    public String imageUrl; // 대표 이미지 URL

    @SerializedName("optionAttribute1")
    public String optionAttribute1; // 첫번째 옵션

    @SerializedName("optionAttribute2")
    public String optionAttribute2; // 두번째 옵션

    @SerializedName("optionAttribute3")
    public String optionAttribute3; // 세번째 옵션

    @SerializedName("orderDate")
    public String orderDate; // 주문일

    @SerializedName("orderPrice")
    public BigDecimal orderPrice; // 주문한 금액

    @SerializedName("orderProdGroupId")
    public int orderProdGroupId; // 주문그룹 아이디

    @SerializedName("originalPrice")
    public BigDecimal originalPrice; // 원래 가격

    @SerializedName("prodName")
    public String prodName; // 상품명

    @SerializedName("productId")
    public int productId; // 상품 아이디

    @SerializedName("purchaseConfirm")
    public boolean purchaseConfirm; // 주문 확정여부

    @SerializedName("purchaseId")
    public int purchaseId; // 구매 데이터의 아이디

    @SerializedName("purchaseStatus")
    public String purchaseStatus; // 주문의 상태값

    @SerializedName("purchaseStatusText")
    public String purchaseStatusText; // 주문의 상태값

    @SerializedName("quantity")
    public int quantity; // 구매수량

    @SerializedName("season")
    public String season; // 시즌

    @SerializedName("sellerId")
    public int sellerId; // 판매자의 아이디

    @SerializedName("sellerName")
    public String sellerName; // 판매자의 이름

    @SerializedName("shipCompleteDate")
    public String shipCompleteDate; // 배송완료일자

    @SerializedName("shipPrice")
    public BigDecimal shipPrice; // 배송비

    @SerializedName("statusMessage")
    public String statusMessage; // 상태의 따른 메세지


    public boolean checkReview = false;
}