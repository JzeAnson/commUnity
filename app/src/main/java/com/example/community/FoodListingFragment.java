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
    private List<String> foodKeys; // List to hold keys for food items
    private DatabaseReference databaseRef;
    private FoodAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FoodListingFragment", "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_food_listing, container, false);

        // Initialize UI components
        Log.d("FoodListingFragment", "Initializing UI components...");
        searchBar = view.findViewById(R.id.searchBar);
        clearButton = view.findViewById(R.id.clearButton);
        searchButton = view.findViewById(R.id.searchButton);
        profileButton = view.findViewById(R.id.profileButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.addButton);

        if (fab != null) {
            Log.d("FoodListingFragment", "Setting up FAB click listener.");
            fab.setOnClickListener(this::pressAdd);
        } else {
            Log.e("FoodListingFragment", "FAB is null. Check R.id.addButton in your layout.");
        }

        // Set up RecyclerView
        Log.d("FoodListingFragment", "Setting up RecyclerView...");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        foodList = new ArrayList<>();
        foodKeys = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize adapter with empty lists
        adapter = new FoodAdapter(getContext(), foodList, foodKeys, (foodItem, foodKey) -> openFoodDetail(foodItem, foodKey));
        recyclerView.setAdapter(adapter);
        Log.d("FoodListingFragment", "RecyclerView adapter set.");

        // Initialize Firebase database reference
        Log.d("FoodListingFragment", "Initializing Firebase database reference...");
        databaseRef = FirebaseDatabase.getInstance("https://community-1f007-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("foodItems");

        // Fetch food data from Firebase
        Log.d("FoodListingFragment", "Fetching food data from Firebase...");
        fetchFoodData();

        // Set up search functionality
        Log.d("FoodListingFragment", "Setting up search bar functionality...");
        setupSearchBar();

        // Role-based visibility for FAB
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String currentRole = sharedPreferences.getString("userRole", "customer"); // Default role is customer
        Log.d("FoodListingFragment", "Current user role: " + currentRole);
        updateUIForRole(currentRole);

        // Profile button click listener to change roles
        profileButton.setOnClickListener(v -> {
            Log.d("FoodListingFragment", "Profile button clicked. Showing role switch dialog.");
            new AlertDialog.Builder(getContext())
                    .setTitle("Switch Role")
                    .setMessage("Select your role:")
                    .setPositiveButton("Merchant", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userRole", "merchant");
                        editor.apply();
                        Log.d("FoodListingFragment", "Role switched to Merchant.");
                        updateUIForRole("merchant");
                        Toast.makeText(getContext(), "Switched to Merchant", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Customer", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userRole", "customer");
                        editor.apply();
                        Log.d("FoodListingFragment", "Role switched to Customer.");
                        updateUIForRole("customer");
                        Toast.makeText(getContext(), "Switched to Customer", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        Log.d("FoodListingFragment", "onCreateView completed.");

        // Listen for refresh signal
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            boolean shouldRefresh = bundle.getBoolean("refresh", false);
            if (shouldRefresh) {
                fetchFoodData(); // Fetch data again to refresh the view
            }
        });

        return view;
    }

    private void fetchFoodData() {
        Log.d("Firebase", "Fetching food data from Firebase...");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                foodKeys.clear();

                Log.d("Firebase", "Data snapshot received. Child count: " + snapshot.getChildrenCount());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
                    String foodKey = dataSnapshot.getKey();

                    if (foodItem != null && foodKey != null) {
                        foodList.add(foodItem);
                        foodKeys.add(foodKey);
                        Log.d("Firebase", "Added item: " + foodItem.getFoodName() + " with key: " + foodKey);
                    } else {
                        Log.e("Firebase", "Null item or key found in snapshot.");
                    }
                }

                adapter.notifyDataSetChanged(); // Notify adapter after data change
                Log.d("Firebase", "Adapter updated with " + foodList.size() + " items.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read data: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("SearchBar", "onTextChanged: query = " + s.toString());
                filterFoodList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        clearButton.setOnClickListener(v -> {
            searchBar.setText("");
            filterFoodList("");
            Log.d("SearchBar", "Clear button clicked. SearchBar text cleared.");
        });

        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            Log.d("SearchBar", "Search button clicked. Query = " + query);
            filterFoodList(query);
            Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
        });
    }

    private void filterFoodList(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(foodList); // Show all items if query is empty
        } else {
            for (FoodItem item : foodList) {
                if (item.getFoodName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getFoodDesc().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        Log.d("Filter", "Filtered list size: " + filteredList.size());

        // Update the adapter with the filtered data
        adapter = new FoodAdapter(getContext(), filteredList, foodKeys, (foodItem, foodKey) -> openFoodDetail(foodItem, foodKey));
        recyclerView.setAdapter(adapter); // Rebind the adapter to update the UI
    }

    private void updateUIForRole(String role) {
        if ("merchant".equals(role)) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    public void pressAdd(View v) {
        Log.i("FoodListingFragment", "Add button pressed.");
        replaceFragment(new AddFoodFragment());
    }

    private void openFoodDetail(FoodItem foodItem, String foodKey) {
        FoodDetailFragment foodDetailFragment = new FoodDetailFragment();
        Bundle args = new Bundle();
        args.putString("foodName", foodItem.getFoodName());
        args.putDouble("foodPrice", foodItem.getFoodPrice());
        args.putString("foodImage", foodItem.getFoodPic());
        args.putString("merchantName", foodItem.getMerchantName());
        args.putString("foodDescription", foodItem.getFoodDesc());
        args.putString("foodKey", foodKey);
        args.putInt("availableQuantity", foodItem.getQuantity()); // Pass available quantity here
        foodDetailFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, foodDetailFragment)
                .addToBackStack(null)
                .commit();
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
}
