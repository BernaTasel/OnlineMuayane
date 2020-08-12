package com.bernatasel.onlinemuayene.fragment.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.Constants;
import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSAdmin;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentAdminAddUser extends FragmentBase {
    public static FragmentAdminAddUser newInstance() {
        Bundle args = new Bundle();
        FragmentAdminAddUser fragment = new FragmentAdminAddUser();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText etUserEmail, etUserName, etUserSurname;
    private Spinner spnUserType;
    private Button btnSaveUser;
    private String[] userTypes = {"Seçiniz...", "Doktor", "Admin"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_add_user, container, false);

        etUserEmail = view.findViewById(R.id.etUserEmail);
        etUserName = view.findViewById(R.id.etUserName);
        etUserSurname = view.findViewById(R.id.etUserSurname);
        spnUserType = view.findViewById(R.id.spnUserType);
        btnSaveUser = view.findViewById(R.id.btnSaveUser);

        ArrayAdapter<String> adapterUserTypes = new ArrayAdapter<String>(getMA(), R.layout.spinner_style, userTypes);
        adapterUserTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUserType.setAdapter(adapterUserTypes);


        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMA().showHideLoading(true);
                if (checkUser()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                    builder.setTitle(spnUserType.getSelectedItem().toString() + " Bilgileri: ");
                    builder.setMessage("Email: " + etUserEmail.getText().toString() +
                            "\nİsim Soyisim: " + etUserName.getText().toString() + " " + etUserSurname.getText().toString());
                    builder.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String email = etUserEmail.getText().toString();
                            String password = "123456";
                            String name = etUserName.getText().toString();
                            String surname = etUserSurname.getText().toString();
                            if (spnUserType.getSelectedItemPosition() == 1) {
                                //VERİTABANINA DOKTOR KAYIT
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        showToast("Bu email sistemde zaten kayıtlı!");
                                        return;
                                    }
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task2 -> {
                                        if (!task2.isSuccessful()) {
                                            showToast("Kayıt başarısız(2)");
                                            return;
                                        }
                                        AuthResult authResult = task2.getResult();
                                        String uid = authResult.getUser().getUid();
                                        FSDoctor fsDoctor = new FSDoctor(uid, Constants.USER_TYPE.DOCTOR.getName(), name, surname, email, null, null, null, null, 0, "default",null, true);
                                        FSOps.getInstance().getCRUser().document(email).set(fsDoctor);
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                                        showToast("Kayıt başarılı");
                                    });
                                });
                            }
                            //VERİTABANINA ADMIN KAYIT
                            if (spnUserType.getSelectedItemPosition() == 2) {
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        showToast("Bu email sistemde zaten kayıtlı!");
                                        return;
                                    }
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task2 -> {
                                        if (!task2.isSuccessful()) {
                                            showToast("Kayıt başarısız(2)");
                                            return;
                                        }
                                        AuthResult authResult = task2.getResult();
                                        String uid = authResult.getUser().getUid();
                                        FSAdmin fsAdmin = new FSAdmin(uid, Constants.USER_TYPE.ADMIN.getName(), name, surname, email, 0, "default", true);
                                        FSOps.getInstance().getCRUser().document(email).set(fsAdmin);

                                        FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                                        showToast("Kayıt başarılı");
                                    });
                                });
                            }
                            etUserEmail.setText("");
                            etUserName.setText("");
                            etUserSurname.setText("");
                        }
                    });
                    builder.setNegativeButton("İptal", null);
                    builder.show();
                }
                getMA().showHideLoading(false);
            }
        });

        return view;
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    protected boolean checkUser() {
        boolean isValid = true;
        if (isEmpty(etUserEmail)) {
            etError(etUserEmail,"Mail adresi boş bırakılamaz!" );
            isValid = false;
        } else {
            if (!isEmail(etUserEmail)) {
                etError(etUserEmail,"Lütfen geçerli bir mail adresi giriniz!" );
                isValid = false;
            }
        }

        if (isEmpty(etUserName)) {
            etError(etUserName,"İsim boş bırakılamaz!" );
            isValid = false;
        }

        if (spnUserType.getSelectedItemPosition() == 0) {
            showToast("Lütfen kullanıcı tipini seçiniz!");
            isValid = false;
        }

        if (isEmpty(etUserSurname)) {
            etError(etUserSurname, "Soyisim boş bırakılamaz!");
            isValid = false;
        }
        return isValid;
    }
}
