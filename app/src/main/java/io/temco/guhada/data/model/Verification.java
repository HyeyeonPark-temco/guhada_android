package io.temco.guhada.data.model;

public class Verification {
    // USING CHANGE PASSWORD
    private String name = "", phoneNumber = "", email = "", newPassword = "", diCode = "", mobile = "";

    // USING VERIFYING EMAIL
    private String verificationNumber = "";
    private String verificationTarget = ""; // EMAIL ADDRESS
    private String verificationTargetType = ""; // "EMAIL"


    public String getDiCode() {
        return diCode;
    }

    public void setDiCode(String diCode) {
        this.diCode = diCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVerificationNumber() {
        return verificationNumber;
    }

    public void setVerificationNumber(String verificationNumber) {
        this.verificationNumber = verificationNumber;
    }

    public String getVerificationTarget() {
        return verificationTarget;
    }

    public void setVerificationTarget(String verificationTarget) {
        this.verificationTarget = verificationTarget;
    }

    public String getVerificationTargetType() {
        return verificationTargetType;
    }

    public void setVerificationTargetType(String verificationTargetType) {
        this.verificationTargetType = verificationTargetType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
