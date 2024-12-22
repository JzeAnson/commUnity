package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddFoodFragment extends Fragment {

    private EditText foodName, foodPrice, foodDescription, foodLocation;
    private ImageView foodImage;
    private Button submitButton;
    private ProgressBar progressBar;
    private Uri imageUri;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);

        foodName = view.findViewById(R.id.foodName);
        foodPrice = view.findViewById(R.id.foodPrice);
        foodDescription = view.findViewById(R.id.foodDescription);
        foodImage = view.findViewById(R.id.foodImage);
        foodLocation=view.findViewById(R.id.pickupShopName);
        submitButton = view.findViewById(R.id.submitButton);
//        progressBar = view.findViewById(R.id.progressBar); // Assuming a progress bar is in the layout
        progressBar.setVisibility(View.GONE);

        databaseRef = FirebaseDatabase.getInstance().getReference("foodItems"); // Updated to match FoodItem
        storageRef = FirebaseStorage.getInstance().getReference("FoodImages");

        foodImage.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> validateAndUploadFoodItem());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            foodImage.setImageURI(imageUri);
        }
    }

    private void validateAndUploadFoodItem() {
        String name = foodName.getText().toString().trim();
        String price = foodPrice.getText().toString().trim();
        String description = foodDescription.getText().toString().trim();
        String location = foodLocation.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            foodName.setError("Food name is required");
            return;
        }
        if (TextUtils.isEmpty(price)) {
            foodPrice.setError("Food price is required");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            foodDescription.setError("Food description is required");
            return;
        }
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            foodLocation.setError("Food Location is required");
            return;
        }

        uploadFoodItem(name, price, description, location);
    }

    private void uploadFoodItem(String name, String price, String description, String location) {
        progressBar.setVisibility(View.VISIBLE);

        // Parse price to double
        double foodPriceValue = 0;
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String id = databaseRef.push().getKey();
                    FoodItem foodItem = new FoodItem(
                            name,
                            foodPriceValue,
                            description,
                            location,
                            uri.toString() // Pass the uploaded image URL
                    );

                    databaseRef.child(id).setValue(foodItem).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Food added successfully!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(getActivity(), "Failed to add food", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearFields() {
        foodName.setText("");
        foodPrice.setText("");
        foodDescription.setText("");
        foodImage.setImageResource(R.drawable.ic_add); // Use your placeholder image here
        foodLocation.setText("");
        imageUri = null;
    }
}