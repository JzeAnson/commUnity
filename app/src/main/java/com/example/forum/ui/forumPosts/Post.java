package com.example.forum.ui.forumPosts;

public class Post {
    private String username;
    private String title;
    private String description;
    private String timestamp;
    private int likes;
    private int comments;

    public Post(String username, String title, String description, String timestamp, int likes, int comments) {
        this.username = username;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = comments;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }
}
