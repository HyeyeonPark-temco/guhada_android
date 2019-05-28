package io.temco.guhada.data.model.body;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilterBody {

    @SerializedName("brandIds")
    public int[] brandIds;

    @SerializedName("categoryIds")
    public int[] categoryIds;

    @SerializedName("filters")
    public List<Filter> filters;

    @SerializedName("searchQueries")
    public String[] searchQueries;

    @SerializedName("searchResultOrder")
    public String searchResultOrder;

    public class Filter {

        @SerializedName("filterAttributeId")
        public int filterAttributeId;

        @SerializedName("filterAttributeName")
        public String filterAttributeName;

        @SerializedName("filterId")
        public int filterId;

        @SerializedName("filterName")
        public String filterName;
    }
}