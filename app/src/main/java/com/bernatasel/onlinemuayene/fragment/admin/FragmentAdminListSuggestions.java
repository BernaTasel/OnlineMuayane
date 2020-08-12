package com.bernatasel.onlinemuayene.fragment.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.adapter.RVAdapterListSuggestion;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.pojo.firestore.FSSuggestion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FragmentAdminListSuggestions extends FragmentBase {

    public static FragmentAdminListSuggestions newInstance() {
        FragmentAdminListSuggestions fragment = new FragmentAdminListSuggestions();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView rv;
    private RadioGroup rgListUnsolved;
    private RadioButton rbAll;
    private List<FSSuggestion> suggestions = new LinkedList<>();
    private List<FSSuggestion> unsolvedSuggestions = new LinkedList<>();
    private RVAdapterListSuggestion adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_list_suggestions, container, false);
        setHeaderTitle("ÖNERİ/ŞİKAYETLER");
        rv = view.findViewById(R.id.rvListSuggestions);
        rgListUnsolved = view.findViewById(R.id.rgListUnsolved);
        rbAll = view.findViewById(R.id.rbAll);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        FSOps.getInstance().getCRSuggestion().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot qds : task.getResult()){
                        FSSuggestion suggestion = qds.toObject(FSSuggestion.class);
                        suggestions.add(suggestion);
                        if(!suggestion.isSolved()) unsolvedSuggestions.add(suggestion);
                    }
                    rbAll.setChecked(true);
                    adapter = new RVAdapterListSuggestion(getActivity(), suggestions);
                    rv.setAdapter(adapter);
                }
            }
        });

        rgListUnsolved.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbAll:
                        adapter = new RVAdapterListSuggestion(getActivity(), suggestions);
                        rv.setAdapter(adapter);
                        return;
                    case R.id.rbUnsolved:
                        unsolvedSuggestions.clear();
                        FSOps.getInstance().getCRSuggestion().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot qds : task.getResult()){
                                        FSSuggestion suggestion = qds.toObject(FSSuggestion.class);
                                        if(!suggestion.isSolved()) unsolvedSuggestions.add(suggestion);
                                    }
                                    adapter = new RVAdapterListSuggestion(getActivity(), unsolvedSuggestions);
                                    rv.setAdapter(adapter);
                                }
                            }
                        });
                        return;
                }
            }
        });

        return view;
    }

}




