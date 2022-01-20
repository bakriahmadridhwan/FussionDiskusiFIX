package com.example.fussionv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.fussionv3.Adapter.AdapterCategory;
import com.example.fussionv3.Model.ModelCategory;
import com.example.fussionv3.databinding.ActivityDashboardUserBinding;
import com.example.fussionv3.databinding.DialogInfoAppBinding;
import com.example.fussionv3.databinding.DialogPanduanBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {

    private ActivityDashboardUserBinding binding;
    private Context context;

    private FirebaseAuth firebaseAuth;

    // arraylist to store category
    private ArrayList<ModelCategory> categoryArrayList;

    // adapter
    private AdapterCategory adapterCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategories();

        // edit text change listener, search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                // called as and when user type each letter
                try {
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e) {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // handle click, logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // confirm logout dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardUserActivity.this);
                builder.setTitle("Keluar Akun")
                        .setIcon(R.drawable.ic_baseline_info_24)
                        .setMessage("Apakah anda yakin ingin keluar dari akun anda?")
                        .setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // begin logout
                                Toast.makeText(DashboardUserActivity.this, "Berhasil keluar...", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                checkUser();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        // handle click, start category add screen
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardUserActivity.this, CategoryAddActivity.class));
            }
        });

        // handle click, start topic add screen
        binding.addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardUserActivity.this, TopicAddActivity.class));
            }
        });

        // handle click, to info app
        binding.infoCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoAppDialog();
            }
        });

        // handle click, to catatan
        binding.addCatatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardUserActivity.this, CatatanListActivity.class));
            }
        });

        binding.penjelasanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penjelasanAppDialog();
            }
        });
    }

    private void penjelasanAppDialog() {
        // inflate bind view for dialog
        DialogPanduanBinding dialogPanduanBinding = DialogPanduanBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogPanduanBinding.getRoot());

        // create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // handle click, dismiss dialog
        dialogPanduanBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void infoAppDialog() {
        // inflate bind view for dialog
        DialogInfoAppBinding dialogInfoAppBinding = DialogInfoAppBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogInfoAppBinding.getRoot());

        // create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // handle click, dismis dialog
        dialogInfoAppBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void loadCategories() {
        // init arraylist
        categoryArrayList = new ArrayList<>();

        // get all categories from firebase > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear arraylist before adding data into it
                categoryArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    // add to arraylist
                    categoryArrayList.add(model);
                }
                // setup adapter
                adapterCategory = new AdapterCategory(DashboardUserActivity.this, categoryArrayList);
                // set adapter to recyclerview
                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            // not logged in, goto mainscreen
            startActivity(new Intent(this, MainScreenActivity.class));
            finish();
        }
        else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // logged in, get user info
            String email = firebaseUser.getEmail();
            String nama = user.getDisplayName();
            // set in textview of toolbar
            binding.subTitleTv.setText(email);
        }
    }
}