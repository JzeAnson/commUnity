package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddFoodFragment extends Fragment {

    private EditText foodName, pickupShopName, foodPrice, foodDescription;
    private ImageView foodImage;
    private Button submitButton;
    private Uri imageUri;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);

        foodName = view.findViewById(R.id.foodName);
        pickupShopName = view.findViewById(R.id.pickupShopName);
        foodPrice = view.findViewById(R.id.foodPrice);
        foodDescription = view.findViewById(R.id.foodDescription);
        foodImage = view.findViewById(R.id.foodImage);
        submitButton = view.findViewById(R.id.submitButton);

        databaseRef = FirebaseDatabase.getInstance().getReference("FoodItems");
        storageRef = FirebaseStorage.getInstance().getReference("FoodImages");

        foodImage.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> uploadFoodItem());

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

    private void uploadFoodItem() {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String id = databaseRef.push().getKey();
                FoodItem foodItem = new FoodItem(
                        foodName.getText().toString(),
                        pickupShopName.getText().toString(),
                        foodPrice.getText().toString(),
                        foodDescription.getText().toString(),
                        uri.toString()
                );

                databaseRef.child(id).setValue(foodItem).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Food added successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }));
        }
    }
}