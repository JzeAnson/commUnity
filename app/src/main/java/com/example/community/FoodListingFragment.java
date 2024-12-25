package com.example.community;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodListingFragment extends Fragment {

    private EditText searchBar;
    private ImageButton clearButton, searchButton, profileButton;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
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
        profileButton = view.findViewById(R.id.profileButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.addButton);
        if (fab!=null){
            fab.setOnClickListener(this::pressAdd);
        }else{
            Log.e("FoodListingFragment", "fab is null. Check R.id.addButton in your layout");
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        foodList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), filteredList, this::openFoodDetail);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance("https://community-1f007-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("foodItems");

        // Fetch food data from Firebase
        fetchFoodData();

        // Set up search functionality
        setupSearchBar();

        // Role-based visibility for FAB
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String currentRole = sharedPreferences.getString("userRole", "customer"); // Default role is customer
        updateUIForRole(currentRole);

        // Profile button click listener to change roles
        profileButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Switch Role")
                    .setMessage("Select your role:")
                    .setPositiveButton("Merchant", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userRole", "merchant");
                        editor.apply();
                        updateUIForRole("merchant");
                        Toast.makeText(getContext(), "Switched to Merchant", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Customer", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userRole", "customer");
                        editor.apply();
                        updateUIForRole("customer");
                        Toast.makeText(getContext(), "Switched to Customer", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            Log.e("FoodListingFragment", "Fragment is null. Cannot replace.");
            return;
        }
        Log.i("FoodListingFragment", "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void setupSearchBar() {
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

        clearButton.setOnClickListener(v -> {
            searchBar.setText("");
            filterFoodList("");
        });

        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            filterFoodList(query);
            Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
        });
    }

    private void filterFoodList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(foodList);
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
                foodList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
                    if (foodItem != null) {
                        foodList.add(foodItem);
                    }
                }
                filteredList.clear();
                filteredList.addAll(foodList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read data: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIForRole(String role) {
        if ("merchant".equals(role)) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    public void pressAdd (View v){
        Log.i("FoodListingFragment", "Add button pressed.");
        replaceFragment(new AddFoodFragment());
    }

    private void openFoodDetail(FoodItem foodItem) {
        FoodDetailFragment foodDetailFragment = new FoodDetailFragment();
        Bundle args = new Bundle();
        args.putString("foodName", foodItem.getFoodName());
        args.putDouble("foodPrice", foodItem.getFoodPrice());
        args.putString("foodImage", foodItem.getFoodPic());
        args.putString("merchantName", foodItem.getMerchantName());
        args.putString("foodDescription", foodItem.getFoodDesc());
        foodDetailFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, foodDetailFragment)
                .addToBackStack(null) // Add to back stack for proper back navigation
                .commit();
    }
}