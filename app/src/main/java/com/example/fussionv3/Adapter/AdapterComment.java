package com.example.fussionv3.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fussionv3.Convert.MyApplication;
import com.example.fussionv3.Model.ModelComment;
import com.example.fussionv3.R;
import com.example.fussionv3.databinding.RowCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment>{

    // context
    private Context context;

    // arraylist to hold comment
    private ArrayList<ModelComment> commentArrayList;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // view binding
    private RowCommentBinding binding;

    // constructor
    public AdapterComment(Context context, ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate/bind the view xml
        binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        // get data spesific position of list, set data, handle click etc

        // get data
        ModelComment modelComment = commentArrayList.get(position);
        String id = modelComment.getId();
        String topicId = modelComment.getTopicId();
        String comment = modelComment.getComment();
        String uid = modelComment.getUid();
        String timestamp = modelComment.getTimestamp();

        // format date, already made function in MyApplication class
        String date = MyApplication.formatTimestamp(timestamp);


        // set data
        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        // we don't have user's name, profile picture, so we will load it using uid we stored in each comment
        loadUserDetails(modelComment, holder);

        // handle click, show option to delete comment
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1) user must be logged in
                // 2) uid in comment (to be deleted) must be same as uid of logged in user

                if (firebaseAuth.getCurrentUser() != null && uid.equals(firebaseAuth.getUid())) {
                    deleteComment(modelComment, holder);
                }
            }
        });

    }

    private void deleteComment(ModelComment modelComment, HolderComment holder) {
        // show confirm dialog before deleting comment
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hapus Komentar")
                .setIcon(R.drawable.ic_baseline_info_24)
                .setMessage("Apakah anda yakin ingin menghapus komentar ini..?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // delete from dialog clicked, begin delete

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
                        ref.child(modelComment.getTopicId())
                                .child("Comments")
                                .child(modelComment.getId()) // comment id
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Komentar terhapus...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Gagal menghapus komentar ini "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // cancel clicked
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void loadUserDetails(ModelComment modelComment, HolderComment holder) {
        String uid = modelComment.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get data
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        // set data
                        holder.nameTv.setText(name);

                        try {

                            // ada di manage profile

                        } catch (Exception e) {
                            holder.profileTv.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size(); // return records size, number of records
    }

    // view holder class for row_comment.xml
    class HolderComment extends RecyclerView.ViewHolder {

         // UI views of row_comment.xml
        ShapeableImageView profileTv;
        TextView nameTv, dateTv, commentTv;

        public HolderComment(@NonNull View itemView) {
            super(itemView);
            profileTv = binding.profileTv;
            nameTv = binding.nameTv;
            dateTv = binding.dateTv;
            commentTv = binding.commentTv;
        }
    }
}
