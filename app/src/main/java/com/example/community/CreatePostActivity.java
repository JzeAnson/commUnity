package com.example.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private EditText titleInput, descriptionInput;
    private RadioGroup categoryRadioGroup;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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

//            FirebaseUser currentUser = auth.getCurrentUser();
//            if (currentUser == null) {
//                Toast.makeText(CreatePostActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
//                return;
//            }

            // Retrieve userId and userName
//            String userId = currentUser.getUid();
//            String userName = currentUser.getDisplayName(); // Retrieve username from FirebaseUser
            String userId = "Shawn";
            String userName = "Shawn";
            if (userName == null || userName.isEmpty()) {
                userName = "Anonymous"; // Fallback if username is not set
            }

            // Create a map for the post data
            Map<String, Object> createPost = new HashMap<>();
            createPost.put("title", title);
            createPost.put("description", description);
            createPost.put("selectedCategory", selectedCategory);
            createPost.put("userName", userName);
            createPost.put("userId", userId); // Include userId in Firestore
            createPost.put("timestamp", Timestamp.now());
            createPost.put("commentsCount", 0); // Initialize with 0 comments
            createPost.put("likesCount", 0);    // Initialize with 0 likes
            createPost.put("likedBy", new HashMap<>()); // Empty map for likedBy

            // Save to Firestore
            // Save to Firestore
            firestore.collection("post")
                    .add(createPost)
                    .addOnSuccessListener(documentReference -> {
                        postId = documentReference.getId(); // Retrieve the document ID
                        Log.d("Testing", "Post ID: " + postId);
                        Toast.makeText(getApplicationContext(), "Post successfully created", Toast.LENGTH_SHORT).show();

                        // Navigate to CommentActivity after the post is created
                        Intent intent = new Intent(CreatePostActivity.this, CommentActivity.class);
                        intent.putExtra("postId", postId); // Pass the postId to the next activity
                        startActivity(intent); // Start the activity
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
}