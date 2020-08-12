package com.bernatasel.onlinemuayene.pojo.firestore.user;

import com.bernatasel.onlinemuayene.Constants;

public class FSUser {

    private String uid;
    private String type;

    private String name;
    private String surname;
    private String email;
    private int availability;
    private String profilePhoto;
    private String fcmToken;
    private boolean activeAccount;
    public FSUser() {
    }

    public FSUser(String uid, String type, String name, String surname, String email, int availability, String profilePhoto, boolean activeAccount) {
        this.uid = uid;
        this.type = type;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.availability = availability;
        this.profilePhoto = profilePhoto;
        this.activeAccount = activeAccount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean isAdmin() {
        return getType().equals(Constants.USER_TYPE.ADMIN.getName());
    }

    public boolean isDoctor() {
        return getType().equals(Constants.USER_TYPE.DOCTOR.getName());
    }

    public boolean isPatient() {
        return getType().equals(Constants.USER_TYPE.PATIENT.getName());
    }

    public boolean isActiveAccount() {
        return activeAccount;
    }

    public void setActiveAccount(boolean activeAccount) {
        this.activeAccount = activeAccount;
    }

    @Override
    public String toString() {
        return "FSUser{" +
                "uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", availability=" + availability +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                '}';
    }
}
