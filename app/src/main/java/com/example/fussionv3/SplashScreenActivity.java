package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        final Handler handler = new Handler();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        }, 2000);
    }

    private void checkUser() {
        // get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // user not logged in
            // start mainscreen
            startActivity(new Intent(SplashScreenActivity.this, MainScreenActivity.class));
            finish();
        }
        else {
            // user logged in check user type, some as done in login screen
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // get user type
                            String userType = "" + snapshot.child("userType").getValue();

                            // check user type
                            if (userType.equals("user")) {
                                startActivity(new Intent(SplashScreenActivity.this, DashboardUserActivity.class));
                                finish();
                            } else if (userType.equals("admin")) {
                                startActivity(new Intent(SplashScreenActivity.this, DashboardAdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SplashScreenActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}