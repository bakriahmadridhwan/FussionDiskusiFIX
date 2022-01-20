package com.example.fussionv3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fussionv3.Adapter.AdapterCatatan;
import com.example.fussionv3.roomdatabase.AppDatabase;
import com.example.fussionv3.roomdatabase.entitas.Catatan;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CatatanListActivity extends AppCompatActivity {

    RecyclerView rvCatatan;
    Button btnTambah;
    ImageButton backBtn;
    AppDatabase database;
    AdapterCatatan adapterCatatan;

    private List<Catatan> list;
    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catatan_list);

        rvCatatan = findViewById(R.id.rvCatatan);
        backBtn = findViewById(R.id.backBtn);
        //btnTambah = findViewById(R.id.btnTambah);
        database = AppDatabase.getInstance(getApplicationContext());

        list = new ArrayList<>();
        list.clear();
        list.addAll(database.catatanDao().getAll());
        //adapterCatatan = new AdapterCatatan(database.catatanDao().getAll(), getApplicationContext());
        adapterCatatan = new AdapterCatatan(list, getApplicationContext());

        adapterCatatan.setDialog(new AdapterCatatan.Dialog() {
            @Override
            public void onClick(int position) {
                final CharSequence[] dialogItem = {"Ubah", "Hapus",};
                dialog = new AlertDialog.Builder(CatatanListActivity.this)
                        .setIcon(R.drawable.ic_baseline_note_yb)
                        .setTitle("Pilihan");
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(CatatanListActivity.this, CatatanAddActivity.class);
                                intent.putExtra("id", list.get(position).id);
                                startActivity(intent);
                                break;
                            case 1:
                                Catatan catatan = list.get(position);
                                database.catatanDao().delete(catatan);
                                Toast.makeText(CatatanListActivity.this, "Data Catatan berhasil dihapus", Toast.LENGTH_SHORT).show();
                                onStart();
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });


        rvCatatan.setHasFixedSize(true);
        rvCatatan.setLayoutManager(new LinearLayoutManager(this));
        rvCatatan.setAdapter(adapterCatatan);

        //fabAdd = findViewById(R.id.fabAdd);
        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fabAddCatatan);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CatatanListActivity.this, CatatanAddActivity.class);
                startActivity(i);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        list.addAll(database.catatanDao().getAll());
        adapterCatatan.notifyDataSetChanged();
    }
}