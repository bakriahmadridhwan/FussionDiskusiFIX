package com.example.fussionv3.Model;

public class ModelComment {

    // variables
    String id, topicId, timestamp, comment, uid;


    // constructor empty, required by firebase
    public ModelComment() {
    }


    // constructor with all params
    public ModelComment(String id, String topicId, String timestamp, String comment, String uid) {
        this.id = id;
        this.topicId = topicId;
        this.timestamp = timestamp;
        this.comment = comment;
        this.uid = uid;
    }


    // getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
