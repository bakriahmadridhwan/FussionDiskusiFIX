package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.fussionv3.Adapter.AdapterComment;
import com.example.fussionv3.Convert.MyApplication;
import com.example.fussionv3.Model.ModelComment;
import com.example.fussionv3.databinding.ActivityTopicDetailBinding;
import com.example.fussionv3.databinding.DialogCommentAddBinding;
import com.example.fussionv3.databinding.DialogInfoBinding;
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

public class TopicDetailActivity extends AppCompatActivity {

    // view binding
    private ActivityTopicDetailBinding binding;
    
    // topic id, get from intent
    String topicId, topicTitle;

    // firebase auth
    FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    // arraylist to hold comments
    private ArrayList<ModelComment> commentArrayList;
    // adapter comment, to set recycler view
    private AdapterComment adapterComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopicDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Silahkan tunggu...");
        progressDialog.setCanceledOnTouchOutside(false);
        
        // get data from intent e.g. topicId
        Intent intent = getIntent();
        topicId = intent.getStringExtra("topicId");

        firebaseAuth = FirebaseAuth.getInstance();
        
        loadTopicDetails();
        loadComments();

        // Increment topic view count, whenever this page starts
        MyApplication.incrementTopicViewCount(topicId);

        // handle click goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // handle click, show comment add dialog
        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Requirements: User must be logged in, to add comment
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(TopicDetailActivity.this, "Anda belum login...!", Toast.LENGTH_SHORT).show();
                }
                else {
                    addCommentDialog();
                }
            }
        });

        binding.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog();
            }
        });
    }

    private void infoDialog() {
        // inflate bind view for dialog
        DialogInfoBinding dialogInfoBinding = DialogInfoBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogInfoBinding.getRoot());

        // create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // handle click, dismiss dialog
        dialogInfoBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void loadComments() {
        // init arraylist before adding data into it
        commentArrayList = new ArrayList<>();

        // db path to load comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
        ref.child(topicId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // clear arraylist before start adding data into it
                        commentArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            // get data as model, spellings of variables in model must be as same as in firebase
                            ModelComment model = ds.getValue(ModelComment.class);
                            // add to arraylist
                            commentArrayList.add(model);
                        }
                        // setup adapter
                        adapterComment = new AdapterComment(TopicDetailActivity.this, commentArrayList);
                        // set adapter to recyclerview
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String comment = "";
    private void addCommentDialog() {
        // inflate bind view for dialog
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());

        // create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // handle click, dismiss dialog
        commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // handle click, add comment
        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get data
                comment = commentAddBinding.commentEt.getText().toString().trim();
                
                // validate data
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(TopicDetailActivity.this, "Masukkan komentar anda...", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });
    }

    private void addComment() {
        // show progress dialog
        progressDialog.setMessage("Menambahkan komentar...");
        progressDialog.show();

        // timestamp for comment id, comment item
        String timestamp = ""+System.currentTimeMillis();

        // setup data to add in db for comment
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("topicId", ""+topicId);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("comment", ""+comment);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        // DB path to add data into it
        // Topics > topicId > Comments > commentId > commentData
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
        ref.child(topicId).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(TopicDetailActivity.this, "Komentar telah ditambahkan...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed to add comment
                        progressDialog.dismiss();
                        Toast.makeText(TopicDetailActivity.this, "Gagal menambahkan komentar "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTopicDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
        ref.child(topicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get data
                        String title = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        // format date
                        String date = MyApplication.formatTimestamp(timestamp);

                        MyApplication.loadCategory(
                                ""+categoryId,
                                binding.categoryTv
                        );

                        // set data
                        binding.titleTv.setText(title);
                        binding.descriptionTv.setText(description);
                        binding.viewsTv.setText(viewsCount.replace("null", "N/A"));
                        binding.dateTv.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}