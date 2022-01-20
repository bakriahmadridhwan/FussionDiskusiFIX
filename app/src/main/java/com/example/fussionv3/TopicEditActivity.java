package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fussionv3.databinding.ActivityTopicEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TopicEditActivity extends AppCompatActivity {

    private ActivityTopicEditBinding binding;

    // topic id get from inten started from AdapterTopicUser
    private String topicId;

    // progressdialog
    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    private static final String TAG = "TOPIC_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopicEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // topic id from intent started from AdapterTopicUser
        topicId = getIntent().getStringExtra("topicId");

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Silahkan tunggu...");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadTopicInfo();

        // handle click, pick category
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });

        // handle click, go to previous screen
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // handle click begin upload / update
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

    }


    private void loadTopicInfo() {

        Log.d(TAG, "loadTopicInfo: Loading topic info");

        DatabaseReference refTopics = FirebaseDatabase.getInstance().getReference("Topics");
        refTopics.child(topicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get topic info
                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String title = ""+snapshot.child("title").getValue();

                        // set to views
                        binding.titleEt.setText(title);
                        binding.descriptionEt.setText(description);

                        Log.d(TAG, "onDataChange: Loading Topic Category Info");
                        DatabaseReference refTopicCategory = FirebaseDatabase.getInstance().getReference("Categories");
                        refTopicCategory.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // get category
                                        String category = ""+snapshot.child("category").getValue();
                                        // set to category text view
                                        binding.categoryTv.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private String title="", description="";
    private void validateData() {
        // get data
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        
        // validate data
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Masukkan nama topik...", Toast.LENGTH_SHORT).show();
        } 
        else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Masukkan deskripsi", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(this, "Pilih kategori", Toast.LENGTH_SHORT).show();
        }
        else {
            updateTopic();
        }
    }

    private void updateTopic() {
        Log.d(TAG, "updateTopic: Starting updating topic info to db...");

        // show progress
        progressDialog.setMessage("Mengubah data topik...");
        progressDialog.show();

        // setup data to update to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("categoryId", ""+selectedCategoryId);

        // start updating
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
        ref.child(topicId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Topic updated...");
                        progressDialog.dismiss();
                        Toast.makeText(TopicEditActivity.this, "Data topik berhasil diubah...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TopicEditActivity.this, TopicListUserActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to update due to..."+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(TopicEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String selectedCategoryId = "", selectedCategoryTitle = "";

    private void categoryDialog() {
        // make string array from arraylist of string
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i=0; i<categoryTitleArrayList.size(); i++) {
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }


        // Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Kategori")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategoryId = categoryIdArrayList.get(which);
                        selectedCategoryTitle = categoryTitleArrayList.get(which);

                        // set selected category to textview
                        binding.categoryTv.setText(selectedCategoryTitle);
                    }
                })
                .show();
    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories: Loading categories...");

        categoryIdArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArrayList.clear();
                categoryTitleArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String id = ""+ds.child("id").getValue();
                    String category = ""+ds.child("category").getValue();
                    categoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);

                    Log.d(TAG, "onDataChange: ID: "+id);
                    Log.d(TAG, "onDataChange: Category"+category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}