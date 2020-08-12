package com.bernatasel.onlinemuayene.fragment.general;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.Constants;
import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.fragment.patient.FragmentPatientSignUp;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSPatient;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentSignUp extends FragmentBase {
    public static FragmentSignUp newInstance() {
        Bundle args = new Bundle();
        FragmentSignUp fragment = new FragmentSignUp();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText etName, etSurname, etEmail, etPassword, etPasswordControl;
    private Button btnSignUp;
    private TextView tvLogin;
    private CheckBox cbPrivacy, cbKVKK;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);
        setHeaderTitle("KAYIT OL");
        initView(view);


        cbPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle("Gizlilik ve Üyelik Sözleşmesi");
                builder.setMessage(getResources().getString(R.string.privacy));
                builder.setPositiveButton("Kabul et", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbPrivacy.setChecked(true);
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbPrivacy.setChecked(false);
                    }
                });
                builder.show();
            }
        });
        cbKVKK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

                builder.setTitle("Kişisel Verileri Koruma Kanunu Metni");
                builder.setMessage(getResources().getString(R.string.kvkk));
                builder.setPositiveButton("Kabul et", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbKVKK.setChecked(true);
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbKVKK.setChecked(false);
                    }
                });
                builder.show();
            }
        });


        btnSignUp.setOnClickListener(v -> {
            if (checkUser()) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String name = etName.getText().toString();
                String surname = etSurname.getText().toString();

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showToast("Bu hesap zaten sistemde kayıtlı!");
                        return;
                    }
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            showToast("Kayıt başarısız(2)");
                            return;
                        }
                        AuthResult authResult = task2.getResult();
                        String uid = authResult.getUser().getUid();
                        FSPatient fsPatient = new FSPatient(uid, Constants.USER_TYPE.PATIENT.getName(), name, surname, email,
                                null, null, null, null, -1,
                                false, false, false, false, null, null,
                                0, "default",true);
                        FSOps.getInstance().getDRUser(email).set(fsPatient);
                        getMA().addFragmentSafe(FragmentPatientSignUp.newInstance(email), MyFM.ANIM.RIGHT);
                        showToast("Kayıt başarılı");
                    });
                });
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMA().myFM.clearAll();
                getMA().addFragmentSafe(FragmentLogin.newInstance(), MyFM.ANIM.RIGHT);
            }
        });

        return view;
    }

    private void initView(View view) {
        etName = view.findViewById(R.id.etName);
        etSurname = view.findViewById(R.id.etSurname);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etPasswordControl = view.findViewById(R.id.etPasswordControl);
        cbPrivacy = view.findViewById(R.id.cbPrivacy);
        cbKVKK = view.findViewById(R.id.cbKVKK);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        tvLogin = view.findViewById(R.id.tvLogin);
    }

    // MAİL VE ŞİFRE KONTROLÜ
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public boolean checkUser() {
        boolean isValid = true;
        //AD
        if (isEmpty(etName)) {
            etError(etName, "İsim boş bırakılamaz!");
            isValid = false;
        }
        //SOYAD
        if (isEmpty(etSurname)) {
            etError(etSurname, "Soyisim boş bırakılamaz!");
            isValid = false;
        }
        //BOŞ MAİL
        if (isEmpty(etEmail)) {
            etError(etEmail, "E-mail boş bırakılamaz!");
            isValid = false;
        }
        //INVALID MAIL
        else {
            if (!isEmail(etEmail)) {
                etError(etEmail, "Lütfen geçerli bir mail adresi giriniz!");
                isValid = false;
            }
        }
        if (!isEmpty(etPassword) && !isEmpty(etPasswordControl)) {
            String str1 = etPassword.getText().toString();
            String str2 = etPasswordControl.getText().toString();

            if (str1.length() < 6 || str2.length() < 6) {
                etError(etPassword, "Şifre en az 6 karakterden oluşmalıdır!");
                isValid = false;
            } else {
                if (!str1.equals(str2)) {
                    etError(etPasswordControl, "Şifreler uyuşmuyor!");
                    etPassword.setError("Şifreler uyuşmuyor!");
                    isValid = false;
                }
            }
        } else {
            etError(etPassword, "Şifre boş bırakılamaz!");
            etError(etPasswordControl, "Şifre boş bırakılamaz!");
            isValid = false;
        }

        if (!cbKVKK.isChecked() || !cbPrivacy.isChecked()) {
            showToast("Lütfen tüm sözleşmeleri onaylayınız!");
            isValid = false;
        }
        return isValid;
    }

}
