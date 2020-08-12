package com.bernatasel.onlinemuayene.fragment.general;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentForgotPassword extends FragmentBase {
    public static FragmentForgotPassword newInstance() {
        Bundle args = new Bundle();
        FragmentForgotPassword fragment = new FragmentForgotPassword();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText etEmail;
    private Button btnSendMail;
    private TextView tvLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_forgot_password, container, false);
        setHeaderTitle("ŞİFREMİ UNUTTUM");
        initView(view);

        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserMail()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail.getText().toString());
                    showToast("Mailiniz kayıtlı ise mail gönderildi");
                }
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
        etEmail = view.findViewById(R.id.etEmail);
        btnSendMail = view.findViewById(R.id.btnSendMail);
        tvLogin = view.findViewById(R.id.tvLogin);
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    protected boolean checkUserMail() {
        boolean isValid = true;

        if (isEmpty(etEmail)) {
            etError(etEmail, "Mail adresi boş bırakılamaz!");
            isValid = false;
        } else {
            if (!isEmail(etEmail)) {
                etError(etEmail, "Lütfen geçerli bir mail adresi giriniz!");
                isValid = false;
            }
        }
        return isValid;
    }
}
