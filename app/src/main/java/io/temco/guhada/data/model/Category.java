package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.temco.guhada.common.Type;

public class Category {

    public Type.Category type = Type.Category.NORMAL;

    public boolean isSelected = false;

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("title")
    public String title;

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
    public List<Category> children;
}
