package com.example.fussionv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.fussionv3.databinding.ActivityMainScreenBinding;

public class MainScreenActivity extends AppCompatActivity {

    // view binding
    private ActivityMainScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainScreenActivity.this, LoginActivity.class));
            }
        });

//        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainScreenActivity.this, DashboardUserActivity.class));
//            }
//        });
    }
}