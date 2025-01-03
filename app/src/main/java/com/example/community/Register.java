package com.example.community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.community.MainActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    Button btn_register;
    EditText txt_username, txt_email, txt_phone, txt_pass, txt_confirm_pass;
    RadioGroup roleGroup;
   FirebaseFirestore firestore;

   ProgressBar pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        btn_register= (Button)findViewById(R.id.button_register);
        txt_username = (EditText) findViewById(R.id.userText);
        txt_email = (EditText) findViewById(R.id.emailText);
        txt_phone = (EditText) findViewById(R.id.phoneText);
        txt_pass = (EditText) findViewById(R.id.passwordText);
        txt_confirm_pass = (EditText) findViewById(R.id.confirmText);
        pg = (ProgressBar)findViewById(R.id.progressBar);


        firestore = FirebaseFirestore.getInstance();


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = String.valueOf(txt_username.getText());
                String email = String.valueOf(txt_email.getText());
                String phone = String.valueOf(txt_phone.getText());
                String pass = String.valueOf(txt_pass.getText());
                String confirmPass = String.valueOf(txt_confirm_pass.getText());

                if(username.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill in the fields",Toast.LENGTH_LONG).show();
                }else{
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        String regex = "^(\\+60|0)[1-9][0-9]{7,9}$";
                        if(phone.matches(regex)){
                            if(pass.equals(confirmPass)){

                                pg.setVisibility(View.VISIBLE);
                                firestore.collection("users") // Replace "users" with your collection name
                                        .whereEqualTo("userName", username) // Query to check the username
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                txt_username.setError("Username exist");

                                            } else {
                                                Map<String,Object> user = new HashMap<>();
                                                user.put("userName",username);
                                                user.put("userEmail",email);
                                                user.put("userPhoneNo",phone);
                                                user.put("userPassword",pass);

                                                firestore.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(getApplicationContext(),"Register Successful",Toast.LENGTH_LONG).show();
                                                        openActivity();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),"Register Unsuccessful",Toast.LENGTH_LONG).show();
                                                        pg.setVisibility(View.GONE);
                                                    }
                                                });

                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getApplicationContext(),"System error. Please try again later",Toast.LENGTH_LONG).show();
                                            pg.setVisibility(View.GONE);
                                        });
                            }else{
                                txt_confirm_pass.setError("Password does not match");
                                pg.setVisibility(View.GONE);
                            }
                        }else{
                            txt_phone.setError("Incorrect phone format");
                            pg.setVisibility(View.GONE);
                        }
                    }else{
                        txt_email.setError("Incorrect email format");
                        pg.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public void openActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}