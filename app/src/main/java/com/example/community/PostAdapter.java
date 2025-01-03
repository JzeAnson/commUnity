package com.example.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
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
    ImageView btn_comment;

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

        // Like button click listener
        holder.likeIcon.setOnClickListener(v -> {
            DocumentReference postRef = firestore.collection("post").document(post.getId());
            postRef.update("likesCount", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> {
                        holder.likeIcon.setImageResource(R.drawable.likedicon);
                        postRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                int likesCount = documentSnapshot.getLong("likesCount").intValue();
                                holder.likesCount.setText(String.valueOf(likesCount));
                            }
                        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching updated likesCount", e));
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating likesCount", e));
        });

        // Comment icon click listener
        holder.commentIcon.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) holder.itemView.getContext();
            int position1 = holder.getAdapterPosition();
            if (position1 != RecyclerView.NO_POSITION) {
                Post clickedPost = postList.get(position1);
                String postId = clickedPost.getId();

                if (postId != null && !postId.isEmpty()) {
                    // Navigate to CommentFragment
                    CommentFragment fragment = new CommentFragment();
                    Bundle args = new Bundle();
                    args.putString("postId", postId);
                    fragment.setArguments(args);

                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, fragment)
                            .addToBackStack(null) // Allows back navigation
                            .commit();
                } else {
                    Log.e(TAG, "Post ID is null or empty for the clicked post.");
                    Toast.makeText(activity, "Error: Invalid Post ID", Toast.LENGTH_SHORT).show();
                }
            }
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
        }

        public void bind(Post post) {
            // Bind data to views
            title.setText(post.getTitle() != null ? post.getTitle() : "Untitled");
            description.setText(post.getDescription() != null ? post.getDescription() : "No Description");
            userName.setText(post.getUserName() != null ? post.getUserName() : "Anonymous");

            if (post.getTimestamp() != null) {
                Date date = post.getTimestamp().toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                timestamp.setText(sdf.format(date));
            } else {
                timestamp.setText("Unknown Time");
            }

            Integer comments = post.getCommentsCount();
            commentsCount.setText(comments != null ? String.valueOf(post.getCommentsCount()) : "0");
            category.setText(post.getSelectedCategory() != null ? post.getSelectedCategory() : "Uncategorized");

            firestore.collection("post").document(post.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long likesCountValue = documentSnapshot.getLong("likesCount");
                            likesCount.setText(likesCountValue != null ? String.valueOf(likesCountValue) : "0");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error retrieving likesCount: " + e.getMessage()));
        }

        public void setupCommentListener(Post post) {
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

                            Long updatedLikesCount = snapshot.getLong("likesCount");
                            likesCount.setText(updatedLikesCount != null ? updatedLikesCount.toString() : "0");
                        }
                    });
        }
    }
}
