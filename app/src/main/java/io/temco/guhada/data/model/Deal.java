package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

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

    @SerializedName("imageUrl")
    public String imageUrl;

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

    public class Option {

        @SerializedName("type")
        public String type;

        @SerializedName("attributes")
        public String[] attributes;
    }
}
