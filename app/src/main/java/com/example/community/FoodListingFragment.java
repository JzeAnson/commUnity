package com.example.community;

import android.os.Bundle;
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

public class FoodListingFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<FoodItem> foodList;
    private DatabaseReference databaseRef;
    private FoodAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FoodListingFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_food_listing, container, false);

        // Initialize RecyclerView and set LayoutManager
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize the food list and adapter
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), foodList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance("https://community-1f007-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("foodItems");
        Log.d("FoodListingFragment", "Database Reference initialized: " + databaseRef);
        // Fetch data from Firebase
        fetchFoodData();

        return view;
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
