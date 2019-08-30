package com.polotechnologies.mydoctor.dataClass;

public class DoctorCategories {
    int imageId;
    String category;

    public DoctorCategories(int imageId, String category) {
        this.imageId = imageId;
        this.category = category;
    }

    public int getImageId() {
        return imageId;
    }

    public String getCategory() {
        return category;
    }
}
