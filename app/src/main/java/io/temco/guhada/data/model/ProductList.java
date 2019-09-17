package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
        return "ProductList{" +
                ", filters=" + filters +
                '}';
    }
}