package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import io.temco.guhada.common.Type;

public class Category {

    public Type.Category type = Type.Category.NORMAL;

    @SerializedName("isSelected")
    public boolean isSelected = false;

    @SerializedName("isExpand")
    public boolean isExpand = false;

    @SerializedName("depth")
    public int depth = -1;

    @SerializedName("id")
    public int id;

//    @SerializedName("name")
//    public String name;

    @SerializedName("title")
    public String title;

    @SerializedName("label")
    public String label;

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

    public int selectId = -1;

    public int parentId = -1;

    @Override
    public String toString() {
        return "Category{" +
                "type=" + type +
                ", isSelected=" + isSelected +
                ", isExpand=" + isExpand +
                ", depth=" + depth +
                ", id=" + id +
                ", parentId=" + parentId +
                ", title='" + title + '\'' +
                ", key='" + key + '\'' +
                ", hierarchy='" + hierarchy + '\'' +
                ", fullDepthName='" + fullDepthName + '\'' +
                ", immediateChildrenCount=" + immediateChildrenCount +
                ", hierarchies=" + Arrays.toString(hierarchies) +
                ", isUnisex=" + isUnisex +
                '}';
    }

}
