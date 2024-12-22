package com.example.community;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;
import android.widget.EditText;
import android.widget.ImageButton;

public class FoodListingFragment extends Fragment {
    private EditText searchBar;
    private ImageButton clearButton;
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

        // Initialize RecyclerView and set LayoutManager
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize the food list and adapter
        foodList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), foodList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance("https://community-1f007-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("foodItems");
        Log.d("FoodListingFragment", "Database Reference initialized: " + databaseRef);
        // Fetch data from Firebase
        fetchFoodData();

        return view;
    }

    private void setupSearchBar() {
        // Show/Hide clear button based on search bar content
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 0) {
//                    clearButton.setVisibility(View.VISIBLE);
//                } else {
//                    clearButton.setVisibility(View.GONE);
//                }
                filterFoodList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear search bar and reset the list when clear button is clicked
        clearButton.setOnClickListener(v -> {
            searchBar.setText(""); // Clear the search bar
            filterFoodList("");   // Reset the food list to show all items
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
                        Log.d("FirebaseData", String.format("Name: %s, Price: RM %.2f", foodItem.getFoodName(), foodItem.getFoodPrice()));
                        foodList.add(foodItem);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read data: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
