package com.example.fussionv3.Convert;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fussionv3.Adapter.AdapterTopicUser;
import com.example.fussionv3.Model.ModelTopic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

// application class runs before your launcher activity
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // created a static method to convert timestamp to proper date format, so we can use it everywhere in project,
    // no need to rewrite again
    public static final String formatTimestamp(String timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        // format timestamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }

    public static void deleteTopic(Context context, String topicId, String topicTitle) {
        String TAG = "DELETE_TOPIC_TAG";

        Log.d(TAG, "deleteTopic: Deleting...");
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Silahkan tunggu...");
        progressDialog.setMessage("Deleting "+topicTitle+" ..."); // e.g. Deleting Topic ABC ...
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Topics");
        reference.child(topicId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Deleted from db too");
                        progressDialog.dismiss();
                        Toast.makeText(context, "Topik berhasil dihapus...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from db due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static void loadCategory(String categoryId, TextView categoryTv) {

        // get category using categoryId

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get category
                        String category = ""+snapshot.child("category").getValue();

                        // set to category textview
                        categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void incrementTopicViewCount(String topicId) {
        // 1) Get topic views count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Topics");
        ref.child(topicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get views count
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        // in case of null replace with 0
                        if (viewsCount.equals("") || viewsCount.equals("null")) {
                            viewsCount = "0";
                        }

                        // 2) Increment views count
                        long newViewsCount = Long.parseLong(viewsCount) + 1;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount", newViewsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Topics");
                        reference.child(topicId)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
