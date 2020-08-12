package com.bernatasel.onlinemuayene.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.utils.MyFM;

public class FragmentDashboardAdmin extends FragmentBase {
    public static FragmentDashboardAdmin newInstance() {
        Bundle args = new Bundle();
        FragmentDashboardAdmin fragment = new FragmentDashboardAdmin();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_main, container, false);

        Button btnUsers = view.findViewById(R.id.btnUsers);
        Button btnSuggestions = view.findViewById(R.id.btnSuggestions);
        Button btnSendNotice = view.findViewById(R.id.btnSendNotice);

        btnUsers.setOnClickListener(v -> {
            getMA().myFM.clearAll();
            getMA().addFragmentSafe(FragmentAdminUsers.newInstance(), MyFM.ANIM.RIGHT);
        });


        btnSuggestions.setOnClickListener(v -> {
            getMA().addFragmentSafe(FragmentAdminListSuggestions.newInstance(), MyFM.ANIM.RIGHT);
        });

        btnSendNotice.setOnClickListener(v -> {

        });

        return view;
    }
}
