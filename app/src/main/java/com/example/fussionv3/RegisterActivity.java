package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.fussionv3.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // view binding
    private ActivityRegisterBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Silahkan tunggu...");
        progressDialog.setCanceledOnTouchOutside(false);

        // handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // handle click, begin register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private String name = "", email = "", password = "";

    private void validateData() {

        // validate
        // getdata
        name = binding.namaEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        String cPassword = binding.cPasswordEt.getText().toString().trim();
        
        // validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Masukkan nama...!", Toast.LENGTH_SHORT).show();
        } 
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Masukkan email...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Masukkan password...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(this, "Konfirmasi password...!", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(cPassword)) {
            Toast.makeText(this, "Password tidak sama...!", Toast.LENGTH_SHORT).show();
        }
        else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        // show progress
        progressDialog.setMessage("Membuat akun...");
        progressDialog.show();

        // create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // account creation success, now add in firebase realtime database
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // account creation failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Menyimpan info pengguna...");

        // timestamp
        long timestamp = System.currentTimeMillis();

        // get current user uid
        String uid = firebaseAuth.getUid();

        // setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", ""); // later
        hashMap.put("userType", "user"); // user or admin
        hashMap.put("timestamp", timestamp);

        // set data to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Akun berhasil dibuat...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // data failed adding to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}