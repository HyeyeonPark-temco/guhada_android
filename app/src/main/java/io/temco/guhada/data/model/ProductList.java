package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.temco.guhada.common.util.CustomLog;

public class ProductList {

    @SerializedName("brands")
    public List<Brand> brands;

    @SerializedName("categories")
    public List<Category> categories;

    @SerializedName("filters")
    public List<Filter> filters;

    @SerializedName("countOfDeals")
    public int countOfDeals;

    @SerializedName("deals")
    public List<Deal> deals;

    @Override
    public String toString() {
        if(CustomLog.getFlag())return "ProductList{" +
                "brands=" + brands +
                ", categories=" + categories +
                ", filters=" + filters +
                ", countOfDeals=" + countOfDeals +
                ", deals=" + deals +
                '}';
        else return "";
    }
}