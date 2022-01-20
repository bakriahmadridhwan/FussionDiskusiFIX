package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.fussionv3.Adapter.AdapterTopicUser;
import com.example.fussionv3.Model.ModelTopic;
import com.example.fussionv3.databinding.ActivityTopicListUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TopicListUserActivity extends AppCompatActivity {

    // viewbinding
    private ActivityTopicListUserBinding binding;

    // arraylist to hold list of data of type ModelTopic
    private ArrayList<ModelTopic> topicArrayList;

    // adapter
    private AdapterTopicUser adapterTopicUser;
    
    private String categoryId, categoryTitle;

    private static final String TAG = "TOPIC_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopicListUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get data from intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        // set topic category
        binding.subTitleTv.setText(categoryTitle);

        loadTopicList();

        // search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // search as and when user type each letter
                try {
                    adapterTopicUser.getFilter().filter(s);
                }
                catch (Exception e) {
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadTopicList() {
        // init list before adding data
        topicArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        topicArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            // get data
                            ModelTopic model = ds.getValue(ModelTopic.class);
                            // add to list
                            topicArrayList.add(model);

                            Log.d(TAG, "onDataChange: "+model.getId() + " " + model.getTitle());
                        }
                        // setup adapter
                        adapterTopicUser = new AdapterTopicUser(TopicListUserActivity.this, topicArrayList);
                        binding.topicRv.setAdapter(adapterTopicUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}