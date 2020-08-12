package com.bernatasel.onlinemuayene.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.general.FragmentLogin;
import com.bernatasel.onlinemuayene.utils.MyFM;

public class FragmentDashboard extends FragmentBase {
    public static FragmentDashboard newInstance() {
        Bundle args = new Bundle();
        FragmentDashboard fragment = new FragmentDashboard();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Button btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> getMA().addFragmentSafe(FragmentLogin.newInstance(), MyFM.ANIM.RIGHT));

        return view;
    }
}
