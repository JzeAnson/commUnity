package com.example.codeforcommunityapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

public class Comment {
    private String userName;
    private String comment;

    public Comment() {
        // Default constructor for Firestore
    }

    public Comment(String userName, String comment) {
        this.userName = userName;
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }
}