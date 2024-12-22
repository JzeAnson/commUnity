package com.example.community;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodListingFragment extends Fragment {

    private EditText searchBar;
    private ImageButton clearButton, searchButton;
    private RecyclerView recyclerView;
    private List<FoodItem> foodList, filteredList;
    private DatabaseReference databaseRef;
    private FoodAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FoodListingFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_food_listing, container, false);

        // Initialize UI components
        searchBar = view.findViewById(R.id.searchBar);
        clearButton = view.findViewById(R.id.clearButton);
        searchButton = view.findViewById(R.id.searchButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        foodList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance("https://community-1f007-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("foodItems");

        // Fetch food data from Firebase
        fetchFoodData();

        // Set up search functionality
        setupSearchBar();

        return view;
    }

    private void setupSearchBar() {
        // Add text watcher to search bar for real-time filtering
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoodList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear search bar and reset the list when X button is clicked
        clearButton.setOnClickListener(v -> {
            searchBar.setText(""); // Clear the search bar
            filterFoodList("");   // Reset the food list to show all items
        });

        // Perform search when Search button is clicked
        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            filterFoodList(query); // Filter the list based on the query
            Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
        });
    }

    private void filterFoodList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(foodList); // Show all items when query is empty
        } else {
            for (FoodItem item : foodList) {
                if (item.getFoodName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getFoodDesc().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchFoodData() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear(); // Clear the list before adding new data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
                    if (foodItem != null) {
                        foodList.add(foodItem);
                    }
                }
                filteredList.clear();
                filteredList.addAll(foodList); // Initially show all items
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read data: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}