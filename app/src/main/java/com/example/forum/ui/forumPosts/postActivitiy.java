package com.example.forum.ui.forumPosts;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forum.R;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        loadDummyPosts();

        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
    }

    private void loadDummyPosts() {
        postList.add(new Post(
                "Shawn",
                "Shawn is eating a chicken",
                "Shawn had a hearty meal today and thoroughly enjoyed a delicious roast chicken.",
                "10 minutes ago",
                25,
                12
        ));

        postList.add(new Post(
                "Alice",
                "Turtle becomes a pig",
                "In an unexpected turn of events, a turtle was spotted behaving like a pig in the park.",
                "30 minutes ago",
                18,
                8
        ));

        postList.add(new Post(
                "John",
                "Why do cats nap so much?",
                "A deep dive into the curious habits of our feline friends and their love for naps.",
                "1 hour ago",
                40,
                15
        ));

        postList.add(new Post(
                "Emma",
                "Amazing sunset today!",
                "The sky was ablaze with colors this evening. A truly breathtaking sunset!",
                "2 hours ago",
                50,
                20
        ));

        postList.add(new Post(
                "Mike",
                "Breaking: Dogs learn to skateboard",
                "A group of dogs wowed the crowd today with their skateboarding skills downtown.",
                "3 hours ago",
                75,
                30
        ));
    }

}
