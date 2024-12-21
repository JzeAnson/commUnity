package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddFoodActivity extends AppCompatActivity {

    private EditText foodName, pickupShopName, foodPrice, foodDescription;
    private ImageView foodImage;
    private Button submitButton;
    private Uri imageUri;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodName = findViewById(R.id.foodName);
        pickupShopName = findViewById(R.id.pickupShopName);
        foodPrice = findViewById(R.id.foodPrice);
        foodDescription = findViewById(R.id.foodDescription);
        foodImage = findViewById(R.id.foodImage);
        submitButton = findViewById(R.id.submitButton);

        databaseRef = FirebaseDatabase.getInstance().getReference("FoodItems");
        storageRef = FirebaseStorage.getInstance().getReference("FoodImages");

        foodImage.setOnClickListener(v -> openImagePicker());
        submitButton.setOnClickListener(v -> uploadFoodItem());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
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
                        Toast.makeText(AddFoodActivity.this, "Food added successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }));
        }
    }
}