package io.temco.guhada.data.model;

import com.google.gson.annotations.Expose;

import java.util.Observable;

public class User extends Observable {
    // ESSENTIAL TERMS
    private Boolean agreeCollectPersonalInfoTos = false;
    private Boolean agreePurchaseTos = false;

    // OPTIONAL TERMS
    private Boolean agreeSaleTos = false;
    private Boolean agreeSmsReception = false;
    private Boolean agreeEmailReception = false;

    // USER INFO
    private String name = "";
    private String email = "";
    private String password = "";
    private String phoneNumber = "";
    private String joinAt = "";
    private int gender = -1; // 1: MALE; 2: FEMALE;
    private String birth ="";
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
        setChanged();
        notifyObservers("agreeCollectPersonalInfoTos");
    }

    public Boolean getAgreePurchaseTos() {
        return agreePurchaseTos;
    }

    public void setAgreePurchaseTos(Boolean agreePurchaseTos) {
        this.agreePurchaseTos = agreePurchaseTos;
        setChanged();
        notifyObservers("agreePurchaseTos");
    }

    // OPTIONAL TERMS
    public Boolean getAgreeEmailReception() {
        return agreeEmailReception;
    }

    public void setAgreeEmailReception(Boolean agreeEmailReception) {
        this.agreeEmailReception = agreeEmailReception;
        setChanged();
        notifyObservers("agreeEmailReception");
    }

    public Boolean getAgreeSaleTos() {
        return agreeSaleTos;
    }

    public void setAgreeSaleTos(Boolean agreeSaleTos) {
        this.agreeSaleTos = agreeSaleTos;
        setChanged();
        notifyObservers("agreeSaleTos");
    }

    public Boolean getAgreeSmsReception() {
        return agreeSmsReception;
    }

    public void setAgreeSmsReception(Boolean agreeSmsReception) {
        this.agreeSmsReception = agreeSmsReception;
        setChanged();
        notifyObservers("agreeSmsReception");
    }

    // USER INFO
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

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
    }

    public String getJoinAt() {
        return "("+joinAt+" 가입)";
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
    }

    public int getNationality() {
        return nationality;
    }

    public void setNationality(int nationality) {
        this.nationality = nationality;
    }
}
