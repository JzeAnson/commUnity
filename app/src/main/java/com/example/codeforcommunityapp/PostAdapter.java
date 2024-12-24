package com.example.codeforcommunityapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "PostAdapter";
    private FirebaseFirestore firestore;
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
        holder.setupCommentListener(post); // Add real-time listener for comments count

        //like implementation
        // Like button click listener
        holder.likeIcon.setOnClickListener(v -> {
            // Reference to the specific post in Firestore
            DocumentReference postRef = FirebaseFirestore.getInstance()
                    .collection("post")
                    .document(post.getId()); // Ensure postId is part of your Post model

            // Increment the likesCount field in Firestore
            postRef.update("likesCount", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> {
                        // Optionally update the UI, e.g., change the icon
                        holder.likeIcon.setImageResource(R.drawable.likedicon);

                        // Fetch updated likesCount and set it in TextView
                        postRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                int likesCount = documentSnapshot.getLong("likesCount").intValue(); // Retrieve likesCount
                                holder.likesCount.setText(String.valueOf(likesCount)); // Update TextView
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("FirestoreError", "Error fetching updated likesCount", e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure (optional)
                        Log.e("Firestore", "Error updating likesCount", e);
                    });
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public void onViewRecycled(@NonNull PostViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.commentListener != null) {
            holder.commentListener.remove(); // Detach listener for comment count
        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, timestamp, userName, category, commentsCount, likesCount;
        ImageView commentIcon, likeIcon;
        ListenerRegistration commentListener;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            title = itemView.findViewById(R.id.postTitle);
            description = itemView.findViewById(R.id.postDescription);
            timestamp = itemView.findViewById(R.id.postTimestamp);
            userName = itemView.findViewById(R.id.usernamePosts);
            commentsCount = itemView.findViewById(R.id.commentsCount);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            category = itemView.findViewById(R.id.postCategory);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            likesCount = itemView.findViewById(R.id.likesCount);

            // Handle comment icon click
            commentIcon.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent commentIntent = new Intent(context, CommentActivity.class);

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Post clickedPost = postList.get(position);
                    String postId = clickedPost.getId();
                    if (postId != null && !postId.isEmpty()) {
                        commentIntent.putExtra("postId", postId);
                        context.startActivity(commentIntent);
                    } else {
                        Log.e(TAG, "Post ID is null or empty for the clicked post.");
                        Toast.makeText(context, "Error: Invalid Post ID", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void bind(Post post) {
            // Bind data to views with proper null checks
            title.setText(post.getTitle() != null ? post.getTitle() : "Untitled");
            description.setText(post.getDescription() != null ? post.getDescription() : "No Description");
            userName.setText(post.getUserName() != null ? post.getUserName() : "Anonymous");

            // Format timestamp
            if (post.getTimestamp() != null) {
                Date date = post.getTimestamp().toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                timestamp.setText(sdf.format(date));
            } else {
                timestamp.setText("Unknown Time");
            }

            // Set comments count
            Integer comments = post.getCommentsCount();
            commentsCount.setText(comments != null ? String.valueOf(post.getCommentsCount()) : "0");

            category.setText(post.getSelectedCategory() != null ? post.getSelectedCategory() : "Uncategorized");

            // Retrieve likesCount from Firestore
            if (post.getId() == null || post.getId().isEmpty()) {
                Log.e(TAG, "Post ID is null or empty for post: " + post.getTitle());
                return;
            }

            firestore.collection("post").document(post.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long likesCountValue = documentSnapshot.getLong("likesCount");
                            if (likesCountValue != null) {
                                likesCount.setText(String.valueOf(likesCountValue)); // Update the TextView
                            } else {
                                likesCount.setText("0"); // Default to 0 if likesCount is null
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error retrieving likesCount: " + e.getMessage());
                    });


            // Tag the view with the post for retrieval
            itemView.setTag(post);
        }

        public void setupCommentListener(Post post) {
            // Real-time listener for comments count
            commentListener = firestore.collection("post")
                    .document(post.getId())
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null) {
                            Log.e(TAG, "Error fetching post data: " + error.getMessage());
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Long updatedCommentCount = snapshot.getLong("commentsCount");
                            commentsCount.setText(updatedCommentCount != null ? updatedCommentCount.toString() : "0");

                            //update like count
                            Long updatedLikesCount = snapshot.getLong("likesCount");
                            likesCount.setText(updatedLikesCount != null ? updatedLikesCount.toString() : "0");
                        }
                    });
        }
    }
}
