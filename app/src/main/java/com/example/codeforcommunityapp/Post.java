package com.example.codeforcommunityapp;

import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

@IgnoreExtraProperties
public class Post {
    private String title;
    private String postID;
    private String selectedCategory;
    private String description;
    private Timestamp timestamp; // Updated field name to match Java conventions
    private String userName;
    private int likesCount;
    private int commentsCount;

    // Default constructor (required for Firestore)
    public Post() {}

    // Getters and setters
    public String getTitle() {
        return title != null ? title : "Untitled";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description != null ? description : "No Description";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("timestamp") // Ensure this matches Firestore's field name
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @PropertyName("timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName != null ? userName : "Anonymous";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }


    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getId() {
        return postID;
    }

    public void setId(String postID) {
        this.postID = postID;
    }
}