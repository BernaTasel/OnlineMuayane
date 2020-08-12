package com.bernatasel.onlinemuayene.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.videocall.VideoCallActivity;
import com.bernatasel.onlinemuayene.pojo.firestore.MyChatMessage;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSPatient;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.bernatasel.onlinemuayene.utils.UtilsDate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FragmentChat extends FragmentBase {
    public static FragmentChat newInstance(boolean isDoctor, @NonNull String doctorUid, @NonNull String patientUid, ArrayList<String> patientChecked) {
        Bundle args = new Bundle();
        FragmentChat fragment = new FragmentChat();
        fragment.patientChecked = patientChecked;
        fragment.isDoctor = isDoctor;
        fragment.doctorUid = doctorUid;
        fragment.patientUid = patientUid;
        fragment.setArguments(args);
        return fragment;
    }

    private CollectionReference crChatDoctorPatientData;
    private EditText et;

    private ArrayList<String> patientChecked;
    private boolean isDoctor;
    private String doctorUid;
    private String patientUid;
    private FSDoctor fsDoctor;
    private FSPatient fsPatient;

    private final ArrayList<MyChatMessage> myChatMessages = new ArrayList<>();

    private DocumentReference drChatDoctorPatient;

    private ListenerRegistration listenerRegistration1, listenerRegistration2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView rv = view.findViewById(R.id.rvDrListMessage);
        Button btnInfo = view.findViewById(R.id.btn_info);
        Button btnVideoCall = view.findViewById(R.id.btn_video_call);

        et = view.findViewById(R.id.et);
        Button btnSend = view.findViewById(R.id.btn_send);

        rv.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getMA());
        lm.setStackFromEnd(true);
        lm.setReverseLayout(false);
        rv.setLayoutManager(lm);

        MyAdapter myAdapter = new MyAdapter();
        rv.setAdapter(myAdapter);

        drChatDoctorPatient = FSOps.getInstance().getDRChatDoctorPatient(doctorUid, patientUid);

        crChatDoctorPatientData = FSOps.getInstance().getCRChatDoctorPatientData(doctorUid, patientUid);

        ArrayList<MyChatMessage> myChatMessages = getApp().mySP.getMyChatMessages(doctorUid, patientUid);
        if (myChatMessages != null) {
            this.myChatMessages.addAll(myChatMessages);
            myAdapter.setDataAndNotify(this.myChatMessages);
        }

        listenerRegistration1 = drChatDoctorPatient.addSnapshotListener((documentSnapshot, e) -> {
//            showToast("documentSnapshot changed");

            if (!isDoctor) {
                String callId = documentSnapshot.getString("callId");
                Boolean doctorActive = documentSnapshot.getBoolean("doctorActive");
                Boolean patientActive = documentSnapshot.getBoolean("patientActive");

                if (callId != null && doctorActive != null && doctorActive) {
                    if (!permissionCameraCheckElseAsk()) return;
                    if (!permissionRecordAudioCheckElseAsk()) return;

                    UtilsAndroid.showAlertDialog(getMA(), "Arama", "Doktorunuz görüntülü arama yapmak istiyor. Onaylıyor musunuz?", false, R.string.ac, null, R.string.reddet,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO:
                                    startActivity(VideoCallActivity.newIntent(getMA(), isDoctor, doctorUid, patientUid, callId));
                                }
                            }, null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO:
                                }
                            });
                }
            }
        });

        listenerRegistration2 = crChatDoctorPatientData.addSnapshotListener((queryDocumentSnapshots, e) -> {
            a:
            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    QueryDocumentSnapshot qds = documentChange.getDocument();
                    MyChatMessage newMyChatMessage = qds.toObject(MyChatMessage.class);

                    for (MyChatMessage myChatMessage : this.myChatMessages)
                        if (newMyChatMessage.getTimestamp().equals(myChatMessage.getTimestamp()))
                            continue a;

                    this.myChatMessages.add(newMyChatMessage);
                    myAdapter.setDataAndNotify(this.myChatMessages);

                    if (!isMyMessage(newMyChatMessage)) {//Benim mesajım değilse, listeye ekledikten sonra DB den siliyoruz
                        DocumentReference dr = qds.getReference();
                        dr.delete();
                    }
                }
            }
            rv.scrollToPosition(myAdapter.data.size() - 1);
        });

        btnSend.setOnClickListener(v -> {
            String newMessage = et.getText().toString().trim();
            if (newMessage.isEmpty()) return;
            sendMessage(newMessage);
        });

        btnInfo.setVisibility(isDoctor ? View.VISIBLE : View.GONE);
        btnVideoCall.setVisibility(isDoctor ? View.VISIBLE : View.GONE);

        btnInfo.setOnClickListener(v -> {
            MaterialAlertDialogBuilder madb = new MaterialAlertDialogBuilder(getMA());
            madb.setCancelable(true);

            View viewInfo = LayoutInflater.from(getMA()).inflate(R.layout.dialog_patient_info, null);
            ImageView ivPatient = viewInfo.findViewById(R.id.iv_patient_profile_photo);
            TextView tvPatientInfo = viewInfo.findViewById(R.id.tv_patient_info);

            ivPatient.setImageBitmap(UtilsAndroid.base64ToBitmap(fsPatient.getProfilePhoto()));
            tvPatientInfo.setText(fsPatient.getInfo());

            madb.setView(viewInfo);

            madb.setPositiveButton(R.string.tamam, (dialog, which) -> {
            });

            madb.show();
        });

        btnVideoCall.setOnClickListener(v -> {
            if (!permissionCameraCheckElseAsk()) return;
            if (!permissionRecordAudioCheckElseAsk()) return;

            UtilsAndroid.showAlertDialog(getMA(), null,
                    "Arama başlatmak istediğinize emin misiniz?", true,
                    R.string.evet, null, R.string.hayir,
                    ((dialog, which) -> {
                        startActivity(VideoCallActivity.newIntent(getMA(), isDoctor, doctorUid, patientUid, null));
                    }),
                    null, null);
        });

        FSOps.getInstance().getFSDoctor(doctorUid).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!isAdded()) return;
            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments())
                fsDoctor = ds.toObject(FSDoctor.class);

            if (!isDoctor) setHeaderTitle(fsDoctor.getName() + " " + fsDoctor.getSurname());
        });
        FSOps.getInstance().getFSPatient(patientUid).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!isAdded()) return;
            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments())
                fsPatient = ds.toObject(FSPatient.class);

            if (isDoctor) setHeaderTitle(fsPatient.getName() + " " + fsPatient.getSurname());
        });

        if (patientChecked != null) {
            StringBuilder complaintsText = new StringBuilder("Merhaba,\nŞikayetlerim:\n");
            for (int i = 0; i < patientChecked.size(); i++) {
                complaintsText.append(patientChecked.get(i));
                if (i != patientChecked.size() - 1) complaintsText.append(", ");
            }

            sendMessage(complaintsText.toString());
        }

        updateOnlineStatus(true);

        setHeaderTitle(null);

        return view;
    }

    private void sendMessage(String message) {
        long now = System.currentTimeMillis();

        MyChatMessage myChatMessage = new MyChatMessage(now, !isDoctor, message);

        crChatDoctorPatientData.document("" + now).set(myChatMessage).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                showToast("Mesaj Gönderilemedi");
                return;
            }
            et.setText(null);
        });
    }

    private void updateOnlineStatus(boolean isActive) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put(isDoctor ? "doctorActive" : "patientActive", isActive);
        FSOps.getInstance().updateMerge(drChatDoctorPatient, hm);
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final ArrayList<MyChatMessage> data = new ArrayList<>();

        class MyViewHolder extends RecyclerView.ViewHolder {
            final LinearLayout llWrapper;
            final LinearLayout ll;
            final TextView tvMessage, tvDate;

            MyViewHolder(View view) {
                super(view);
                llWrapper = (LinearLayout) view;
                ll = view.findViewById(R.id.ll);
                tvMessage = view.findViewById(R.id.tv_message);
                tvDate = view.findViewById(R.id.tv_date);
            }
        }

        MyAdapter() {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            MyChatMessage myChatMessage = data.get(position);

            if (isMyMessage(myChatMessage)) {
                myViewHolder.ll.setBackgroundResource(R.drawable.chat_outgoing);
                myViewHolder.llWrapper.setGravity(Gravity.END);

            } else {
                myViewHolder.ll.setBackgroundResource(R.drawable.chat_incoming);
                myViewHolder.llWrapper.setGravity(Gravity.START);
            }

            myViewHolder.tvMessage.setText(myChatMessage.getMessage());
            myViewHolder.tvDate.setText(UtilsDate.timestampToHumanReadable1(myChatMessage.getTimestamp()));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        void setDataAndNotify(ArrayList<MyChatMessage> myChatMessages) {
            data.clear();
            data.addAll(myChatMessages);
            Collections.sort(data, (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
            notifyDataSetChanged();
        }
    }

    private boolean isMyMessage(MyChatMessage myChatMessage) {
        return (myChatMessage.getPatientToDoctor() && !isDoctor) || (!myChatMessage.getPatientToDoctor() && isDoctor);
    }

    @Override
    public boolean onBack() {
        getMA().myFM.clearAll();
        getMA().onDashBoard();
        return false;
    }

    @Override
    public void onDestroy() {
        listenerRegistration1.remove();
        listenerRegistration2.remove();

        getApp().mySP.putMyChatMessages(doctorUid, patientUid, myChatMessages);
        if (fsDoctor != null)
            getApp().mySP.putNameSurname(doctorUid, fsDoctor.getName(), fsDoctor.getSurname());
        if (fsPatient != null)
            getApp().mySP.putNameSurname(patientUid, fsPatient.getName(), fsPatient.getSurname());

        updateOnlineStatus(false);

        super.onDestroy();
    }
}
