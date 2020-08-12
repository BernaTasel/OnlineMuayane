package com.bernatasel.onlinemuayene.fragment.general;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;

public class FragmentAboutUs extends FragmentBase {

    public static FragmentAboutUs newInstance() {
        FragmentAboutUs fragment = new FragmentAboutUs();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        setHeaderTitle("HAKKIMIZDA");

        return view;
    }
}
