package io.temco.guhada.data.model.user;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Observable;

import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.community.UserDetail;

/**
 * 유저 정보 클래스
 *
 * @author Hyeyeon Park
 */
public class User extends Observable implements Serializable {
    // ESSENTIAL TERMS
    private Boolean agreeCollectPersonalInfoTos = false;
    private Boolean agreePurchaseTos = false;

    // OPTIONAL TERMS
    private Boolean agreeSaleTos = false;
    private Boolean agreeSmsReception = false;
    private Boolean agreeEmailReception = false;

    // USER INFO
    @Nullable
    private String name = "";

    @Nullable
    private String familyName = "";

    @Nullable
    private String givenName = "";

    @Nullable
    private String email = "";
    @Nullable
    private String password = "";
    @Nullable
    private String mobile = "";
    @Nullable
    private String phoneNumber = "";
    @Nullable
    private String joinAt = "", withdrawalAt = "";
    @Nullable
    private String birth = "";

    @Nullable
    private String address = "", roadAddress = "", detailAddress = "", zip;
    private Boolean emailVerify = false;

    @Nullable
    private String profileImageUrl = "";

    @Nullable
    private String nickname = "";

    // 임시 userDetail 정보
    private UserDetail userDetail = new UserDetail();


    @SerializedName("temp") // 본인 인증을 위해 사용하는 gender
    private int gender = -1; // 1: MALE; 2: FEMALE;

    @SerializedName("gender") // 서버로 부터 내려받은 유저의 gender
    private String userGender = ""; // 1: MALE; 2: FEMALE;

    private int mobileCarriers = -1; // 1: SKT; 2: KT; 3: LG
    private int nationality = -1; // 1: INTERNAL; 2: FOREIGNER

    @Expose
    private String confirmPassword = "";

    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ESSENTIAL TERMS
    public Boolean getAgreeCollectPersonalInfoTos() {
        return agreeCollectPersonalInfoTos;
    }

    public void setAgreeCollectPersonalInfoTos(Boolean agreeCollectPersonalInfoTos) {
        this.agreeCollectPersonalInfoTos = agreeCollectPersonalInfoTos;
        userDetail.setAgreeCollectPersonalInfoTos(agreeCollectPersonalInfoTos);
    }

    public Boolean getAgreePurchaseTos() {
        return agreePurchaseTos;
    }

    public void setAgreePurchaseTos(Boolean agreePurchaseTos) {
        this.agreePurchaseTos = agreePurchaseTos;
        userDetail.setAgreePurchaseTos(agreePurchaseTos);
    }

    // OPTIONAL TERMS
    public Boolean getAgreeEmailReception() {
        return agreeEmailReception;
    }

    public void setAgreeEmailReception(Boolean agreeEmailReception) {
        this.agreeEmailReception = agreeEmailReception;
        userDetail.setAgreeEmailReception(agreeEmailReception);
    }

    public Boolean getAgreeSaleTos() {
        return agreeSaleTos;
    }

    public void setAgreeSaleTos(Boolean agreeSaleTos) {
        this.agreeSaleTos = agreeSaleTos;
        userDetail.setAgreeSaleTos(agreeSaleTos);
    }

    public Boolean getAgreeSmsReception() {
        return agreeSmsReception;
    }

    public void setAgreeSmsReception(Boolean agreeSmsReception) {
        this.agreeSmsReception = agreeSmsReception;
        userDetail.setAgreeSmsReception(agreeSmsReception);
    }

    // USER INFO
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setChanged();
        notifyObservers("email");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;

    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setChanged();
        notifyObservers("phoneNumber");
    }

    public String getJoinAt() {
        return joinAt + "가입";
    }

    public void setJoinAt(String joinAt) {
        this.joinAt = joinAt;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public int getMobileCarriers() {
        return mobileCarriers;
    }

    public void setMobileCarriers(int mobileCarriers) {
        this.mobileCarriers = mobileCarriers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setChanged();
        notifyObservers("name");
    }

    public int getNationality() {
        return nationality;
    }

    public void setNationality(int nationality) {
        this.nationality = nationality;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        setChanged();
        notifyObservers("mobile");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getWithdrawalAt() {
        return withdrawalAt;
    }

    public void setWithdrawalAt(String withdrawalAt) {
        this.withdrawalAt = withdrawalAt;
    }

    public Boolean getEmailVerify() {
        return emailVerify;
    }

    public void setEmailVerify(Boolean emailVerify) {
        this.emailVerify = emailVerify;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    @Nullable
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(@Nullable String familyName) {
        this.familyName = familyName;
    }

    @Nullable
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(@Nullable String givenName) {
        this.givenName = givenName;
    }

    @Override
    public String toString() {
        if(CustomLog.getFlag())return "User{" +
                "agreeCollectPersonalInfoTos=" + agreeCollectPersonalInfoTos +
                ", agreePurchaseTos=" + agreePurchaseTos +
                ", agreeSaleTos=" + agreeSaleTos +
                ", agreeSmsReception=" + agreeSmsReception +
                ", agreeEmailReception=" + agreeEmailReception +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", mobile='" + mobile + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", joinAt='" + joinAt + '\'' +
                ", withdrawalAt='" + withdrawalAt + '\'' +
                ", birth='" + birth + '\'' +
                ", address='" + address + '\'' +
                ", roadAddress='" + roadAddress + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", zip='" + zip + '\'' +
                ", emailVerify=" + emailVerify +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userDetail=" + userDetail +
                ", gender=" + gender +
                ", userGender='" + userGender + '\'' +
                ", mobileCarriers=" + mobileCarriers +
                ", nationality=" + nationality +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
        else return "";
    }
}
