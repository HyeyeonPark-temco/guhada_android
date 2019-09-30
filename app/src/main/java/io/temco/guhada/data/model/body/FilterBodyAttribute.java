package io.temco.guhada.data.model.body;

import com.google.gson.annotations.SerializedName;

public class FilterBodyAttribute {

    @SerializedName("filterAttributeId")
    public int filterAttributeId;

    @SerializedName("filterAttributeName")
    public String filterAttributeName;

    @SerializedName("filterId")
    public int filterId;

    @SerializedName("filterName")
    public String filterName;

}
