package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

public class BlockChain {

    @SerializedName("serialId")
    public String serialId;

    @SerializedName("brandName")
    public String brandName;

    @SerializedName("seller")
    public String seller;

    @SerializedName("productName")
    public String productName;

    @SerializedName("price")
    public int price;

    @SerializedName("certificateUrl")
    public String certificateUrl;

    @SerializedName("certificateName")
    public String certificateName;

    @SerializedName("certificateContentType")
    public String certificateContentType;

    @SerializedName("hash")
    public String hash;

    @SerializedName("contractAddress")
    public String contractAddress;
}
