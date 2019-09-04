package com.polotechnologies.mydoctor.dataClass;

public class PatientProfile {

    private String uId;
    private String imageUrl;
    private String fullName;
    private String mobileNumber;

    public PatientProfile() {
    }

    public PatientProfile(String uId, String imageUrl, String fullName, String mobileNumber) {
        this.uId = uId;
        this.imageUrl = imageUrl;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
    }

    public String getuId() {
        return uId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
