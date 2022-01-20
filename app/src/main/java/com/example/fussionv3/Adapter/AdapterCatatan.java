package com.example.fussionv3.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fussionv3.R;
import com.example.fussionv3.roomdatabase.AppDatabase;
import com.example.fussionv3.roomdatabase.entitas.Catatan;

import java.util.List;

import butterknife.ButterKnife;

public class AdapterCatatan extends RecyclerView.Adapter<AdapterCatatan.CatatanViewHolder> {

    private List<Catatan> catatanList;
    private Context context;
    Bundle bundle;
    AppDatabase database;

    private Dialog dialog;

    public interface Dialog {
        void onClick(int position);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public AdapterCatatan(List<Catatan> catatanList, Context context) {
        this.catatanList = catatanList;
        this.context = context;
    }

    @NonNull
    @Override
    public CatatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_catatan, parent, false);

       return new AdapterCatatan.CatatanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCatatan.CatatanViewHolder holder, int position) {

        holder.tvValueJudul.setText(catatanList.get(position).judul);
        //holder.tvValueIsi.setText(catatanList.get(position).isi);

    }

    @Override
    public int getItemCount() {
        return catatanList.size();
    }

    public class CatatanViewHolder extends RecyclerView.ViewHolder {

        TextView tvValueJudul, tvValueIsi;

        public CatatanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValueJudul = itemView.findViewById(R.id.tvValueJudul);
            //tvValueIsi = itemView.findViewById(R.id.tvValueIsi);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButterKnife.bind(this, itemView);
                    if (dialog != null) {
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
