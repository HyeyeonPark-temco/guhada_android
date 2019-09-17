package io.temco.guhada.data.model.body;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class FilterBody {

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