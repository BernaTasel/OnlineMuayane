package com.bernatasel.onlinemuayene.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdminUsers extends FragmentBase {
    public static FragmentAdminUsers newInstance() {
        Bundle args = new Bundle();
        FragmentAdminUsers fragment = new FragmentAdminUsers();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_users, container, false);
        setHeaderTitle("KULLANICILAR");
        TabLayout tlAdminDoctor = view.findViewById(R.id.tlAdminDoctor);
        ViewPager vpAdminDoctor = view.findViewById(R.id.vpAdminDoctor);

        RVAdapterAdminDoctors adapter = new RVAdapterAdminDoctors(getMA().myFM.getSupportFragmentManager());
        adapter.addFragment(new FragmentAdminAddUser(), "Ekle");
        adapter.addFragment(new FragmentAdminListUsers(), "Görüntüle");
        vpAdminDoctor.setAdapter(adapter);
        tlAdminDoctor.setupWithViewPager(vpAdminDoctor);

        return view;
    }

    private static class RVAdapterAdminDoctors extends FragmentStatePagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        RVAdapterAdminDoctors(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }
}
