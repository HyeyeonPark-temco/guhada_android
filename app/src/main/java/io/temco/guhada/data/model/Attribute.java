package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import io.temco.guhada.common.Type;

public class Attribute {

    public Type.List type = Type.List.CONTENTS;

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("selected")
    public boolean selected;

    @SerializedName("colorName")
    public String colorName;

    @Override
    public String toString() {
        return "Attribute{" +
                "type=" + type +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", selected=" + selected +
                ", colorName='" + colorName + '\'' +
                '}';
    }
}