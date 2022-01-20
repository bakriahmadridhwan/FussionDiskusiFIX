package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.fussionv3.databinding.ActivityCategoryAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {
    
    private ActivityCategoryAddBinding binding;
    
    private FirebaseAuth firebaseAuth;
    
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        firebaseAuth = FirebaseAuth.getInstance();
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Silahkan tunggu...");
        progressDialog.setCanceledOnTouchOutside(false);
        
        // handle click, begin upload category
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        // handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
    }

    private String category = "";

    private void validateData() {
        // validate date
        // get data

        category = binding.categoryEt.getText().toString().trim();
        // validate if not empty
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Masukkan nama kategori...!", Toast.LENGTH_SHORT).show();
        }
        else {
            addCategoryFirebase();
        }
    }

    private void addCategoryFirebase() {
        // show progress
        progressDialog.setMessage("Menambahkan kategori...");
        progressDialog.show();

        // get timestamp
        long timestamp = System.currentTimeMillis();

        // setup info to add in firebase db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("category", ""+category);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        // add to firebase db..... Database Root > Categories > categoryId > category info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Category add success
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, "Kategori berhasil ditambahkan...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CategoryAddActivity.this, DashboardUserActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // category add failed
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}