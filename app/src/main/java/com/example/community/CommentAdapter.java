package com.example.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Ensure that the comment text and username are not null
        String commentText = comment.getComment() != null ? comment.getComment() : "No comment";
        String userNameText = comment.getUserName() != null ? comment.getUserName() : "Anonymous";

        holder.description.setText(commentText);
        holder.userName.setText(userNameText);
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView description, userName;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.comment_text);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
