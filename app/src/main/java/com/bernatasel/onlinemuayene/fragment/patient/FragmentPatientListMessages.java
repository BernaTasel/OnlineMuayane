package com.bernatasel.onlinemuayene.fragment.patient;

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
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.fragment.FragmentChat;
import com.bernatasel.onlinemuayene.fragment.doctor.FragmentDoctorDashboard;
import com.bernatasel.onlinemuayene.pojo.firestore.MyChatMessage;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.bernatasel.onlinemuayene.utils.UtilsDate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentPatientListMessages extends FragmentBase {
    public static FragmentPatientListMessages newInstance() {
        Bundle args = new Bundle();
        FragmentPatientListMessages fragment = new FragmentPatientListMessages();
        fragment.setArguments(args);
        return fragment;
    }

    private final ArrayList<FragmentDoctorDashboard.Data> dataAll = new ArrayList<>();
    private MyAdapter myAdapter;
    private TextView tvNoMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_list_massages_layout, container, false);
        FloatingActionButton btnGetComplaint = view.findViewById(R.id.btnGetComplaint);
        tvNoMessage = view.findViewById(R.id.tvNoMessage);

        //ŞİKAYET LİSTESİ
        String[] complaint = getResources().getStringArray(R.array.complaint_items);
        boolean[] isChecked = new boolean[complaint.length];

        //YENİ BAŞVURU
        btnGetComplaint.setOnClickListener(v -> {
            ArrayList<String> patientChecked = new ArrayList<>();
            for (int i = 0; i < complaint.length; i++) {
                isChecked[i] = false;
            }
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
            builder.setTitle("Şikayetiniz/şikayetleriniz nedir?");
            builder.setMultiChoiceItems(complaint, isChecked, (dialog, which, isChecked1) -> {
                if (isChecked1) {
                    patientChecked.add(complaint[which]);
                } else {
                    patientChecked.remove(complaint[which]);
                }
            });
            builder.setCancelable(false);
            builder.setPositiveButton("Tamam", (dialog, which) -> {
                getMA().myFM.clearAll();
                getMA().addFragmentSafe(FragmentPatientChooseDoctor.newInstance(patientChecked), MyFM.ANIM.RIGHT);
            });

            builder.setNegativeButton("İptal", (dialog, which) -> {
                for (int i = 0; i < complaint.length; i++) {
                    isChecked[i] = false;
                    patientChecked.clear();
                }
            });
            builder.create().show();
        });

        //RECYCLER VİEW
        RecyclerView rv = view.findViewById(R.id.rvPatientListMessage);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        myAdapter = new MyAdapter();
        rv.setAdapter(myAdapter);

        updateContent();

        return view;
    }

    @Override
    public void updateContent() {
        handler.postDelayed(() -> {
            if (!isAdded()) return;

            dataAll.clear();

            String patientUid = getApp().getLoggedInPerson().getUid();//Bu sayfayı sadece hasta açmalı
            ArrayList<String> chatTitles = getApp().mySP.getChatTitles();
            for (String chatTitle : chatTitles) {
                if (chatTitle.contains(patientUid)) {
                    String[] s = chatTitle.split("_");
                    String doctorUid = s[0];

                    ArrayList<MyChatMessage> myChatMessages = getApp().mySP.getMyChatMessages(doctorUid, patientUid);
                    myAdapter.addDataAndNotify(new FragmentDoctorDashboard.Data(doctorUid, getApp().mySP.getNameSurname(doctorUid), myChatMessages));
                }
            }

            tvNoMessage.setVisibility(dataAll.isEmpty() ? View.VISIBLE : View.GONE);
        }, 500);
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        class MyViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final CircleImageView iv;
            final TextView tvTitle, tvLastMessage, tvLastMessageTimestamp;

            MyViewHolder(View view) {
                super(view);
                this.view = view;
                iv = view.findViewById(R.id.iv);
                tvTitle = view.findViewById(R.id.tv_title);
                tvLastMessage = view.findViewById(R.id.tv_last_message);
                tvLastMessageTimestamp = view.findViewById(R.id.tv_last_message_timestamp);
            }
        }

        public MyAdapter() {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyAdapter.MyViewHolder myViewHolder = (MyAdapter.MyViewHolder) holder;

            FragmentDoctorDashboard.Data data = dataAll.get(position);

            myViewHolder.view.setOnClickListener(v -> {
                String patientUid = getApp().getLoggedInPerson().getUid();

                getMA().myFM.clearAll();
                getMA().addFragmentSafe(FragmentChat.newInstance(false, data.getUid(), patientUid, null), MyFM.ANIM.RIGHT);
            });

            //

            FSOps.getInstance().getFSDoctor(data.getUid()).addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    FSDoctor fsDoctor = queryDocumentSnapshot.toObject(FSDoctor.class);
                    String profilePhoto = fsDoctor.getProfilePhoto();
                    if (profilePhoto != null && !profilePhoto.isEmpty() && !profilePhoto.equals("default"))
                        myViewHolder.iv.setImageBitmap(UtilsAndroid.base64ToBitmap(profilePhoto));
                    else
                        myViewHolder.iv.setImageResource(R.drawable.doctor_icon);
                    break;
                }
            });

            myViewHolder.tvTitle.setText(data.getName());

            myViewHolder.tvLastMessage.setText(null);
            if (!data.getMyChatMessages().isEmpty()) {
                MyChatMessage myChatMessageLast = data.getMyChatMessages().get(data.getMyChatMessages().size() - 1);

                myViewHolder.tvLastMessage.setText(myChatMessageLast.getMessage());
                myViewHolder.tvLastMessageTimestamp.setText(UtilsDate.timestampToHumanReadable(myChatMessageLast.getTimestamp()));
            }
        }

        @Override
        public int getItemCount() {
            return dataAll.size();
        }

        void addDataAndNotify(FragmentDoctorDashboard.Data data) {
            dataAll.add(data);
            notifyDataSetChanged();
        }
    }
}
