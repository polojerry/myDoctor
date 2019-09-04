package com.polotechnologies.mydoctor.dataClass;

public class DoctorProfile {

    private String uId;
    private String imageUrl;
    private String fullName;
    private String mobileNumber;
    private String designation;
    private String speciality;


    public DoctorProfile() {
    }

    public DoctorProfile(String uId, String imageUrl, String fullName,
                         String mobileNumber, String designation, String speciality) {
        this.uId = uId;
        this.imageUrl = imageUrl;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.designation = designation;
        this.speciality = speciality;
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

    public String getDesignation() {
        return designation;
    }

    public String getSpeciality() {
        return speciality;
    }
}
