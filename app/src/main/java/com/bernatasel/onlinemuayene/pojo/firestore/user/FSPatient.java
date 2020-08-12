package com.bernatasel.onlinemuayene.pojo.firestore.user;


import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;


public class FSPatient extends FSUser {
    private String phone;
    private String gender;
    private Timestamp birthdate;
    private String city;
    private int bmi;
    private boolean isSmoke;
    private boolean isAlcohol;
    private boolean isAllergy;
    private boolean isChoronic;
    private String allergyInfo;
    private String choronicInfo;

    public FSPatient() {
    }

    public FSPatient(String uid, String type, String name, String surname, String email,
                     String phone, String gender, Timestamp birthdate, String city, int bmi,
                     boolean isSmoke, boolean isAlcohol, boolean isAllergy, boolean isChoronic,
                     String allergyInfo, String choronicInfo,
                     int availability, String profilePhoto, boolean activeAccount) {
        super(uid, type, name, surname, email, availability, profilePhoto, activeAccount);
        this.phone = phone;
        this.gender = gender;
        this.birthdate = birthdate;
        this.city = city;
        this.bmi = bmi;
        this.isSmoke = isSmoke;
        this.isAlcohol = isAlcohol;
        this.isAllergy = isAllergy;
        this.isChoronic = isChoronic;
        this.allergyInfo = allergyInfo;
        this.choronicInfo = choronicInfo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Timestamp birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getBmi() {
        return bmi;
    }

    public void setBmi(int bmi) {
        this.bmi = bmi;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isSmoke() {
        return isSmoke;
    }

    public void setSmoke(boolean smoke) {
        isSmoke = smoke;
    }

    public boolean isAlcohol() {
        return isAlcohol;
    }

    public void setAlcohol(boolean alcohol) {
        isAlcohol = alcohol;
    }

    public boolean isAllergy() {
        return isAllergy;
    }

    public void setAllergy(boolean allergy) {
        isAllergy = allergy;
    }

    public boolean isChoronic() {
        return isChoronic;
    }

    public void setChoronic(boolean choronic) {
        isChoronic = choronic;
    }

    public String getAllergyInfo() {
        return allergyInfo;
    }

    public void setAllergyInfo(String allergyInfo) {
        this.allergyInfo = allergyInfo;
    }

    public String getChoronicInfo() {
        return choronicInfo;
    }

    public void setChoronicInfo(String choronicInfo) {
        this.choronicInfo = choronicInfo;
    }

    public int getAge() {
        if(birthdate == null){
            return 0;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(birthdate.toDate());
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        return  now.get(Calendar.YEAR) - date.get(Calendar.YEAR);
    }

    @Override
    public String toString() {
        return "FSPatient{" +
                "birthday=" + birthdate +
                ", gender='" + gender + '\'' +
                ", isSmoke=" + isSmoke +
                ", isAlcohol=" + isAlcohol +
                ", isAllergy=" + isAllergy +
                ", isChoronic=" + isChoronic +
                ", allergyInfo='" + allergyInfo + '\'' +
                ", choronicInfo='" + choronicInfo + '\'' +
                '}';
    }

    public String getInfo(){
        String smoke;
        String alcohol;
        if(isSmoke()){
            smoke = "Sigara kullanıyor";
        }
        else{
            smoke = "Sigara kullanmıyor";
        }
        if(isAlcohol()){
            alcohol = "Alkol kullanıyor";
        }
        else {
            alcohol = "Alkol kullanmıyor";
        }
        return "Adı: " + getName()
                + "\nSoyadı: " + getSurname()
                + "\nCinsiyeti: " + getGender()
                + "\nYaşı: " + getAge()
                + "\n" + smoke
                + "\n" +alcohol
                + "\nAlerjisi: " + getAllergyInfo()
                + "\nKronik Hastalıklar: " + getChoronicInfo()
                + "\nBMI: " + getBmi();
    }
}
