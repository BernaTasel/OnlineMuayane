package com.bernatasel.onlinemuayene.fragment.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.Constants;
import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.fragment.doctor.FragmentDoctorSignUp;
import com.bernatasel.onlinemuayene.fragment.patient.FragmentPatientSignUp;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSAdmin;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSPatient;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FragmentLogin extends FragmentBase {
    public static FragmentLogin newInstance() {
        Bundle args = new Bundle();
        FragmentLogin fragment = new FragmentLogin();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText etEmail, etPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        TextView tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        LinearLayout llTest = view.findViewById(R.id.ll_test);
        Button btnAdmin = view.findViewById(R.id.btn_admin);
        Button btnDoctor = view.findViewById(R.id.btn_doctor);
        Button btnPatient = view.findViewById(R.id.btn_patient);
        TextView tvSignUp = view.findViewById(R.id.tvSignUp);

        llTest.setVisibility(Constants.IS_DEBUG ? View.VISIBLE : View.GONE);

        tvForgotPassword.setOnClickListener(v -> getMA().addFragmentSafe(FragmentForgotPassword.newInstance(), MyFM.ANIM.RIGHT));
        btnLogin.setOnClickListener(v -> {
            etClearError(etEmail, etPassword);

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || !UtilsAndroid.isEmail(email)) {
                etError(etEmail, "Lütfen geçerli bir mail adresi giriniz!");
                return;
            }
            if (password.isEmpty()) {
                etError(etPassword, "Şifre boş bırakılamaz!");
                return;
            }

            getMA().showHideLoading(true);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    showToast("Yanlış mail ya da şifre!");
                    getMA().showHideLoading(false);
                    return;
                }

                AuthResult authResult = task.getResult();
                String uid = authResult.getUser().getUid();

                FSOps.getInstance().getUserByUID(uid).addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        showToast("Kullanıcı DB de bulunamadı");
                        getMA().showHideLoading(false);
                        return;
                    }

                    etClear(etPassword);

                    getMA().showHideLoading(false);
                    for (QueryDocumentSnapshot qds : task1.getResult()) {
                        FSUser fsUser = qds.toObject(FSUser.class);
                        if(fsUser.isActiveAccount()){
                            if (fsUser.isAdmin()) {
                                FSAdmin fsAdmin = qds.toObject(FSAdmin.class);
                                getApp().setUser(fsAdmin);
                                getMA().updateDashBoard();
                            } else if (fsUser.isDoctor()) {
                                FSDoctor fsDoctor = qds.toObject(FSDoctor.class);

                                if (fsDoctor.getGender() == null) {
                                    getMA().addFragmentSafe(FragmentDoctorSignUp.newInstance(email), MyFM.ANIM.RIGHT);
                                } else {
                                    getApp().setUser(fsDoctor);
                                    getMA().updateDashBoard();
                                }
                            } else if (fsUser.isPatient()) {
                                FSPatient fsPatient = qds.toObject(FSPatient.class);
                                if(fsPatient.getBirthdate() == null){
                                    getMA().addFragmentSafe(FragmentPatientSignUp.newInstance(email), MyFM.ANIM.RIGHT);
                                }
                                else{
                                    getApp().setUser(fsPatient);
                                    getMA().updateDashBoard();
                                }
                            }
                        }
                        else {
                            UtilsAndroid.showAlertDialog(getMA(), "Hesabınıza erişim sorunu", "Hesabınız sistem yöneticisi tarafından engellenmiş durumdadır. Öneri/Şikayet bölümünden sistem yöneticisine ulaşabilirsiniz. ",false, R.string.tamam,null,null,null,null,null );
                        }
                        break;
                    }
                });
            });
        });
        btnAdmin.setOnClickListener(v -> {
            etEmail.setText("bernataselbal@gmail.com");
            etPassword.setText("123456");
        });
        btnDoctor.setOnClickListener(v -> {
            etEmail.setText("derya@gmail.com");
            etPassword.setText("123456");
        });
        btnPatient.setOnClickListener(v -> {
            etEmail.setText("sevil@gmail.com");
            etPassword.setText("123456");
        });
        tvSignUp.setOnClickListener(v -> getMA().addFragmentSafe(FragmentSignUp.newInstance(), MyFM.ANIM.RIGHT));

        return view;
    }

    @Override
    public boolean onBack() {
        if (getMA().isLoading()) return false;
        return super.onBack();
    }
}
