package com.bernatasel.onlinemuayene;

import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FSOps {
    private static FSOps instance;

    public static FSOps getInstance() {
        if (instance == null) instance = new FSOps();
        return instance;
    }

    private FirebaseFirestore ff = FirebaseFirestore.getInstance();

    public CollectionReference getCRUser() {
        return ff.collection("User");
    }

    public DocumentReference getDRUser(String email) {
        return getCRUser().document(email);
    }

    public CollectionReference getCRChat() {
        return ff.collection("Chat");
    }

    public CollectionReference getCRChatDoctorPatient(String doctorUid) {
        return ff.collection("Chat").document(doctorUid).collection("Patient");
    }

    public CollectionReference getCRSuggestion() {
        return ff.collection("Suggestion");
    }

    public DocumentReference getDRChatDoctorPatient(String doctorUid, String patientUid) {
        return getCRChat().document(doctorUid).collection("Patient").document(patientUid);
    }

    public CollectionReference getCRChatDoctorPatientData(String doctorUid, String patientUid) {
        return getDRChatDoctorPatient(doctorUid, patientUid).collection("Data");
    }

    public void updateUser(String email, HashMap<String, Object> hm) {
        updateMerge(getDRUser(email), hm);
    }

    public void updateSuggestionSolve(String id, boolean solved) {
        getCRSuggestion().document(id).update("solved", solved);
    }

    public Task<QuerySnapshot> getUserByEmail(String email) {
        return getCRUser()
                .whereEqualTo("email", email)
                .get();
    }

    public Task<QuerySnapshot> getUserByUID(String uid) {
        return getCRUser()
                .whereEqualTo("uid", uid)
                .get();
    }


    public Task<QuerySnapshot> getFSDoctor(String doctorUid) {
        return getCRUser()
                .whereEqualTo("type", "doctor")
                .whereEqualTo("uid", doctorUid)
                .get();
    }

    public Task<QuerySnapshot> getFSPatient(String patientUid) {
        return getCRUser()
                .whereEqualTo("type", "patient")
                .whereEqualTo("uid", patientUid)
                .get();
    }

    public Task<QuerySnapshot> getAdmins() {
        return getCRUser()
                .whereEqualTo("type", "admin")
                .get();
    }

    public Task<QuerySnapshot> getDoctors() {
        return getCRUser()
                .whereEqualTo("type", "doctor")
                .get();
    }

    public Task<QuerySnapshot> getPatients() {
        return getCRUser()
                .whereEqualTo("type", "patient")
                .get();
    }

    public void updateMerge(DocumentReference dr, HashMap<String, Object> hm) {
        dr.set(hm, SetOptions.merge());
    }

    private void updateMergeCurrentUser(Map<String, Object> map) {
        FSUser loggedInPerson = MyApp.getInstance().getLoggedInPerson();
        if (loggedInPerson == null) return;
        getDRUser(loggedInPerson.getEmail()).set(map, SetOptions.merge());
    }

    public void updateCurrentUserFCMToken(String fcmToken) {
        if (fcmToken == null || fcmToken.isEmpty()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("fcmToken", fcmToken);

        updateMergeCurrentUser(map);
    }
}
