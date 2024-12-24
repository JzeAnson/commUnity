package com.example.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.stream.Collectors;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private EditText searchBar;
    private Button activitiesButton, academicButton, emergencyButton;
    private RadioGroup sortRadioGroup;
    private RadioButton radioLatest, radioPopular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_activity);

        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized: " + (firestore != null));

        // Initialize RecyclerView
        postAdapter = new PostAdapter(postList);
        postRecyclerView = findViewById(R.id.recyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setAdapter(postAdapter);

        fetchPosts();  // Fetch posts initially

        Button createPostButton = findViewById(R.id.createPostButton);
        createPostButton.setOnClickListener(v -> {
            Intent intent = new Intent(PostActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    fetchPosts();  // Reload all posts if search bar is empty
                } else {
                    searchPosts(s.toString().toLowerCase());  // Filter posts based on the search query
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        activitiesButton = findViewById(R.id.activities);
        academicButton = findViewById(R.id.academic);
        emergencyButton = findViewById(R.id.emergency);

        activitiesButton.setOnClickListener(v -> filterPostsByCategory("Activities"));
        academicButton.setOnClickListener(v -> filterPostsByCategory("Academic"));
        emergencyButton.setOnClickListener(v -> filterPostsByCategory("Emergency"));

        // Initialize views
        sortRadioGroup = findViewById(R.id.sortRadioGroup);
        radioLatest = findViewById(R.id.radioLatest);
        radioPopular = findViewById(R.id.radioPopular);

        // Set listener for RadioGroup
        sortRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLatest) {
                sortPostsBy("latest");
            } else if (checkedId == R.id.radioPopular) {
                sortPostsBy("popular");
            }
        });
    }

    // Method to fetch posts from Firestore
    private void fetchPosts() {
        firestore.collection("post")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        postList.clear(); // Clear old data
                        for (var doc : queryDocumentSnapshots.getDocuments()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                post.setId(doc.getId()); // Set Firestore document ID as postID
                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();  // Notify adapter to update views
                        Log.d(TAG, "Posts fetched successfully: " + postList.size());
                    } else {
                        Log.d(TAG, "No posts found in the collection.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching posts: ", e));
    }

    // Method to search posts based on the search query
    private void searchPosts(String query) {
        List<Post> filteredPosts = postList.stream()
                .filter(post -> post.getTitle() != null && post.getTitle().toLowerCase().contains(query))
                .collect(Collectors.toList());

        postAdapter = new PostAdapter(filteredPosts); // Create a new adapter with filtered data
        postRecyclerView.setAdapter(postAdapter); // Set the new adapter to RecyclerView
        postAdapter.notifyDataSetChanged(); // Notify adapter of dataset changes
        Log.d(TAG, "Filtered posts count: " + filteredPosts.size());
    }

    // Method to filter posts by category
    private void filterPostsByCategory(String category) {
        firestore.collection("post")
                .whereEqualTo("selectedCategory", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        postList.clear(); // Clear old data
                        postList.addAll(queryDocumentSnapshots.toObjects(Post.class));
                        postAdapter.notifyDataSetChanged();  // Notify adapter to update views
                        Log.d(TAG, "Filtered posts by category: " + category);
                    } else {
                        Toast.makeText(this, "No posts found in " + category, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No posts found in category: " + category);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error filtering posts by category: ", e));
    }

    // Method to sort posts based on selected criteria (latest/popular)
    private void sortPostsBy(String sortOption) {
        if (sortOption.equals("latest")) {
            // Sort posts by timestamp (latest first)
            Collections.sort(postList, (p1, p2) -> Long.compare(
                    p2.getTimestamp().getSeconds(),
                    p1.getTimestamp().getSeconds()
            ));
            postAdapter.notifyDataSetChanged();
            Log.d(TAG, "Posts sorted by latest.");
        } else if (sortOption.equals("popular")) {
            // Sort posts by likesCount (most popular first)
            Collections.sort(postList, (p1, p2) -> Integer.compare(
                    p2.getLikesCount(),
                    p1.getLikesCount()
            ));
            postAdapter.notifyDataSetChanged();
            Log.d(TAG, "Posts sorted by popularity.");
        }
    }

    // Method to add a new post
    public void addPost(Post post) {
        firestore.collection("post")
                .add(post)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Post added with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error adding post", e));
    }

    // Method to test fetching a specific post
    private void testPosts() {
        firestore.collection("post")
                .document("wptnGpFmjLZhFyi7R2gg")
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Log.d(TAG, "Document Data: " + document.getData());
                        Post post = document.toObject(Post.class);
                        if (post != null) {
                            Log.d(TAG, "Post Object: " + post.toString());
                        }
                    } else {
                        Log.d(TAG, "No such document.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching document: ", e));
    }
}
