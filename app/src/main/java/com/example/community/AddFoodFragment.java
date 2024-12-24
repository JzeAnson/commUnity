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
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private EditText foodName, foodPrice, foodDescription, foodLocation;
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

        btnBack=view.findViewById(R.id.backArrow);
        foodName = view.findViewById(R.id.foodName);
        foodPrice = view.findViewById(R.id.foodPrice);
        foodDescription = view.findViewById(R.id.foodDescription);
        foodImage = view.findViewById(R.id.foodImage);
        foodLocation=view.findViewById(R.id.pickupShopName);
        submitButton = view.findViewById(R.id.submitButton);
        clearButton=view.findViewById(R.id.clearButton);

        databaseRef = FirebaseDatabase.getInstance().getReference("foodItems"); // Updated to match FoodItem
        storageRef = FirebaseStorage.getInstance().getReference("FoodImages");

        foodImage.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> validateAndUploadFoodItem());
        if (btnBack!=null){
            btnBack.setOnClickListener(this::pressBack);
        }else{
            Log.e("AddFoodFragment", "btnBack is null. Check R.id.btnBack in your layout");
        }
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

        double foodPriceValue;
        try {
            foodPriceValue = Double.parseDouble(price);
            foodPriceValue = Math.round(foodPriceValue * 100.0) / 100.0; // Ensure 2 decimal places
        } catch (NumberFormatException e) {
            foodPrice.setError("Invalid price format");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            foodDescription.setError("Food description is required");
            return;
        }
        if (TextUtils.isEmpty(location)) {
            foodLocation.setError("Food location is required");
            return;
        }
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadFoodItem(name, foodPriceValue, description, location);
    }

    private void uploadFoodItem(String name, double price, String description, String location) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String id = databaseRef.push().getKey();
                    FoodItem foodItem = new FoodItem(
                            name,
                            price,
                            description,
                            uri.toString(), // Image URL
                            location
                    );

                    databaseRef.child(id).setValue(foodItem).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Food added successfully!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(getActivity(), "Failed to add food", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
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