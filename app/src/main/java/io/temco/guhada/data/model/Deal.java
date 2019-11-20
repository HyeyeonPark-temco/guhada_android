package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.main.MainBaseModel;

public class Deal {

    @SerializedName("brandId")
    public int brandId;

    @SerializedName("brandName")
    public String brandName;

    @SerializedName("sellerId")
    public int sellerId;

    @SerializedName("sellerName")
    public String sellerName;

    @SerializedName("productId")
    public int productId;

    @SerializedName("productName")
    public String productName;

    @SerializedName("productSeason")
    public String productSeason;

    @SerializedName("imageName")
    public String imageName;

    @SerializedName("productImage")
    public Image productImage;

    @SerializedName("dealId")
    public int dealId;

    @SerializedName("dealName")
    public String dealName;

    @SerializedName("sellPrice")
    public BigDecimal sellPrice;

    @SerializedName("discountPrice")
    public BigDecimal discountPrice;

    @SerializedName("setDiscount")
    public boolean setDiscount;

    @SerializedName("categoryId")
    public int categoryId;

    @SerializedName("options")
    public List<Option> options;

    @SerializedName("discountRate")
    public int discountRate;

    @SerializedName("freeShipping")
    public boolean freeShipping;

    @SerializedName("shipExpenseType")
    public String shipExpenseType;

    @SerializedName("isBoldName")
    public boolean isBoldName;

    @SerializedName("timeDealInfo")
    public TimeDealInfo timeDealInfo;

    @SerializedName("totalStock")
    public int totalStock;

    public boolean isFreeShipping() {
        if (shipExpenseType != null && !"".equals(shipExpenseType))
            return "FREE".equals(shipExpenseType);
        return freeShipping;
    }

    public class Option {

        @SerializedName("type")
        public String type;

        @SerializedName("attributes")
        public String[] attributes;

    }


    @Override
    public String toString() {
        if (CustomLog.getFlag()) return "Deal{" +
                "brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productSeason='" + productSeason + '\'' +
                ", imageName='" + imageName + '\'' +
                ", productImage=" + productImage +
                ", dealId=" + dealId +
                ", dealName='" + dealName + '\'' +
                ", sellPrice=" + sellPrice +
                ", discountPrice=" + discountPrice +
                ", setDiscount=" + setDiscount +
                ", categoryId=" + categoryId +
                ", options=" + options +
                ", discountRate=" + discountRate +
                ", freeShipping=" + freeShipping +
                ", shipExpenseType='" + shipExpenseType + '\'' +
                ", isBoldName=" + isBoldName +
                '}';
        else return "";
    }


    public class TimeDealInfo {
        public Long now = 0L;                       // s
        public Long remainedTimeForStart = 0L;
        public Long remainedTimeForEnd = 0L;        // s
        public Long discountStartAt = 0L;           // ms
        public String statusCode = "";
        public String statusText = "";
    }
}
