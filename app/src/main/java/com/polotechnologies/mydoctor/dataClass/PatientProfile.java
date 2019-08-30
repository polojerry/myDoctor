package com.polotechnologies.mydoctor.dataClass;

public class PatientProfile {

    private String uId;
    private String fullName;
    private String mobileNumber;

    public PatientProfile() {
    }

    public PatientProfile(String uId, String fullName, String mobileNumber) {
        this.uId = uId;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
    }

    public String getuId() {
        return uId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
