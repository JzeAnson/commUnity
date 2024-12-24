package com.example.community;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {


    EditText txtuser, txtemail, txtphone;
    TextInputEditText txtbio;

    Button btnUpdate;
    FrameLayout loadingOverlay;

    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();

        txtuser = rootView.findViewById(R.id.txt_user);
        txtemail = rootView.findViewById(R.id.txt_email);
        txtphone = rootView.findViewById(R.id.txt_phone);
        txtbio = rootView.findViewById(R.id.txt_bio);
        btnUpdate = rootView.findViewById(R.id.btn_update);
        loadingOverlay = rootView.findViewById(R.id.loading_overlay);

        String documentID = userDocument.getInstance().getDocumentId();
        if (documentID == null || documentID.isEmpty()) {
            Toast.makeText(getContext(), "Document ID is null or empty", Toast.LENGTH_LONG).show();
            return rootView; // Exit if documentID is invalid
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        db.collection("users").document(documentID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        txtuser.setText(documentSnapshot.getString("userName"));
                        txtemail.setText(documentSnapshot.getString("userEmail"));
                        txtphone.setText(documentSnapshot.getString("userPhoneNo"));
                        txtbio.setText(documentSnapshot.getString("userBio"));
                    } else {
                        Toast.makeText(getContext(),"No such document!",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreExample", "Error fetching document", e);
                    Toast.makeText(getContext(), "Error fetching document: " + e.getMessage(), Toast.LENGTH_LONG).show();
                })
                .addOnCompleteListener(task -> {
                    loadingOverlay.setVisibility(View.GONE);
                });


        btnUpdate.setOnClickListener(v -> {
            // Retrieve updated data from input fields
            String updatedName = txtuser.getText().toString();
            String updatedEmail = txtemail.getText().toString();
            String updatedPhone = txtphone.getText().toString();
            String updatedBio = txtbio.getText().toString();


            // Create a map to hold updated data
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("userName", updatedName);
            updatedData.put("userEmail", updatedEmail);
            updatedData.put("userPhoneNo", updatedPhone);
            updatedData.put("userBio", updatedBio);

            loadingOverlay.setVisibility(View.VISIBLE);

            // Update Firestore document
            db.collection("users").document(documentID)
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreExample", "Error updating document", e);
                        Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnCompleteListener(task -> {
                        loadingOverlay.setVisibility(View.GONE);
                    });
        });


        // Inflate the layout for this fragment
        return rootView;

    }
}