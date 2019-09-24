package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProductByList implements Serializable {

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

    @SerializedName("imageName")
    public String imageName;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("dealId")
    public int dealId;

    @SerializedName("dealName")
    public String dealName;

    @SerializedName("sellPrice")
    public int sellPrice;

    @SerializedName("discountRate")
    public int discountRate;

    @SerializedName("discountPrice")
    public int discountPrice;

    @SerializedName("shipExpenseType")
    public String shipExpenseType;

    @SerializedName("productSeason")
    public String productSeason;

    @SerializedName("options")
    public List options;

    @SerializedName("isBoldName")
    public boolean isBoldName;
}
