package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddFoodFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText foodName, foodPrice, foodDescription, foodQuantity; // Added quantity input
    private Spinner foodLocationSpinner;
    private ImageButton btnBack;
    private ImageView foodImage;
    private Button submitButton, clearButton;
    private Uri imageUri;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("AddFoodFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);

        btnBack = view.findViewById(R.id.backArrow);
        foodName = view.findViewById(R.id.foodName);
        foodPrice = view.findViewById(R.id.foodPrice);
        foodDescription = view.findViewById(R.id.foodDescription);
        foodQuantity = view.findViewById(R.id.foodQuantity); // Initialize quantity input
        foodImage = view.findViewById(R.id.foodImage);
        foodLocationSpinner = view.findViewById(R.id.pickupShopNameSpinner);
        submitButton = view.findViewById(R.id.submitButton);
        clearButton = view.findViewById(R.id.clearButton);

        databaseRef = FirebaseDatabase.getInstance().getReference("foodItems"); // Updated to match FoodItem
        storageRef = FirebaseStorage.getInstance().getReference("FoodImages");

        // Set up Spinner options
        String[] pickupLocations = {
                "AEON MALL AU2 Setiawangsa",
                "Dunkin Donuts Aeon Big",
                "Sushi Combo Set",
                "Bakers’ Cottage Taman Melawati"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                pickupLocations
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodLocationSpinner.setAdapter(adapter);

        foodImage.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> validateAndUploadFoodItem());
        btnBack.setOnClickListener(this::pressBack);
        clearButton.setOnClickListener(v -> clearFields()); // Set onClickListener for clearButton

        return view;
    }

    public void pressBack(View v) {
        Log.i("AddFoodFragment", "Back button pressed.");
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with FoodListingFragment
        transaction.replace(R.id.frame_layout, new FoodListingFragment());
        transaction.commit();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            foodImage.setImageURI(imageUri); // Display selected image
        }
    }

    private void validateAndUploadFoodItem() {
        Log.d("validateAndUpload", "Validation started.");

        String name = foodName.getText().toString().trim();
        String price = foodPrice.getText().toString().trim();
        String description = foodDescription.getText().toString().trim();
        String quantityStr = foodQuantity.getText().toString().trim(); // Get quantity input
        String location = foodLocationSpinner.getSelectedItem().toString();

        // Validate food name
        if (TextUtils.isEmpty(name)) {
            foodName.setError("Food name is required");
            return;
        }

        // Validate food price
        double foodPriceValue;
        try {
            foodPriceValue = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            foodPrice.setError("Invalid price format");
            return;
        }

        // Validate food description
        if (TextUtils.isEmpty(description)) {
            foodDescription.setError("Food description is required");
            return;
        }

        // Validate quantity
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                foodQuantity.setError("Quantity must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            foodQuantity.setError("Invalid quantity format");
            return;
        }

        // Validate image URI
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the upload function
        uploadFoodItem(name, foodPriceValue, description, quantity, location);
    }

    private void uploadFoodItem(String name, double price, String description, int quantity, String location) {
        String merchantAddress;

        switch (location) {
            case "AEON MALL AU2 Setiawangsa":
                merchantAddress = "No.6 Jalan Taman Setiawangsa (Jalan 37/56), AU2, Taman Keramat, 54200 Kuala Lumpur";
                break;
            case "Dunkin Donuts Aeon Big":
                merchantAddress = "Level 1, Lot F1-62, Section 5, Jalan 8/27a, Wangsa Maju, 53300 Kuala Lumpur, Wilayah Persekutuan";
                break;
            case "Sushi Combo Set":
                merchantAddress = "Lot G-23A, Melawati Mall, 355 Jalan Bandar, Taman Melawati, 53100 Kuala Lumpur, Wilayah Persekutuan";
                break;
            case "Bakers’ Cottage Taman Melawati":
                merchantAddress = "No. 15 Jalan Bandar 12, Taman Melawati, 53100 Kuala Lumpur, Wilayah Persekutuan";
                break;
            default:
                merchantAddress = "Unknown Location";
        }

        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String id = databaseRef.push().getKey();
                            FoodItem foodItem = new FoodItem(
                                    name,
                                    price, // Pass price as double
                                    description,
                                    uri.toString(), // Image URL
                                    location,
                                    merchantAddress,
                                    "Available", // Default status
                                    quantity // Add quantity
                            );

                            databaseRef.child(id).setValue(foodItem)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Food added successfully!", Toast.LENGTH_SHORT).show();
                                            clearFields();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to add food", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        foodName.setText("");
        foodPrice.setText("");
        foodDescription.setText("");
        foodQuantity.setText(""); // Clear quantity field
        foodImage.setImageResource(R.drawable.ic_add); // Placeholder image
        foodLocationSpinner.setSelection(0);
        imageUri = null;
    }
}