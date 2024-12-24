package com.example.codeforcommunityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.community.userDocument;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {

   Button btnLogout, btnForgotEmail, btnPasswordForgot;
   TextView txtForgot, txtList;

   EditText txtemailforgot, txtpasswordforgot;
   FrameLayout blurScreen, forgotLayout, loadingLayout,passwordLayout, listLayout;
   LinearLayout settingLayout;

    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        btnLogout = rootView.findViewById(R.id.btnLogout);
        btnForgotEmail = rootView.findViewById(R.id.btnSubmitEmail);
        btnPasswordForgot = rootView.findViewById(R.id.btnSubmitPassword);
        txtForgot = rootView.findViewById(R.id.forgot_password_option);
        txtList = rootView.findViewById(R.id.list_developer);
        forgotLayout = rootView.findViewById(R.id.forget_layout);
        settingLayout = rootView.findViewById(R.id.setting_layout);
        txtemailforgot = rootView.findViewById(R.id.txtEmailForgot);
        txtpasswordforgot = rootView.findViewById(R.id.txtPasswordForgot);
        loadingLayout = rootView.findViewById(R.id.loading_overlay);
        passwordLayout = rootView.findViewById(R.id.password_layout);
        listLayout = rootView.findViewById(R.id.list_layout);


        db = FirebaseFirestore.getInstance();

        String documentID = userDocument.getInstance().getDocumentId();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("userId"); // Remove only the userId key
                editor.apply();

                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Example: Navigate to LoginActivity
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtemailforgot.setText("");
                settingLayout.setAlpha(0.5f);
                forgotLayout.setVisibility(View.VISIBLE);
                btnLogout.setEnabled(false);
                txtList.setEnabled(false);
            }
        });

        txtList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listLayout.setVisibility(View.VISIBLE);
                txtForgot.setEnabled(false);
                btnLogout.setEnabled(false);
                settingLayout.setAlpha(0.5f);
            }
        });

        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogout.setEnabled(true);
                txtForgot.setEnabled(true);
                txtList.setEnabled(true);
                settingLayout.setAlpha(1f);
                forgotLayout.setVisibility(View.GONE);
                passwordLayout.setVisibility(View.GONE);
                listLayout.setVisibility(View.GONE);
            }
        });

        btnForgotEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forgotLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);

                String email = txtemailforgot.getText().toString();

                db.collection("users").document(documentID)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                if(email.equals(documentSnapshot.getString("userEmail"))){

                                    passwordLayout.setVisibility(View.VISIBLE);
                                    txtpasswordforgot.setText("");
                                }else{
                                    loadingLayout.setVisibility(View.GONE);
                                    forgotLayout.setVisibility(View.VISIBLE);
                                    txtemailforgot.setError("Incorrect email");
                                }

                            } else {
                                loadingLayout.setVisibility(View.GONE);
                                forgotLayout.setVisibility(View.VISIBLE);
                                txtemailforgot.setError("No such email");

                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreExample", "Error fetching document", e);
                            Toast.makeText(getContext(), "Error fetching document: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        })
                        .addOnCompleteListener(task -> {
                            loadingLayout.setVisibility(View.GONE);
                        });
            }
        });

        btnPasswordForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passwordLayout.setVisibility(View.GONE);

                String newPassword = txtpasswordforgot.getText().toString();

                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("userPassword", newPassword);

                loadingLayout.setVisibility(View.VISIBLE);

                // Update Firestore document
                db.collection("users").document(documentID)
                        .update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreExample", "Error updating document", e);
                            Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        })
                        .addOnCompleteListener(task -> {
                            loadingLayout.setVisibility(View.GONE);
                            btnLogout.setEnabled(true);
                            settingLayout.setAlpha(1f);

                        });

            }
        });

        return rootView;
    }



}