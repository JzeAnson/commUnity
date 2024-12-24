package com.example.community;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    Button btn_login;
    TextView txt_create;

    EditText txt_username, txt_password;
    FirebaseFirestore firestore;


    ProgressBar pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        if(userId != null){
            userDocument.getInstance().setDocumentId(userId);
            openActivity(HomeMain.class);
            finish();
        }else{
            btn_login= (Button)findViewById(R.id.button_login);
            txt_create = (TextView)findViewById(R.id.createAcc);
            txt_username = (EditText)findViewById(R.id.txtUsername);
            txt_password = (EditText)findViewById(R.id.txtPassword);
            pg = (ProgressBar)findViewById(R.id.progressBar);

            firestore = FirebaseFirestore.getInstance();
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String username = txt_username.getText().toString();
                    String password = txt_password.getText().toString();

                    if(username.isEmpty() || password.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Please fill in the fields",Toast.LENGTH_LONG).show();
                    }else{
                        pg.setVisibility(View.VISIBLE);

                        firestore.collection("users")
                                .whereEqualTo("userName", username)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        boolean passwordMatched = false;
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                            String Firepassword = document.getString("userPassword");

                                            if(Firepassword.equals(password)){

                                                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("userId", document.getId()); // Mark user as logged in
                                                editor.apply();

                                                userDocument.getInstance().setDocumentId(document.getId());

                                                passwordMatched = true;
                                                openActivity(HomeMain.class);
                                                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();

                                                break;
                                            }
                                        }
                                        if (!passwordMatched) {
                                            txt_password.setError("Incorrect Password");
                                            pg.setVisibility(View.GONE);
                                        }

                                    } else {
                                        txt_username.setError("Username does not exist");
                                        pg.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getApplicationContext(),"System error. Please try again later",Toast.LENGTH_LONG).show();
                                    pg.setVisibility(View.GONE);
                                });
                    }
                }
            });

            txt_create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openActivity(Register.class);
                }
            });
        }



    }



    public void openActivity(Class<?> activityClass){
        Intent intent = new Intent(this, activityClass);
        if(activityClass.equals(HomeMain.class)){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);

    }

}