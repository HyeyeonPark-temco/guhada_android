package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.temco.guhada.common.Type;

public class CategoryData {

    public Type.Category type = Type.Category.NORMAL;

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("key")
    public String key;

    @SerializedName("hierarchy")
    public String hierarchy;

    @SerializedName("fullDepthName")
    public String fullDepthName;

    @SerializedName("immediateChildrenCount")
    public int immediateChildrenCount;

    @SerializedName("hierarchies")
    public int[] hierarchies;

    @SerializedName("isUnisex")
    public boolean isUnisex;

    @SerializedName("children")
    public List<CategoryData> children;
}
