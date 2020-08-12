package com.bernatasel.onlinemuayene.pojo.firestore.user;

import com.google.firebase.Timestamp;

public class FSDoctor extends FSUser {
    private String phone;
    private String gender;
    private Timestamp birthdate;
    private String profession;
    private String city;

    public FSDoctor() {
    }

    public FSDoctor(String uid, String type, String name, String surname, String email,
                    String phone, String gender, Timestamp birthdate, String profession,int availability, String profilePhoto, String city, boolean activeAccount) {
        super(uid, type, name, surname, email, availability, profilePhoto, activeAccount);
        this.phone = phone;
        this.gender = gender;
        this.birthdate = birthdate;
        this.profession = profession;
        this.city = city;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Timestamp getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Timestamp birthdate) {
        this.birthdate = birthdate;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "FSDoctor{" +
                "phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", birthdate=" + birthdate +
                ", profession='" + profession + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
