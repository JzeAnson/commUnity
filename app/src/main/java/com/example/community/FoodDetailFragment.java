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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
                            // Fetch user data from Firestore
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            String documentID = userDocument.getInstance().getDocumentId();

                            firestore.collection("users").document(documentID)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String customerName = documentSnapshot.getString("userName");
                                            String customerPhone = documentSnapshot.getString("userPhoneNo");

                                            // Proceed to create the order in Realtime Database
                                            createOrder(foodKey, quantity, customerName, customerPhone);
                                        } else {
                                            Toast.makeText(getContext(), "User not found in Firestore", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error fetching user data", e);
                                        Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
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

    private void createOrder(String foodKey, int quantity, String customerName, String customerPhone) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        String orderKey = databaseRef.child("orders").push().getKey(); // Generate a unique order ID
        String orderDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date()); // Date with spelled-out month
        String orderTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()); // Current time

        // Fetch food details from the database to include in the order
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foodItems").child(foodKey);

        foodRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Fetch food details from the snapshot
                String foodName = snapshot.child("foodName").getValue(String.class);
                String foodDesc = snapshot.child("foodDesc").getValue(String.class);
                String foodPic = snapshot.child("foodPic").getValue(String.class);
                double foodPrice = snapshot.child("foodPrice").getValue(Double.class);
                String merchantName = snapshot.child("merchantName").getValue(String.class);

                // Build order data based on the required format
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("orderID", orderKey);
                orderData.put("foodID", foodKey);
                orderData.put("foodName", foodName);
                orderData.put("foodPrice", foodPrice);
                orderData.put("quantity", quantity);
                orderData.put("merchantName", merchantName);
                orderData.put("customerName", customerName);
                orderData.put("customerPhone", customerPhone);
                orderData.put("orderDate", orderDate);
                orderData.put("orderTime", orderTime);
                orderData.put("foodDesc", foodDesc);
                orderData.put("foodPic", foodPic);
                orderData.put("orderStatus", "Pending");

                // Add order to the database
                databaseRef.child("orders").child(orderKey).setValue(orderData)
                        .addOnSuccessListener(aVoid -> {
                            // Update food quantity after successful order creation
                            updateFoodQuantity(foodKey, quantity);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to create order", Toast.LENGTH_SHORT).show();
                            Log.e("RealtimeDB", "Error creating order", e);
                        });
            } else {
                Toast.makeText(getContext(), "Food item not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error fetching food details", Toast.LENGTH_SHORT).show();
            Log.e("Firebase", "Error fetching food details", e);
        });
    }

    private void updateFoodQuantity(String foodKey, int quantity) {
        DatabaseReference foodRef = FirebaseDatabase.getInstance()
                .getReference("foodItems").child(foodKey);

        foodRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentQuantity = mutableData.child("quantity").getValue(Integer.class);
                if (currentQuantity == null || currentQuantity < quantity) {
                    return Transaction.abort();
                }
                mutableData.child("quantity").setValue(currentQuantity - quantity);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    // Navigate back to FoodListingFragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_layout, new FoodListingFragment());
                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "Not enough stock available", Toast.LENGTH_SHORT).show();
                }
            }
        });
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