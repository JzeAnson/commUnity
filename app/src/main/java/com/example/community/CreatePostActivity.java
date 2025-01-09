package com.example.community;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText titleInput, descriptionInput;
    private RadioGroup categoryRadioGroup;
    private String userDocumentID, userName = "Anonymous"; // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve stored user document ID
        userDocumentID = userDocument.getInstance().getDocumentId();

        // Fetch the username from Firestore
        fetchUserNameFromFirestore();

        // Initialize UI elements
        titleInput = findViewById(R.id.titleinput);
        descriptionInput = findViewById(R.id.descriptioninput);
        categoryRadioGroup = findViewById(R.id.categoryRadioGroup);
        Button submitButton = findViewById(R.id.submit_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            // Get selected category
            int selectedId = categoryRadioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(CreatePostActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedCategory = selectedRadioButton.getText().toString();

            // Validate inputs
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
                if (title.isEmpty()) {
                    titleInput.setError("Enter Title");
                    titleInput.requestFocus();
                }
                if (description.isEmpty()) {
                    descriptionInput.setError("Enter Description");
                    descriptionInput.requestFocus();
                }
                return;
            }

            // Create a map for the post data
            Map<String, Object> createPost = new HashMap<>();
            createPost.put("title", title);
            createPost.put("description", description);
            createPost.put("selectedCategory", selectedCategory);
            createPost.put("userName", userName); // Correctly set the username
            createPost.put("userDocumentID", userDocumentID); // Store userDocumentID for reference
            createPost.put("timestamp", Timestamp.now());
            createPost.put("commentsCount", 0);
            createPost.put("likesCount", 0);
            createPost.put("likedBy", new HashMap<>());

            // Save to Firestore
            firestore.collection("post")
                    .add(createPost)
                    .addOnSuccessListener(documentReference -> {
                        String postId = documentReference.getId();
                        Log.d("CreatePostActivity", "Post ID: " + postId);
                        Toast.makeText(getApplicationContext(), "Post successfully created", Toast.LENGTH_SHORT).show();

                        // Navigate to CommentActivity after the post is created
                        Intent intent = new Intent(CreatePostActivity.this, CommentFragment.class);
                        intent.putExtra("postId", postId);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to create post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle cancel button click
        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // Function to fetch the user's username from Firestore
    private void fetchUserNameFromFirestore() {
        if (userDocumentID == null || userDocumentID.isEmpty()) {
            Log.e("CreatePostActivity", "User Document ID is null or empty");
            return;
        }

        firestore.collection("users").document(userDocumentID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("userName");
                        Log.d("CreatePostActivity", "Fetched userName: " + userName);
                    } else {
                        Log.e("CreatePostActivity", "User document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("CreatePostActivity", "Error fetching userName", e));
    }
}