package com.bernatasel.onlinemuayene.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bernatasel.onlinemuayene.Constants;
import com.bernatasel.onlinemuayene.MainActivity;
import com.bernatasel.onlinemuayene.MyApp;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentChat;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterChooseDoctor extends RecyclerView.Adapter<RVAdapterChooseDoctor.CardViewObjectHolder> {
    private MainActivity mainActivity;
    private List<FSDoctor> doctors;
    private ArrayList<String> patientChecked;

    public RVAdapterChooseDoctor(MainActivity mainActivity, List<FSDoctor> doctors, ArrayList<String> patientChecked) {
        this.mainActivity = mainActivity;
        this.doctors = doctors;
        this.patientChecked = patientChecked;
    }

    static class CardViewObjectHolder extends RecyclerView.ViewHolder {
        CircleImageView ivDoctorProfilePhoto;
        TextView tvDoctorNameSurname, tvDoctorProfession, tvIsDoctorAvaible;
        CardView cvDoctor;

        CardViewObjectHolder(View view) {
            super(view);
            ivDoctorProfilePhoto = view.findViewById(R.id.ivDoctorProfilePhoto);
            tvDoctorNameSurname = view.findViewById(R.id.tvDoctorNameSurname);
            tvDoctorProfession = view.findViewById(R.id.tvDoctorProfession);
            tvIsDoctorAvaible = view.findViewById(R.id.tvIsDoctorAvaible);
            cvDoctor = view.findViewById(R.id.cvDoctor);
        }
    }

    @NonNull
    @Override
    public CardViewObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_doctor, parent, false);
        return new CardViewObjectHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewObjectHolder holder, int position) {
        final FSDoctor doctor = doctors.get(position);
        if (!doctor.getProfilePhoto().equals("default")) {
            holder.ivDoctorProfilePhoto.setImageBitmap(UtilsAndroid.base64ToBitmap(doctor.getProfilePhoto()));
        } else {
            holder.ivDoctorProfilePhoto.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.doctor_icon));
            //Picasso.get().load(R.drawable.doctor_icon).into(holder.ivDoctorProfilePhoto);
        }
        holder.tvDoctorNameSurname.setText(doctor.getName() + " " + doctor.getSurname());
        holder.tvDoctorProfession.setText(doctor.getProfession());

        Constants.STATUS status = Constants.STATUS.getByValue(doctor.getAvailability());
        holder.tvIsDoctorAvaible.setText(status != null ? status.getName() : Constants.STATUS.OFFLINE.getName());

        holder.cvDoctor.setOnClickListener(v -> UtilsAndroid.showAlertDialog(mainActivity, null,
                "Doktor " + doctor.getName() + " ile görüşme başlatılıyor. Onaylıyor musunuz?", true,
                R.string.evet, null, R.string.hayir,
                ((dialog, which) -> {
                    mainActivity.addFragmentSafe(FragmentChat.newInstance(MyApp.getInstance().getLoggedInPerson().isDoctor(), doctor.getUid(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(), patientChecked), MyFM.ANIM.RIGHT);
                }),
                null,
                ((dialog, which) -> {
                    dialog.cancel();
                })));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }
}
