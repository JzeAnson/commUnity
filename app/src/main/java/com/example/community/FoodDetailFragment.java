package com.example.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class FoodDetailFragment extends Fragment {

    private ImageView foodImage;
    private TextView foodName, price, pickupAddress, paymentMethod, foodDescription;
    private ImageButton backButton;
    private Button reserveButton;

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

        // Get data from arguments
        if (getArguments() != null) {
            String name = getArguments().getString("foodName");
            double foodPrice = getArguments().getDouble("foodPrice");
            String imageUrl = getArguments().getString("foodImage");
            String merchantName = getArguments().getString("merchantName");
            String description = getArguments().getString("foodDescription");
            String payment = getArguments().getString("paymentMethod", "Cash-on Arrival");

            // Combine merchant name and address
            String merchantAddress = getMerchantAddress(merchantName);
            String combinedAddress = merchantName + "\n" + merchantAddress;

            // Populate static fields
            foodName.setText(name);
            price.setText(String.format("RM %.2f", foodPrice));
            foodDescription.setText(description);
            paymentMethod.setText(payment);
            Glide.with(this).load(imageUrl).into(foodImage);
            pickupAddress.setText(combinedAddress); // Updated field
        }

        // Handle back button
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Handle reserve button
        reserveButton.setOnClickListener(v -> {
            // Add your logic for the "Reserve" button here
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