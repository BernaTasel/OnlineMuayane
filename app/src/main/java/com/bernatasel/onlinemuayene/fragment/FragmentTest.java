package com.bernatasel.onlinemuayene.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.Constants;
import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.MyApp;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.doctor.FragmentDoctorSignUp;
import com.bernatasel.onlinemuayene.fragment.patient.FragmentPatientChooseDoctor;
import com.bernatasel.onlinemuayene.fragment.patient.FragmentPatientProfile;
import com.bernatasel.onlinemuayene.fragment.patient.FragmentPatientSignUp;
import com.bernatasel.onlinemuayene.fragment.videocall.VideoCallActivity;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSPatient;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FragmentTest extends FragmentBase {
    public static FragmentTest newInstance() {
        Bundle args = new Bundle();
        FragmentTest fragment = new FragmentTest();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG = "FragmentTest";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_test, container, false);

        String doctorUidDefault = "5f1b2n7CGZOj9HeLArmrYDu0sQC3";//FIXME:derya
        String patientUidDefault = "deroETckuTUBdqlMYo3aCEEVcln1";//FIXME:sevil

        Button btnInsert = view.findViewById(R.id.btn_insert);
        Button btnSelect = view.findViewById(R.id.btn_select);
        Button btnInsertDoctor = view.findViewById(R.id.btn_insert_doctor);
        Button btnChatDoctor = view.findViewById(R.id.btn_chat_doctor);
        Button btnChatPatient = view.findViewById(R.id.btn_chat_patient);
        Button btnVideoCallDoctor = view.findViewById(R.id.btn_video_call_doctor);
        Button btnVideoCallPatient = view.findViewById(R.id.btn_video_call_patient);
        Button btnGetChatPatients = view.findViewById(R.id.btn_get_chat_patients);

        Button btn5 = view.findViewById(R.id.btn5);
        Button btn10 = view.findViewById(R.id.btn10);
        Button btn14 = view.findViewById(R.id.btn14);
        Button btn15 = view.findViewById(R.id.btn15);
        Button btn101 = view.findViewById(R.id.btn101);

        btnVideoCallDoctor.setOnClickListener(v -> {
            if (!permissionCameraCheckElseAsk()) return;
            if (!permissionRecordAudioCheckElseAsk()) return;

            startActivity(VideoCallActivity.newIntent(getMA(), true, doctorUidDefault, patientUidDefault, null));
        });
        btnVideoCallPatient.setOnClickListener(v -> {
            if (!permissionCameraCheckElseAsk()) return;
            if (!permissionRecordAudioCheckElseAsk()) return;

            startActivity(VideoCallActivity.newIntent(getMA(), false, doctorUidDefault, patientUidDefault, "yC4BgVUJ9htfyAZgAAAI"));
        });
        btnChatDoctor.setOnClickListener(v -> getMA().addFragmentSafe(FragmentChat.newInstance(true, doctorUidDefault, patientUidDefault, null), MyFM.ANIM.RIGHT));
        btnChatPatient.setOnClickListener(v -> getMA().addFragmentSafe(FragmentChat.newInstance(false, doctorUidDefault, patientUidDefault, null), MyFM.ANIM.RIGHT));
        btnInsert.setOnClickListener(v -> showToast("sorgu atıldı..."));
        btnSelect.setOnClickListener(v -> FSOps.getInstance().getCRUser().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("success");

                for (QueryDocumentSnapshot qds : task.getResult()) {
                    FSDoctor doctor = qds.toObject(FSDoctor.class);
                    System.out.println(doctor);
                }
            } else {
                showToast("fail");
            }
        }));
        btnInsertDoctor.setOnClickListener(v -> {
            String email = "berna@gmail.com";
            String pass = "123456";

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    showToast("Fail 1");
                    return;
                }
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(task2 -> {
                    if (!task2.isSuccessful()) {
                        showToast("Fail 2");
                        return;
                    }
                    AuthResult authResult = task2.getResult();
                    String uid = authResult.getUser().getUid();
                    String name = "Berna";
                    String surname = "Surname";

                    FSDoctor fsDoctor = new FSDoctor(uid, Constants.USER_TYPE.DOCTOR.getName(), name, surname, email,
                            null, null, null, null, 0, "default", null, true);
                    FSOps.getInstance().getCRUser().document(email).set(fsDoctor);

                    showToast("SignedIn");
                });
            });
        });
        btnGetChatPatients.setOnClickListener(v -> {
            if (!MyApp.getInstance().isLoggedIn()) {
                showToast("No login");
                return;
            }
            FSUser loggedInPerson = MyApp.getInstance().getLoggedInPerson();
            if (!loggedInPerson.isDoctor()) {
                showToast("Not doctor");
                return;
            }
            String doctorUid = loggedInPerson.getUid();
            FSOps.getInstance().getCRChatDoctorPatient(doctorUid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<String> chatTitles = getApp().mySP.getChatTitles();

                    for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                        String patientUid = ds.getId();

                        if (!chatTitles.contains(doctorUid + "_" + patientUid)) {
                            getApp().mySP.putMyChatMessages(doctorUid, patientUid, new ArrayList<>());

                            FSOps.getInstance().getUserByUID(patientUid).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot dsFSUser : queryDocumentSnapshots.getDocuments()) {
                                        FSUser fsUser = dsFSUser.toObject(FSPatient.class);
                                        getApp().mySP.putNameSurname(patientUid, fsUser.getName(), fsUser.getSurname());
                                    }
                                }
                            });
                        }
                    }
                }
            });
        });

        btn5.setOnClickListener(v -> openFragment(FragmentDoctorSignUp.newInstance("ilayda@gmail.com")));
        btn10.setOnClickListener(v -> openFragment(FragmentPatientChooseDoctor.newInstance(null)));
        btn14.setOnClickListener(v -> openFragment(FragmentPatientProfile.newInstance()));
        btn15.setOnClickListener(v -> openFragment(FragmentPatientSignUp.newInstance("test@test.com")));


