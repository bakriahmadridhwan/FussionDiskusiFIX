package com.example.fussionv3.Search;

import android.widget.Filter;

import com.example.fussionv3.Adapter.AdapterCategory;
import com.example.fussionv3.Adapter.AdapterTopicUser;
import com.example.fussionv3.Model.ModelCategory;
import com.example.fussionv3.Model.ModelTopic;

import java.util.ArrayList;

public class FilterTopicUser extends Filter {

    // arraylist in which we want to search
    ArrayList<ModelTopic> filterList;
    // adapter in which filter need to be implemented
    AdapterTopicUser adapterTopicUser;

    // constructor
    public FilterTopicUser(ArrayList<ModelTopic> filterList, AdapterTopicUser adapterTopicUser) {
        this.filterList = filterList;
        this.adapterTopicUser = adapterTopicUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // value should not be null and empty
        if (constraint != null && constraint.length() > 0) {

            // change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelTopic> filteredModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                // validate
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    // add to filtered list
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        // apply filter changes
        adapterTopicUser.topicArrayList = (ArrayList<ModelTopic>)results.values;

        // notify changes
        adapterTopicUser.notifyDataSetChanged();

    }
}
