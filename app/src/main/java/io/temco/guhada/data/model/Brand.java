package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.temco.guhada.data.model.base.BaseStickyHeaderModel;

public class Brand extends BaseStickyHeaderModel implements Serializable {

    public boolean isSelected = false;

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
}