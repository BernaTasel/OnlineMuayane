package com.bernatasel.onlinemuayene.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.admin.FragmentAdminListSuggestions;
import com.bernatasel.onlinemuayene.pojo.firestore.FSSuggestion;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;

import java.util.HashMap;
import java.util.List;

public class RVAdapterListSuggestion extends RecyclerView.Adapter<RVAdapterListSuggestion.CardViewObjectHolder>{
    public Context mContext;
    private List<FSSuggestion> suggestions;
    public RVAdapterListSuggestion(Context mContext, List<FSSuggestion> suggestions){
        this.mContext = mContext;
        this.suggestions = suggestions;
    }

    public class CardViewObjectHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle, tvText, tvDate;
        public CardView cvSuggestion;
        public ImageView ivSolved;
        public  CardViewObjectHolder(View view){
            super(view);
            tvTitle = view.findViewById(R.id.tvSuggestionTitle);
            tvText = view.findViewById(R.id.tvSuggestionText);
            tvDate = view.findViewById(R.id.tvSuggestionDate);
            cvSuggestion = view.findViewById(R.id.cvSuggestion);
            ivSolved = view.findViewById(R.id.ivSolved);
        }
    }

    @NonNull
    @Override
    public CardViewObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_suggestion, parent, false);
        return new CardViewObjectHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewObjectHolder holder, int position) {
        final FSSuggestion suggestion = suggestions.get(position);

        if(!suggestion.isSolved()){
            holder.ivSolved.setVisibility(View.INVISIBLE);
        }
        holder.tvTitle.setText(suggestion.getTitle());
        holder.tvText.setText(suggestion.getText());
        holder.tvText.setMaxLines(1);
        holder.tvDate.setText(String.valueOf(suggestion.getDate().toDate()));

        holder.cvSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggestion.isSolved()){ //YANITLANMIŞ ÖNERİ
                    UtilsAndroid.showAlertDialog(mContext, suggestion.getTitle(), suggestion.getText()+"\nGönderen: "+suggestion.getEmail(),
                            true, R.string.unsolved, null, R.string.iptal,
                            ((dialog, which) -> {
                                suggestion.setSolved(false);
                                FSOps.getInstance().updateSuggestionSolve(suggestion.getId(), suggestion.isSolved());
                                holder.ivSolved.setVisibility(View.INVISIBLE);
                                Toast.makeText(mContext, "Yanıtlanmadı olarak işaretlendi.", Toast.LENGTH_SHORT).show();
                            }),
                            null,
                            null);
                }
                else{ //YANITLANMAMIŞ ÖNERİ
                    UtilsAndroid.showAlertDialog(mContext, suggestion.getTitle(), suggestion.getText()+"\nGönderen: "+suggestion.getEmail(),
                            true, R.string.yanitla, R.string.iptal, R.string.solved,
                            ((dialog, which) -> {
                                try {
                                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                    emailIntent.setType("*/*");
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{suggestion.getEmail()});
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT,("Cevap: "+suggestion.getTitle()+" Hakkında"));
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, ("Mesaj::\n"+ suggestion.getText()+"\nCevap:\n"));
                                    mContext.startActivity(Intent.createChooser(emailIntent,"Seçiniz: "));
                                }
                                catch (Throwable t){
                                    Toast.makeText(mContext, "Bir hata oluştu.", Toast.LENGTH_SHORT).show();
                                }

                            }),

                            ((dialog, which) -> {
                                dialog.cancel();
                            }),

                            ((dialog, which) -> {
                                suggestion.setSolved(true);
                                FSOps.getInstance().updateSuggestionSolve(suggestion.getId(), suggestion.isSolved());
                                holder.ivSolved.setVisibility(View.VISIBLE);
                                Toast.makeText(mContext, "Yanıtlandı olarak işaretlendi.", Toast.LENGTH_SHORT).show();
                            }));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

}
