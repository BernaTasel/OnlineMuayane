package com.bernatasel.onlinemuayene.fragment.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.MyApp;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.fragment.FragmentChat;
import com.bernatasel.onlinemuayene.pojo.firestore.MyChatMessage;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSPatient;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.bernatasel.onlinemuayene.utils.UtilsDate;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentDoctorDashboard extends FragmentBase {

    public static FragmentDoctorDashboard newInstance() {
        FragmentDoctorDashboard fragment = new FragmentDoctorDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private final ArrayList<Data> dataAll = new ArrayList<>();
    private MyAdapter myAdapter;
    private ListenerRegistration listenerRegistration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_list_messages, container, false);
        RecyclerView rv = view.findViewById(R.id.rvDrListMessage);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        RadioGroup rgDoctorStatus = view.findViewById(R.id.rgDoctorStatus);
        RadioButton avaible = view.findViewById(R.id.avaible);
        RadioButton busy = view.findViewById(R.id.busy);
        RadioButton onlyMsg = view.findViewById(R.id.only_msg);

        FSUser user = getApp().getLoggedInPerson();

        int userChoice = user.getAvailability();
        if (userChoice == 1) {
            avaible.setChecked(true);
        } else if (userChoice == 2) {
            busy.setChecked(true);
        } else if (userChoice == 3) {
            onlyMsg.setChecked(true);
        }

        rgDoctorStatus.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedId = rgDoctorStatus.getCheckedRadioButtonId();
            if (selectedId == R.id.avaible) {
                user.setAvailability(1);
            } else if (selectedId == R.id.busy) {
                user.setAvailability(2);
            } else {
                user.setAvailability(3);
            }
            HashMap<String, Object> doctorInfo = new HashMap<String, Object>();
            doctorInfo.put("availability", user.getAvailability());
            FSOps.getInstance().updateUser(user.getEmail(), doctorInfo);
        });

        myAdapter = new MyAdapter();
        rv.setAdapter(myAdapter);

        //Get new patient messages
        String doctorUid = MyApp.getInstance().getLoggedInPerson().getUid();
        listenerRegistration = FSOps.getInstance().getCRChatDoctorPatient(doctorUid).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots == null) return;

            ArrayList<String> chatTitles = getApp().mySP.getChatTitles();

            for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                String patientUid = ds.getId();

                if (!chatTitles.contains(doctorUid + "_" + patientUid)) {
                    FSOps.getInstance().getUserByUID(patientUid).addOnSuccessListener(queryDocumentSnapshots1 -> {
                        //Listeye Yeni hasta ekle
                        getApp().mySP.putMyChatMessages(doctorUid, patientUid, new ArrayList<>());

                        for (DocumentSnapshot dsFSUser : queryDocumentSnapshots1.getDocuments()) {
                            FSUser fsUser = dsFSUser.toObject(FSPatient.class);
                            getApp().mySP.putNameSurname(patientUid, fsUser.getName(), fsUser.getSurname());
                        }
                        updateContent();
                    });
                }
            }
        });

        updateContent();

        return view;
    }

    @Override
    public void updateContent() {
        handler.postDelayed(() -> {
            if (!isAdded()) return;

            dataAll.clear();

            String doctorUid = getApp().getLoggedInPerson().getUid();//Bu sayfayı sadece doktor açmalı
            ArrayList<String> chatTitles = getApp().mySP.getChatTitles();
            for (String chatTitle : chatTitles) {
                if (chatTitle.startsWith(doctorUid)) {
                    String[] s = chatTitle.split("_");
                    String patientUid = s[1];

                    ArrayList<MyChatMessage> myChatMessages = getApp().mySP.getMyChatMessages(doctorUid, patientUid);
                    myAdapter.addDataAndNotify(new Data(patientUid, getApp().mySP.getNameSurname(patientUid), myChatMessages));
                }
            }
        }, 500);
    }

    public static class Data {
        private String uid;
        private String name;
        private ArrayList<MyChatMessage> myChatMessages;

        public Data(String uid, String name, ArrayList<MyChatMessage> myChatMessages) {
            this.uid = uid;
            this.name = name;
            this.myChatMessages = myChatMessages;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<MyChatMessage> getMyChatMessages() {
            return myChatMessages;
        }

        public void setMyChatMessages(ArrayList<MyChatMessage> myChatMessages) {
            this.myChatMessages = myChatMessages;
        }
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
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyAdapter.MyViewHolder myViewHolder = (MyAdapter.MyViewHolder) holder;

            Data data = dataAll.get(position);

            myViewHolder.view.setOnClickListener(v -> {
                String doctorUid = getApp().getLoggedInPerson().getUid();

                getMA().myFM.clearAll();
                getMA().addFragmentSafe(FragmentChat.newInstance(true, doctorUid, data.getUid(), null), MyFM.ANIM.RIGHT);
            });

            FSOps.getInstance().getFSPatient(data.getUid()).addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    FSPatient fsPatient = queryDocumentSnapshot.toObject(FSPatient.class);
                    myViewHolder.iv.setImageBitmap(UtilsAndroid.base64ToBitmap(fsPatient.getProfilePhoto()));
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

        void addDataAndNotify(Data data) {
            dataAll.add(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        listenerRegistration.remove();
        super.onDestroy();
    }
}
