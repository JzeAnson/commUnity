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
            String address = getArguments().getString("pickupAddress");
            String description = getArguments().getString("foodDescription");
            String payment = getArguments().getString("paymentMethod", "Cash-on Arrival"); // Default value

            // Populate data into the UI
            foodName.setText(name);
            price.setText(String.format("RM %.2f", foodPrice));
            pickupAddress.setText(address);
            paymentMethod.setText(payment);
            foodDescription.setText(description);

            // Load image with Glide
            Glide.with(this).load(imageUrl).into(foodImage);
        }

        // Handle back button
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Handle reserve button
        reserveButton.setOnClickListener(v -> {
            // Add your logic for the "Reserve" button here (e.g., navigate to a reservation fragment or show a confirmation dialog)
        });

        return view;
    }
}
