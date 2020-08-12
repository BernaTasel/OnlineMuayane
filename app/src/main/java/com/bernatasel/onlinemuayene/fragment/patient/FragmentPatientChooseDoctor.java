package com.bernatasel.onlinemuayene.fragment.patient;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.adapter.RVAdapterChooseDoctor;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FragmentPatientChooseDoctor extends FragmentBase {
    public static FragmentPatientChooseDoctor newInstance(ArrayList<String> patientChecked) {
        Bundle args = new Bundle();
        FragmentPatientChooseDoctor fragment = new FragmentPatientChooseDoctor();
        fragment.patientChecked = patientChecked;
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<String> patientChecked;
    private RVAdapterChooseDoctor adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_patient_choose_doctor, container, false);

        setHeaderTitle("DOKTOR SEÇİNİZ");
        TextView etDoctorFilter = view.findViewById(R.id.etDoctorFilter);
        RecyclerView rv = view.findViewById(R.id.rvChooseDoctor);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getMA()));

        ArrayList<FSDoctor> doctors = new ArrayList<>();
        FSOps.getInstance().getDoctors().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getMA().showHideLoading(true);
                ArrayList<String> doctorProfessions = new ArrayList<>();
                for (QueryDocumentSnapshot qds : task.getResult()) {
                    FSDoctor doctor = qds.toObject(FSDoctor.class);
                    String profession = doctor.getProfession();

                    if (profession != null) {
                        doctors.add(doctor);

                        if (!doctorProfessions.contains(profession))
                            doctorProfessions.add(profession);
                    }
                }
                adapter = new RVAdapterChooseDoctor(getMA(), doctors, patientChecked);
                rv.setAdapter(adapter);

                //
                boolean[] isChecked = new boolean[doctorProfessions.size()];
                ArrayList<String> patientCheckedProfession = new ArrayList<>();

                String[] doctorProfessionsPrimitive = doctorProfessions.toArray(new String[]{});

                etDoctorFilter.setOnClickListener(v -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                    builder.setMultiChoiceItems(doctorProfessionsPrimitive, isChecked, (dialog, which, isChecked1) -> {
                        patientCheckedProfession.remove(doctorProfessionsPrimitive[0]);
                        if (isChecked1) {
                            patientCheckedProfession.add(doctorProfessionsPrimitive[which]);
                        } else {
                            patientCheckedProfession.remove(doctorProfessionsPrimitive[which]);
                        }
                    });
                    builder.setCancelable(false);
                    builder.setPositiveButton("Tamam", (dialog, which) -> {
                        ArrayList<FSDoctor> choosenDoctors = choosedDoctors(doctors, patientCheckedProfession);
                        if (patientCheckedProfession.isEmpty()) {
                            choosenDoctors.clear();
                            choosenDoctors.addAll(doctors);
                        }
                        adapter = new RVAdapterChooseDoctor(getMA(), choosenDoctors, patientChecked);
                        rv.setAdapter(adapter);
                    });

                    builder.setNegativeButton("İptal", (dialog, which) -> {
                        for (int i = 0; i < doctorProfessionsPrimitive.length; i++) {
                            isChecked[i] = false;
                            patientCheckedProfession.clear();
                        }
                    });

                    builder.create().show();
                });
            }
            getMA().showHideLoading(false);
        });

        return view;
    }

    public ArrayList<FSDoctor> choosedDoctors(ArrayList<FSDoctor> allDoctors, ArrayList<String> patientCheckedProfession) {
        ArrayList<FSDoctor> choosenDoctors = new ArrayList<>();
        for (String profession : patientCheckedProfession) {
            if (profession.equals("Tüm Doktorlar")) {
                return allDoctors;
            }
            for (FSDoctor doctor : allDoctors) {
                if (doctor.getProfession().equals(profession)) {
                    choosenDoctors.add(doctor);
                }
            }
        }
        return choosenDoctors;
    }
}