//                ActionCodeSettings actionCodeSettings =
//                        ActionCodeSettings.newBuilder()
//                                // URL you want to redirect back to. The domain (www.example.com) for this
//                                // URL must be whitelisted in the Firebase Console.
//                                .setUrl("https://www.example.com/finishSignUp?cartId=1234")
//                                // This must be true
//                                .setHandleCodeInApp(true)
//                                .setIOSBundleId("com.example.ios")
//                                .setAndroidPackageName(
//                                        "com.example.android",
//                                        true, /* installIfNotAvailable */
//                                        "12"    /* minimumVersion */)
//                                .build();


//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                auth.sendSignInLinkToEmail(email, actionCodeSettings)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG, "Email sent.");
//                                }
//                            }
//                        });


//                HashMap<String, Object> hm = new HashMap<>();
//                hm.put("type", "doctor");
//                hm.put("name", "Name");
//                hm.put("surname", "Surname");
//                hm.put("email", "email@email.com");
//                hm.put("password", "123456789");
//                hm.put("phone", "+90 000");
//                hm.put("gender", Constants.GENDER.FEMALE.getName());
//                hm.put("birthdate", Timestamp.now());
//                hm.put("profession", "123456789");
//
//                FSOps.getInstance().getCRUser().document("email@email.com").set(hm);

//                FSOps.getInstance().getDoctorByEmail("ahmet@gmail.com").addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot qds : task.getResult()) {
//                                String email = qds.getString("email");
//                                String type = qds.getString("type");
//                                String name = qds.getString("name");
//                                //TODO: ...
//
//                                showToast(name);
//
////                                Doctor doctor = qds.toObject(Doctor.class);
////                                showToast(doctor.toString());
//                            }
//                        } else {
//                            showToast("Doktor çekilirken Hata Oluştu");
//                        }
//                    }
//                });

//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Query capitalCities = db.collection("User").whereEqualTo("type", "admin");
//
//                capitalCities.get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    int size = task.getResult().size();
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
//                                    }
//                                } else {
//                                    Log.d(TAG, "Error getting documents: ", task.getException());
//                                }
//                            }
//                        });
//
//                db.collection("User ")
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                //Log.d(TAG, "onComplete: ");
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
//
//                                        String id = document.getString("id");
//                                        String name = document.getString("name");
//                                        String password = document.getString("password");
//                                        String type = document.getString("type");
//                                        Log.d(TAG, "Id: " + id);
//                                        Log.d(TAG, "Name: " + name);
//                                        Log.d(TAG, "Password: " + password);
//                                        Log.d(TAG, "Type: " + type);
//
//                                    }
//                                } else {
//                                    Log.w(TAG, "Error getting documents.", task.getException());
//                                }
//                            }
//                        });
//            }
//        });

        return view;
    }

    private void open(Class clazz) {
        Intent i = new Intent(getMA(), clazz);
        startActivity(i);
    }

    private void openFragment(FragmentBase fb) {
        getMA().addFragmentSafe(fb, MyFM.ANIM.RIGHT);
    }
}
