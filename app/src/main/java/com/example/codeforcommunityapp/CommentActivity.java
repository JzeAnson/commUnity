package com.example.codeforcommunityapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText addComment;
    private Button postButton;
    private FirebaseFirestore firestore;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get postId from intent
        String postId = getIntent().getStringExtra("postId");
        if (postId == null || postId.isEmpty()) {
            Log.e(TAG, "Post ID is null or empty in CommentActivity");
            Toast.makeText(this, "Error: Invalid Post ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if postId is invalid
            return;
        }

        Log.d(TAG, "Received postId: " + postId);


        // Initialize UI elements
        commentRecyclerView = findViewById(R.id.recycler_view);
        addComment = findViewById(R.id.add_comment);
        postButton = findViewById(R.id.commentPostButton);

        // Setup RecyclerView
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // Fetch comments for the post
        loadComments(postId);

        // Handle comment posting
        postButton.setOnClickListener(v -> {
            String newCommentText = addComment.getText().toString().trim();
            String username = "Anonymous"; // Replace with the actual username logic if available

            if (newCommentText.isEmpty()) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                addComment(postId, username, newCommentText);
            }
        });
    }

    private void loadComments(String postId) {
        CollectionReference commentsRef = firestore.collection("post")
                .document(postId)
                .collection("comments");

        commentsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            commentList.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                String username = doc.getString("userName");
                                String commentText = doc.getString("comment");
                                if (username != null && commentText != null) {
                                    commentList.add(new Comment(username, commentText));
                                }
                            }
                            commentAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "No comments found for post ID: " + postId);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error fetching comments: ", e);
                        Toast.makeText(CommentActivity.this, "Error loading comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addComment(String postId, String username, String commentText) {
        CollectionReference commentsRef = firestore.collection("post")
                .document(postId)
                .collection("comments");

        // Create a new comment map
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("userName", username);
        newComment.put("comment", commentText);

        commentsRef.add(newComment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CommentActivity.this, "Comment added successfully!", Toast.LENGTH_SHORT).show();
                    addComment.setText(""); // Clear the input field
                    loadComments(postId); // Refresh the comments

                    incrementCommentCount(postId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding comment: ", e);
                    Toast.makeText(CommentActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                });
    }

    private void incrementCommentCount(String postId) {
        firestore.collection("post")
                .document(postId)
                .update("commentsCount", com.google.firebase.firestore.FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "commentsCount incremented successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error incrementing commentsCount: ", e));
    }
}