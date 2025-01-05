package com.example.community;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PostFragment extends Fragment {

    private static final String TAG = "PostFragment";
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private EditText searchBar;
    private Button activitiesButton, academicButton, emergencyButton;
    private RadioGroup sortRadioGroup;
    private RadioButton radioLatest, radioPopular;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.forum_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized: " + (firestore != null));

        // Initialize RecyclerView
        postAdapter = new PostAdapter(postList);
        postRecyclerView = view.findViewById(R.id.recyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setAdapter(postAdapter);

        fetchPosts();  // Fetch posts initially

        Button createPostButton = view.findViewById(R.id.createPostButton);
        createPostButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });

        searchBar = view.findViewById(R.id.search_bar);
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

        activitiesButton = view.findViewById(R.id.activities);
        academicButton = view.findViewById(R.id.academic);
        emergencyButton = view.findViewById(R.id.emergency);

        activitiesButton.setOnClickListener(v -> filterPostsByCategory("Activities"));
        academicButton.setOnClickListener(v -> filterPostsByCategory("Academic"));
        emergencyButton.setOnClickListener(v -> filterPostsByCategory("Emergency"));

        // Initialize views
        sortRadioGroup = view.findViewById(R.id.sortRadioGroup);
        radioLatest = view.findViewById(R.id.radioLatest);
        radioPopular = view.findViewById(R.id.radioPopular);

        // Set listener for RadioGroup
        sortRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLatest) {
                sortPostsBy("latest");
            } else if (checkedId == R.id.radioPopular) {
                sortPostsBy("popular");
            }
        });
    }

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
                        Log.d("testting", firestore.collection("post")
                                .get().toString());
                        postAdapter.notifyDataSetChanged();  // Notify adapter to update views
                        Log.d(TAG, "Posts fetched successfully: " + postList.size());
                    } else {
                        Log.d(TAG, "No posts found in the collection.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching posts: ", e));
    }

    private void searchPosts(String query) {
        List<Post> filteredPosts = postList.stream()
                .filter(post -> post.getTitle() != null && post.getTitle().toLowerCase().contains(query))
                .collect(Collectors.toList());

        postAdapter = new PostAdapter(filteredPosts); // Create a new adapter with filtered data
        postRecyclerView.setAdapter(postAdapter); // Set the new adapter to RecyclerView
        postAdapter.notifyDataSetChanged(); // Notify adapter of dataset changes
        Log.d(TAG, "Filtered posts count: " + filteredPosts.size());
    }

    private void filterPostsByCategory(String category) {
        firestore.collection("post")
                .whereEqualTo("selectedCategory", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        postList.clear(); // Clear old data
                        for (var doc : queryDocumentSnapshots.getDocuments()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                post.setId(doc.getId());  // Set document ID to postID
                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();  // Notify adapter to update views
                        Log.d("postss", postList.toString());
                        Log.d(TAG, "Filtered posts by category: " + category);
                    } else {
                        Toast.makeText(getContext(), "No posts found in " + category, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No posts found in category: " + category);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error filtering posts by category: ", e);
                    Toast.makeText(getContext(), "Error filtering posts", Toast.LENGTH_SHORT).show();
                });
    }


    private void sortPostsBy(String sortOption) {
        if (sortOption.equals("latest")) {
            Collections.sort(postList, (p1, p2) -> Long.compare(
                    p2.getTimestamp().getSeconds(),
                    p1.getTimestamp().getSeconds()
            ));
            postAdapter.notifyDataSetChanged();
            Log.d(TAG, "Posts sorted by latest.");
        } else if (sortOption.equals("popular")) {
            Collections.sort(postList, (p1, p2) -> Integer.compare(
                    p2.getLikesCount(),
                    p1.getLikesCount()
            ));
            postAdapter.notifyDataSetChanged();
            Log.d(TAG, "Posts sorted by popularity.");
        }
    }
}
