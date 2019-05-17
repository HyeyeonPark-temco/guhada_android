package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

public class NaverResponse {
    private String message;

    @SerializedName("resultcode")
    private String resultCode;

    @SerializedName("response")
    private NaverUser user;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NaverUser getUser() {
        return user;
    }

    public void setUser(NaverUser user) {
        this.user = user;
    }
}
