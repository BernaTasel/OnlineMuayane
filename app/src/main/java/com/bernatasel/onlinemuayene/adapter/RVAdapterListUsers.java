package com.bernatasel.onlinemuayene.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.MainActivity;
import com.bernatasel.onlinemuayene.MyApp;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firestore.v1.UpdateDocumentRequest;

import java.util.HashMap;
import java.util.List;

public class RVAdapterListUsers extends RecyclerView.Adapter<RVAdapterListUsers.CardViewObjectHolder> {
    public Context mContext;
    private List<FSUser> users;
    private String loginUserEmail;

    public RVAdapterListUsers(Context mContext, List<FSUser> users, String loginUserEmail) {
        this.mContext = mContext;
        this.users = users;
        this.loginUserEmail = loginUserEmail;
    }

    public class CardViewObjectHolder extends RecyclerView.ViewHolder {
        public TextView tvUserType, tvUserName, tvUserSurname, tvUserEmail;
        public ImageView ivBlocked;
        public CardView cvUsers;

        public CardViewObjectHolder(View view) {
            super(view);
            tvUserType = view.findViewById(R.id.tvUserType);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvUserSurname = view.findViewById(R.id.tvUserSurname);
            tvUserEmail = view.findViewById(R.id.tvUserEmail);
            ivBlocked = view.findViewById(R.id.ivBlocked);
            cvUsers = view.findViewById(R.id.cvUsers);
        }
    }

    @NonNull
    @Override
    public RVAdapterListUsers.CardViewObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_users, parent, false);
        return new CardViewObjectHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RVAdapterListUsers.CardViewObjectHolder holder, int position) {
        final FSUser user = users.get(position);

        //CARD VİEW BİLGİ GÖNDERME
        if (user.getType().equals("admin")) {
            holder.tvUserType.setText("A");
        }
        if (user.getType().equals("doctor")) {
            holder.tvUserType.setText("D");
        }
        if (user.getType().equals("patient")) {
            holder.tvUserType.setText("H");
        }
        if(user.isActiveAccount()){
            holder.ivBlocked.setVisibility(View.INVISIBLE);
        }
        else {
            holder.ivBlocked.setVisibility(View.VISIBLE);
        }

        holder.tvUserName.setText(user.getName());
        holder.tvUserSurname.setText(user.getSurname());
        holder.tvUserEmail.setText(user.getEmail());


        //TIKLAMA DİNLEME
        holder.cvUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                if (user.getType().equals("admin")) {
                    builder.setTitle("Admin Bilgileri: ");
                    builder.setMessage("Email: " + user.getEmail() +
                            "\nİsim Soyisim: " + user.getName() + " " + user.getSurname());
                }
                if (user.getType().equals("doctor")) {
                    builder.setTitle("Doktor Bilgileri: ");
                    builder.setMessage("Email: " + user.getEmail() +
                            "\nİsim Soyisim: " + user.getName() + " " + user.getSurname());
                }
                if (user.getType().equals("patient")) {
                    builder.setTitle("Hasta Bilgileri: ");
                    builder.setMessage("Email: " + user.getEmail() +
                            "\nİsim Soyisim: " + user.getName() + " " + user.getSurname());
                }
                if(user.isActiveAccount()){
                    builder.setPositiveButton("Kullanıcıyı Engelle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(user.getEmail().equals(loginUserEmail)){
                                Toast.makeText(mContext, "Bu kullanıcı engellenemez!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                user.setActiveAccount(false);
                                HashMap<String, Object> userInfo = new HashMap<String, Object>();
                                userInfo.put("activeAccount", user.isActiveAccount());
                                FSOps.getInstance().updateUser(user.getEmail(), userInfo);
                                holder.ivBlocked.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else {
                    builder.setPositiveButton("Kullanıcının Engelini Kaldır", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.setActiveAccount(true);
                            HashMap<String, Object> userInfo = new HashMap<String, Object>();
                            userInfo.put("activeAccount", user.isActiveAccount());
                            FSOps.getInstance().updateUser(user.getEmail(), userInfo);
                            holder.ivBlocked.setVisibility(View.INVISIBLE);
                            holder.ivBlocked.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                builder.setNegativeButton("İptal", null);
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
