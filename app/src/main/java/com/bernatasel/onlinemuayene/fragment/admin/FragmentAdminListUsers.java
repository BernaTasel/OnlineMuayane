package com.bernatasel.onlinemuayene.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.adapter.RVAdapterListUsers;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class FragmentAdminListUsers extends FragmentBase {
    public static FragmentAdminListUsers newInstance() {
        Bundle args = new Bundle();
        FragmentAdminListUsers fragment = new FragmentAdminListUsers();
        fragment.setArguments(args);
        return fragment;
    }

    private Spinner spnUserType;
    private RecyclerView rv;
    private List<FSUser> users = new LinkedList<>();;
    private RVAdapterListUsers adapter;
    FSUser loggedInPerson = getApp().getLoggedInPerson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_list_users, container, false);
        spnUserType = rootView.findViewById(R.id.spnUserType);
        ArrayAdapter<String> dataAdapterForUserTypes = new ArrayAdapter<String>(getMA(), R.layout.spinner_style, getResources().getStringArray(R.array.user_type_array));
        spnUserType.setAdapter(dataAdapterForUserTypes);
        rv = rootView.findViewById(R.id.rvListUsers);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        spnUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    FSOps.getInstance().getCRUser().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getMA().showHideLoading(true);
                            if (task.isSuccessful()) {
                                users.clear();
                                for (QueryDocumentSnapshot qds : task.getResult()) {
                                    FSUser user = qds.toObject(FSUser.class);
                                    users.add(user);
                                    System.out.println(user);
                                }
                                adapter = new RVAdapterListUsers(getActivity(), users, loggedInPerson.getEmail());
                                rv.setAdapter(adapter);
                            }
                            getMA().showHideLoading(false);
                        }
                    });

                }
                if (position == 1) {
                    FSOps.getInstance().getAdmins().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                users.clear();
                                for (QueryDocumentSnapshot qds : task.getResult()) {
                                    FSUser user = qds.toObject(FSUser.class);
                                    users.add(user);
                                    System.out.println(user);
                                }
                                adapter = new RVAdapterListUsers(getActivity(), users, loggedInPerson.getEmail());
                                rv.setAdapter(adapter);
                            }
                        }
                    });
                }
                if (position == 2) {
                    FSOps.getInstance().getDoctors().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                users.clear();
                                for (QueryDocumentSnapshot qds : task.getResult()) {
                                    FSUser user = qds.toObject(FSUser.class);
                                    users.add(user);
                                    System.out.println(user);
                                }
                                adapter = new RVAdapterListUsers(getActivity(), users, loggedInPerson.getEmail());
                                rv.setAdapter(adapter);
                            }
                        }
                    });
                }
                if (position == 3) {
                    FSOps.getInstance().getPatients().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                users.clear();
                                for (QueryDocumentSnapshot qds : task.getResult()) {
                                    FSUser user = qds.toObject(FSUser.class);
                                    users.add(user);
                                    System.out.println(user);
                                }
                                adapter = new RVAdapterListUsers(getActivity(), users, loggedInPerson.getEmail());
                                rv.setAdapter(adapter);
                            }
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }
}
