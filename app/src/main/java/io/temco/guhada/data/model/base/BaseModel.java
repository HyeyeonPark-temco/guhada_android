package io.temco.guhada.data.model.base;

import com.google.gson.annotations.SerializedName;

public class BaseModel<T> {

    @SerializedName("resultCode")
    public int resultCode;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public T data;
}