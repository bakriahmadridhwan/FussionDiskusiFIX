package com.example.fussionv3.Model;

public class ModelTopic {

    // variables
    String uid, id, title, description, categoryId;
    String timestamp;
    long viewsCount;

    // empty constructor, required for firebase
    public ModelTopic() {
    }

    // constructor with all params
    public ModelTopic(String uid, String id, String title, String description, String categoryId, String timestamp, long viewsCount) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
    }

    // Getter Setters

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }
}
