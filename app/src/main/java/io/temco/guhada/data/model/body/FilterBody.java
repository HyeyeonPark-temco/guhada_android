package io.temco.guhada.data.model.body;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import io.temco.guhada.data.model.search.ProductSearchFilterValue;

public class FilterBody {

    public FilterBody() {
    }

    public FilterBody(ProductSearchFilterValue value) {
        if(value.getCategoryIds().isEmpty()) categoryIds = null;
        else{
            categoryIds = new int[value.getCategoryIds().size()];
            for (int i = 0; i < categoryIds.length; i++)   categoryIds[i] = value.getCategoryIds().get(i);
        }

        if(value.getBrandIds().isEmpty()) brandIds = null;
        else{
            brandIds = new int[value.getBrandIds().size()];
            for (int i = 0; i < brandIds.length; i++)   brandIds[i] = value.getBrandIds().get(i);
        }
        if(value.getFilters().isEmpty()) filters = null;
        else filters.addAll(value.getFilters());

        if(value.getSearchQueries().isEmpty()) searchQueries = null;
        else{
            searchQueries = new String[value.getSearchQueries().size()];
            for (int i = 0; i < searchQueries.length; i++)   searchQueries[i] = value.getSearchQueries().get(i);
        }

        searchResultOrder = value.getSearchResultOrder();
    }

    @SerializedName("brandIds")
    public int[] brandIds;

    @SerializedName("categoryIds")
    public int[] categoryIds;

    @SerializedName("filters")
    public List<FilterBodyAttribute> filters;

    @SerializedName("searchQueries")
    public String[] searchQueries;

    @SerializedName("searchResultOrder")
    public String searchResultOrder;

    @Override
    public String toString() {
        return "FilterBody{" +
                "brandIds=" + Arrays.toString(brandIds) +
                ", categoryIds=" + Arrays.toString(categoryIds) +
                ", filters=" + filters +
                ", searchQueries=" + Arrays.toString(searchQueries) +
                ", searchResultOrder='" + searchResultOrder + '\'' +
                '}';
    }
}