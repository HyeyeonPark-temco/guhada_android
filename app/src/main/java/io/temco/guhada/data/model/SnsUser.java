package io.temco.guhada.data.model;

import com.google.gson.annotations.SerializedName;

public class SnsUser {
    @SerializedName("profileJson")
    private UserProfile userProfile;

    private String email;
    private String snsId;
    private String type;

    public SnsUser() {
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSnsId() {
        return snsId;
    }

    public void setSnsId(String snsId) {
        this.snsId = snsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
