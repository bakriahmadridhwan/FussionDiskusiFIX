package com.example.fussionv3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fussionv3.databinding.ActivityMainBinding;

import soup.neumorphism.NeumorphButton;

public class MainActivity extends AppCompatActivity {

    // private ActivityMainBinding binding;

    NeumorphButton neumorphButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

//        binding.btnSoup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "AMAN!!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}