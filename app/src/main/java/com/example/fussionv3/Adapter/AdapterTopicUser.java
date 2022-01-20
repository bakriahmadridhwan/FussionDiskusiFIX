package com.example.fussionv3.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fussionv3.Convert.MyApplication;
import com.example.fussionv3.Model.ModelTopic;
import com.example.fussionv3.Search.FilterTopicUser;
import com.example.fussionv3.TopicDetailActivity;
import com.example.fussionv3.TopicEditActivity;
import com.example.fussionv3.databinding.RowTopicUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterTopicUser extends RecyclerView.Adapter<AdapterTopicUser.HolderTopicUser> implements Filterable {

    // context
    private Context context;
    // arraylist to hold list of data of type ModelTopic
    public ArrayList<ModelTopic> topicArrayList, filterList;

    // view binding row_topic_user.xml
    private RowTopicUserBinding binding;

    private FilterTopicUser filter;

    private static final String TAG = "TOPIC_ADAPTER_TAG";

    // progress
    private ProgressDialog progressDialog;

    // constructor
    public AdapterTopicUser(Context context, ArrayList<ModelTopic> topicArrayList) {
        this.context = context;
        this.topicArrayList = topicArrayList;
        this.filterList = topicArrayList;

        // init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Silahkan tunggu...");
        progressDialog.setCanceledOnTouchOutside(false);
    }


    @NonNull
    @Override
    public HolderTopicUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // bind layout using view binding
        binding = RowTopicUserBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderTopicUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTopicUser.HolderTopicUser holder, int position) {
        // get data, set data, handle click etc

        // get data
        ModelTopic model = topicArrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        String timestamp = model.getTimestamp();
        String categoryId = model.getCategoryId();
        String topicId = model.getId();


        // convert timestamp to dd/MM/yyyy format
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        // set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        // load further details like category
        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryTv
        );
        
        // handle click, show dialog with options 1) edit 2) delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });

        // handle click, open topic detail page, pass topid id to get details of it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TopicDetailActivity.class);
                intent.putExtra("topicId", topicId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionsDialog(ModelTopic model, HolderTopicUser holder) {
        String topicId = model.getId();
        String topicTitle = model.getTitle();

        // options to show in dialog
        String[] options = {"Edit", "Hapus"};

        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pilih Opsi")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle dialog option click
                        if (which == 0) {
                            // edit clicked, open new activity to edit the topic info
                            Intent intent = new Intent(context, TopicEditActivity.class);
                            intent.putExtra("topicId", topicId);
                            context.startActivity(intent);

                        } else if (which == 1) {
                            // delete clicked
                            MyApplication.deleteTopic(
                                    context,
                                    ""+topicId,
                                    ""+topicTitle
                            );
                            // deleteTopic(model, holder);
                        }
                    }
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return topicArrayList.size(); // return number of records | list size
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterTopicUser(filterList, this);
        }
        return filter;
    }


    // view holder class for row_topic_user.xml
    class HolderTopicUser extends RecyclerView.ViewHolder {

        // UI Views of row_topic_user.xml
        ProgressBar pbTopic;
        TextView titleTv, descriptionTv, categoryTv, dateTv;
        ImageButton moreBtn;

        public HolderTopicUser(@NonNull View itemView) {
            super(itemView);

            // init ui views
            pbTopic = binding.pbTopic;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            dateTv = binding.dateTv;
            moreBtn = binding.moreBtn;
        }
    }
}
