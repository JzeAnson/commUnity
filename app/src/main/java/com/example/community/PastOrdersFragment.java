package com.example.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class PastOrdersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_past_orders, container, false);

        // Initialize navigation buttons
        Button btnFoodListing = view.findViewById(R.id.btn_food_listing);
        Button btnPastOrders = view.findViewById(R.id.btn_past_orders);

        // Set click listeners for navigation
        btnFoodListing.setOnClickListener(v -> {
            btnFoodListing.setSelected(true);
            btnPastOrders.setSelected(false);
            replaceFragment(new FoodListingFragment());
        });

        btnPastOrders.setOnClickListener(v -> {
            btnFoodListing.setSelected(false);
            btnPastOrders.setSelected(true);
            Log.i("PastOrdersFragment", "Already on Past Orders fragment");
        });

// Set initial state
        btnFoodListing.setSelected(false);
        btnPastOrders.setSelected(true);

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            Log.e("PastOrdersFragment", "Fragment is null. Cannot replace.");
            return;
        }
        Log.i("PastOrdersFragment", "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}