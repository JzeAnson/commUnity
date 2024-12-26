package com.example.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodDetailFragment extends Fragment {

    private ImageView foodImage;
    private TextView foodName, price, pickupAddress, paymentMethod, foodDescription, quantityTextView;
    private ImageButton backButton, incrementButton, decrementButton;
    private Button reserveButton;

    private int quantity = 1; // Default quantity
    private int availableQuantity = 0; // Default available quantity

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);

        // Initialize UI elements
        foodImage = view.findViewById(R.id.foodImage);
        foodName = view.findViewById(R.id.foodName);
        price = view.findViewById(R.id.price);
        pickupAddress = view.findViewById(R.id.pickupAddress);
        paymentMethod = view.findViewById(R.id.paymentMethod);
        foodDescription = view.findViewById(R.id.foodDescription);
        backButton = view.findViewById(R.id.backButton);
        reserveButton = view.findViewById(R.id.reserveButton);
        incrementButton = view.findViewById(R.id.incrementButton); // Initialize increment button
        decrementButton = view.findViewById(R.id.decrementButton); // Initialize decrement button
        quantityTextView = view.findViewById(R.id.quantityTextView); // Quantity display

        // Get data from arguments
        if (getArguments() != null) {
            String name = getArguments().getString("foodName");
            double foodPrice = getArguments().getDouble("foodPrice");
            String imageUrl = getArguments().getString("foodImage");
            String merchantName = getArguments().getString("merchantName");
            String description = getArguments().getString("foodDescription");
            String payment = getArguments().getString("paymentMethod", "Cash-on Arrival");
            availableQuantity = getArguments().getInt("availableQuantity", 0); // Get available quantity

            // Combine merchant name and address
            String merchantAddress = getMerchantAddress(merchantName);
            String combinedAddress = merchantName + "\n" + merchantAddress;

            // Populate static fields
            foodName.setText(name);
            price.setText(String.format("RM %.2f", foodPrice));
            foodDescription.setText(description);
            paymentMethod.setText(payment);
            Glide.with(this).load(imageUrl).into(foodImage);
            pickupAddress.setText(combinedAddress);
            quantityTextView.setText(String.valueOf(quantity)); // Set initial quantity
        }

        // Handle increment button
        incrementButton.setOnClickListener(v -> {
            if (quantity < availableQuantity) { // Enforce maximum limit
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Maximum quantity available is " + availableQuantity, Toast.LENGTH_SHORT).show();
            }
        });

        // Handle decrement button
        decrementButton.setOnClickListener(v -> {
            if (quantity > 1) { // Enforce minimum limit
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle back button
        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Replace the current fragment with a fresh instance of FoodListingFragment
            transaction.replace(R.id.frame_layout, new FoodListingFragment());
            transaction.commit();
        });

        // Handle reserve button
        reserveButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Reservation Confirmation")
                    .setMessage("You are reserving " + quantity + " item(s).\n\n⚠️ Important:\nFailure to claim your order within the stipulated time may result in the merchant canceling your order.")
                    .setPositiveButton("Agree", (dialog, which) -> {
                        String foodKey = getArguments().getString("foodKey");

                        if (foodKey != null) {
                            DatabaseReference foodRef = FirebaseDatabase.getInstance("https://community-1f007-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference("foodItems")
                                    .child(foodKey);

                            // Update reserved quantity and reduce available quantity
                            foodRef.child("reservedQuantity").setValue(quantity);
                            foodRef.child("quantity").setValue(availableQuantity - quantity);
                            foodRef.child("status").setValue("Reserved")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Reservation Successful!", Toast.LENGTH_SHORT).show();
                                        FragmentManager fragmentManager = getParentFragmentManager();
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.replace(R.id.frame_layout, new FoodListingFragment());
                                        transaction.commit();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "Error: Food item key is missing.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return view;
    }

    private String getMerchantAddress(String location) {
        switch (location) {
            case "AEON MALL AU2 Setiawangsa":
                return "No.6 Jalan Taman Setiawangsa (Jalan 37/56), AU2, Taman Keramat, 54200 Kuala Lumpur";
            case "Dunkin Donuts Aeon Big":
                return "Level 1, Lot F1-62, Section 5, Jalan 8/27a, Wangsa Maju, 53300 Kuala Lumpur, Wilayah Persekutuan";
            case "Empire Sushi Melawati Mall":
                return "Lot G-23A, Melawati Mall, 355 Jalan Bandar, Taman Melawati, 53100 Kuala Lumpur, Wilayah Persekutuan";
            case "Bakers’ Cottage Taman Melawati":
                return "No. 15 Jalan Bandar 12, Taman Melawati, 53100 Kuala Lumpur, Wilayah Persekutuan";
            default:
                return "Unknown Location";
        }
    }
}