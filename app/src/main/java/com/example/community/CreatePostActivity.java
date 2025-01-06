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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText titleInput, descriptionInput;
    private RadioGroup categoryRadioGroup;
    private String postId, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "Anonymous"); // Default to "Anonymous" if username is not found

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
            createPost.put("userName", userName); // Use the username from SharedPreferences
            createPost.put("timestamp", Timestamp.now());
            createPost.put("commentsCount", 0); // Initialize with 0 comments
            createPost.put("likesCount", 0);    // Initialize with 0 likes
            createPost.put("likedBy", new HashMap<>()); // Empty map for likedBy

            // Save to Firestore
            firestore.collection("post")
                    .add(createPost)
                    .addOnSuccessListener(documentReference -> {
                        postId = documentReference.getId();
                        Log.d("Testing", "Post ID: " + postId);
                        Toast.makeText(getApplicationContext(), "Post successfully created", Toast.LENGTH_SHORT).show();

                        // Navigate to CommentActivity after the post is created
                        Intent intent = new Intent(CreatePostActivity.this, CommentFragment.class);
                        intent.putExtra("postId", postId); // Pass the postId to the next activity
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
}
