package com.bernatasel.onlinemuayene.fragment.general;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.pojo.firestore.FSSuggestion;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;

public class FragmentMakeSuggestion extends FragmentBase {

    public static FragmentMakeSuggestion newInstance() {
        Bundle args = new Bundle();
        FragmentMakeSuggestion fragment = new FragmentMakeSuggestion();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText etSuggestionText, etSuggestionTitle, etSuggestionEmail;
    private Button btnSendSuggestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_make_suggestion, container, false);
        setHeaderTitle("ÖNERİ/ŞİKAYET");
        etSuggestionEmail = view.findViewById(R.id.etSuggestionEmail);
        etSuggestionTitle = view.findViewById(R.id.etSuggestionTitle);
        etSuggestionText = view.findViewById(R.id.etSuggestionText);
        btnSendSuggestion = view.findViewById(R.id.btnSendSuggestion);

        if(getApp().isLoggedIn()){
            etSuggestionEmail.setText(getApp().getLoggedInPerson().getEmail());
        }

        btnSendSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etClearError(etSuggestionEmail, etSuggestionTitle);
                String email = etSuggestionEmail.getText().toString();
                String title = etSuggestionTitle.getText().toString();
                String text = etSuggestionTitle.getText().toString();
                if (email.isEmpty() || !UtilsAndroid.isEmail(email)) {
                    etError(etSuggestionEmail, "Lütfen geçerli bir mail adresi giriniz!");
                    return;
                }
                if (title.isEmpty()) {
                    etError(etSuggestionTitle, "Konu boş bırakılamaz!");
                    return;
                }
                if (text.isEmpty()) {
                    etError(etSuggestionText, "Mesaj boş bırakılamaz!");
                    return;
                }
                sendSuggestion();
            }
        });
        return view;
    }

    private void sendSuggestion(){
        FSSuggestion fsSuggestion = new FSSuggestion(
                etSuggestionEmail.getText().toString(),
                etSuggestionTitle.getText().toString(),
                etSuggestionText.getText().toString(),
                Timestamp.now(), false);
        FSOps.getInstance().getCRSuggestion().document(fsSuggestion.getId()).set(fsSuggestion);
        etClear(etSuggestionText, etSuggestionTitle);
        showToast("Geri bildiriminiz için teşekkürler!");
    }
}
