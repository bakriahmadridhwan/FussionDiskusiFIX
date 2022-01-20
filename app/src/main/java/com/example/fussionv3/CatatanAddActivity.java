package com.example.fussionv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fussionv3.databinding.ActivityCatatanAddBinding;
import com.example.fussionv3.roomdatabase.AppDatabase;
import com.example.fussionv3.roomdatabase.entitas.Catatan;

public class CatatanAddActivity extends AppCompatActivity {

    EditText judulEt, isiEt;
    TextView titleTv, tambahCatatanTv;
    ImageButton backBtn;
    Button submitCatatanBtn;
    AppDatabase database;

    int id = 0;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catatan_add);

        titleTv = findViewById(R.id.titleTv);
        tambahCatatanTv = findViewById(R.id.tambahCatatanTv);
        judulEt = findViewById(R.id.judulEt);
        isiEt = findViewById(R.id.isiEt);
        backBtn = findViewById(R.id.backBtn);
        submitCatatanBtn = findViewById(R.id.submitCatatanBtn);

        database = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        if (id > 0) {
            isEdit = true;
            titleTv.setText("Mengubah Catatan");
            tambahCatatanTv.setText("UBAH CATATAN");

            Catatan catatan = database.catatanDao().get(id);
            judulEt.setText(catatan.judul);
            isiEt.setText(catatan.isi);
        } else {
            isEdit = false;

            titleTv.setText("Tambah Catatan Baru");
            tambahCatatanTv.setText("CATATAN BARU");
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submitCatatanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Catatan catatan = new Catatan();
                catatan.judul = judulEt.getText().toString();
                catatan.isi = isiEt.getText().toString();

                String judulCatatan = judulEt.getText().toString().trim();
                String isiCatatan = isiEt.getText().toString().trim();

                if (isEdit) {
                    database.catatanDao().update(id, catatan.judul, catatan.isi);
                    Toast.makeText(CatatanAddActivity.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (judulCatatan.equals("") || isiCatatan.equals("")) {
                        Toast.makeText(CatatanAddActivity.this, "data harus diisi...", Toast.LENGTH_SHORT).show();
                    } else {
                        database.catatanDao().insertAll(catatan);
                        Toast.makeText(CatatanAddActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }

            }
        });
    }
}