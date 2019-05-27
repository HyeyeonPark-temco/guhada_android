package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Filter {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("viewType")
    public String viewType;

    @SerializedName("attributes")
    public List<Attribute> attributes;

    public class Attribute {

        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;

        @SerializedName("selected")
        public boolean selected;
    }
}
