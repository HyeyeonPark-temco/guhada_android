package io.temco.guhada.data.model.body;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.search.ProductSearchFilterValue;

public class FilterBody {

    public FilterBody() {
        brandIds = new ArrayList<>();
        categoryIds = new ArrayList<>();
        filters = new ArrayList<>();
        searchQueries = new ArrayList<>();
        searchResultOrder = "";
    }

    public FilterBody(ProductSearchFilterValue value) {
        if(value.getCategoryIds().isEmpty()) categoryIds = null;
        else categoryIds = new ArrayList<>(value.getCategoryIds());

        if(value.getBrandIds().isEmpty()) brandIds = null;
        else brandIds = new ArrayList<>(value.getBrandIds());

        if(value.getFilters().isEmpty()) filters = null;
        else filters.addAll(value.getFilters());

        if(value.getSearchQueries().isEmpty()) searchQueries = null;
        else searchQueries = new ArrayList<>(value.getSearchQueries());

        searchResultOrder = value.getSearchResultOrder();

        shippingCondition = value.getShippingCondition();
        productCondition = value.getProductCondition();
    }

    @SerializedName("brandIds")
    public ArrayList<Integer> brandIds;

    @SerializedName("categoryIds")
    public ArrayList<Integer>  categoryIds;

    @SerializedName("filters")
    public List<FilterBodyAttribute> filters;

    @SerializedName("searchQueries")
    public ArrayList<String>  searchQueries;

    @SerializedName("searchResultOrder")
    public String searchResultOrder;

    @SerializedName("searchCondition")
    public String searchCondition;

    @SerializedName("shippingCondition")
    public String shippingCondition;

    @SerializedName("productCondition")
    public String productCondition;

    @SerializedName("maxPrice")
    public int maxPrice;

    @SerializedName("minPrice")
    public int minPrice;

    @Override
    public String toString() {
        if(CustomLog.getFlag())return "FilterBody{" +
                "brandIds=" + brandIds +
                ", categoryIds=" + categoryIds +
                ", filters=" + filters +
                ", searchQueries=" + searchQueries +
                ", searchResultOrder='" + searchResultOrder + '\'' +
                ", searchCondition='" + searchCondition + '\'' +
                ", shippingCondition='" + shippingCondition + '\'' +
                ", productCondition='" + productCondition + '\'' +
                '}';
        else return "";
    }
}