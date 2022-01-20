package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fussionv3.Model.ModelCategory;
import com.example.fussionv3.databinding.ActivityTopicAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TopicAddActivity extends AppCompatActivity {

    private ActivityTopicAddBinding binding;

    private FirebaseAuth firebaseAuth;

    // progress dialog
    private ProgressDialog progressDialog;

    // arraylist to hold topic categories
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    // TAG for debugging
    private static final String TAG = "ADD_TOPIC_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopicAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        loadTopicCategories();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Silahkan tunggu...");
        progressDialog.setCanceledOnTouchOutside(false);

        // handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // handle click, attach pdf
//        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(TopicAddActivity.this, "PDF", Toast.LENGTH_SHORT).show();
//            }
//        });

        // handle click, add topic / UPLOAD
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        // handle click, pick categories
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });
    }


    private String title = "", description = "";

    private void validateData() {
        // validate data
        Log.d(TAG, "validateData: validating data...");

        // get data
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();

        // validate data
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Masukkan nama topik...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Masukkan deskripsi...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Pilih kategori...", Toast.LENGTH_SHORT).show();
        }
        else {
            // all data is valid, can upload now
            uploadTopic();
        }
    }

    private void uploadTopic() {
        Log.d(TAG, "uploadTopic: uploading to firebase db...");

        // show progress
        progressDialog.setMessage("Menyimpan data...");
        progressDialog.show();

        // timestamp
        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        // setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("categoryId", ""+selectedCategoryId);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("viewsCount", 0);

        // db reference: DB > Topics
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded...");
                        Toast.makeText(TopicAddActivity.this, "Topik Berhasil ditambahkan...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TopicAddActivity.this, DashboardUserActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db  due to"+e.getMessage());
                        Toast.makeText(TopicAddActivity.this, "Failed to upload to db  due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTopicCategories() {
        Log.d(TAG, "loadTopicCategories: Loading topic categories...");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        // db reference to load categories... db > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear(); // clear before add data
                categoryIdArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){

                    // get id and title of category
                    String categoryId = ""+ds.child("id").getValue();
                    String categoryTitle = ""+ds.child("category").getValue();

                    // add to respective arraylists
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // selected category id and category title
    private String selectedCategoryId, selectedCategoryTitle;

    private void categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: showing category pick dialog");

        // get string array of categories from arraylist
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i < categoryTitleArrayList.size(); i++) {
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Kategori")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle item click
                        // get clicked item from list
                        selectedCategoryTitle = categoryTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);
                        // set to category textview
                        binding.categoryTv.setText(selectedCategoryTitle);

                        Log.d(TAG, "onClick: Selected Category: " + selectedCategoryId + " " + selectedCategoryTitle);
                    }
                })
                .show();
    }
}