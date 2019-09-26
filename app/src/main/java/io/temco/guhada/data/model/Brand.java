package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.temco.guhada.data.model.base.BaseStickyHeaderModel;

public class Brand extends BaseStickyHeaderModel implements Serializable {

    public boolean isSelected = false;

    public boolean isAlphaClicked = false;

    @SerializedName("id")
    public int id;

    @SerializedName("nameDefault")
    public String nameDefault;

    @SerializedName("nameEn")
    public String nameEn;

    @SerializedName("nameKo")
    public String nameKo;

    @SerializedName("isFavorite")
    public boolean isFavorite;

    @Override
    public String toString() {
        return "Brand{" +
                "isSelected=" + isSelected +
                ", id=" + id +
                ", nameDefault='" + nameDefault + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", nameKo='" + nameKo + '\'' +
                ", isFavorite=" + isFavorite +
                ", type=" + type +
                ", position=" + position +
                ", layoutRes=" + layoutRes +
                '}';
    }
}