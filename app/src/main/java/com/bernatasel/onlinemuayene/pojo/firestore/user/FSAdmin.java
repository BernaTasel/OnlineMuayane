package com.bernatasel.onlinemuayene.pojo.firestore.user;

public class FSAdmin extends FSUser {
    public FSAdmin() {
    }

    public FSAdmin(String uid, String type, String name, String surname, String email, int availability, String profilePhoto, boolean activeAccount) {
        super(uid, type, name, surname, email, availability, profilePhoto, activeAccount);
    }
}
