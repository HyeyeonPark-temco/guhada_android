package io.temco.guhada.data.model.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseListModel<T> {

    @SerializedName("resultCode")
    public int resultCode;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public List<T> data;
}