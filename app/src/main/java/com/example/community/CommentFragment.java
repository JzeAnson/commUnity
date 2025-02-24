package com.example.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentFragment extends Fragment {

    private static final String TAG = "CommentFragment";
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText addComment;
    private Button postButton;
    private FirebaseFirestore firestore;
    private String postId, userDocumentID, userName = "Anonymous"; // Default username

    public CommentFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve postId from arguments
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }

        if (postId == null || postId.isEmpty()) {
            Log.e(TAG, "Post ID is null or empty in CommentFragment");
            Toast.makeText(getContext(), "Error: Invalid Post ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Received postId: " + postId);

        // Initialize UI elements
        commentRecyclerView = view.findViewById(R.id.recyclerViewComment);
        addComment = view.findViewById(R.id.add_comment);
        postButton = view.findViewById(R.id.commentPostButton);

        if (commentRecyclerView == null) {
            Log.e(TAG, "RecyclerView not found in the fragment_comments layout");
            return;
        }

        // Setup RecyclerView
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // Fetch comments for the post
        loadComments(postId);

        // Fetch the user's document ID (Assuming it's stored in SharedPreferences or similar)
        userDocumentID = userDocument.getInstance().getDocumentId();
        fetchUserNameFromFirestore(userDocumentID);

        // Handle comment posting
        postButton.setOnClickListener(v -> {
            String newCommentText = addComment.getText().toString().trim();

            if (newCommentText.isEmpty()) {
                Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // Ensure the username is retrieved before posting the comment
                addComment(postId, userName, newCommentText);
            }
        });
    }

    private void fetchUserNameFromFirestore(String userDocumentID) {
        if (userDocumentID == null || userDocumentID.isEmpty()) {
            Log.e(TAG, "User Document ID is null or empty");
            return;
        }

        firestore.collection("users").document(userDocumentID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("userName");
                        Log.d(TAG, "Fetched userName: " + userName);
                    } else {
                        Log.e(TAG, "User document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching userName", e));
    }

    private void loadComments(String postId) {
        CollectionReference commentsRef = firestore.collection("post")
                .document(postId)
                .collection("comments");

        commentsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
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
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching comments: ", e);
                    Toast.makeText(getContext(), "Error loading comments", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Comment added successfully!", Toast.LENGTH_SHORT).show();
                    addComment.setText(""); // Clear the input field
                    loadComments(postId); // Refresh the comments
                    incrementCommentCount(postId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding comment: ", e);
                    Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
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