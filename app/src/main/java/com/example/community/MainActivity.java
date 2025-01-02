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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.app.NotificationChannel;
import android.app.NotificationManager;



import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


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
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance("https://community-1f007-default-rtdb.asia-southeast1.firebasedatabase.app");

        // Create notification channel
        createNotificationChannel();

        // Check and request notification permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission();
        }

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
/*
    Button btn_busTracking;
    ProgressDialog progressDialog;
    SplitInstallManager splitInstallManager;
    private int mySessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        splitInstallManager = SplitInstallManagerFactory.create(this);
        progressDialog = new ProgressDialog(this);

        btn_busTracking = findViewById(R.id.btn_bustrackingmodule);

        btn_busTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloading();
            }
        });

    }

    private void startDownloading() {
        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule("busTrackingModule").build();
        SplitInstallStateUpdatedListener listener = splitInstallState -> {
            if (splitInstallState.sessionId() == mySessionId) {
                switch (splitInstallState.status()) {
                    case SplitInstallSessionStatus.DOWNLOADING:
                        progressDialog.setMessage("Downloading");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.INSTALLED:
                        Intent intent = new Intent();
                        intent.setClassName(BuildConfig.APPLICATION_ID, "com.example.bustrackingmodule.BusTrackingMainActivity");
                        startActivity(intent);
                    case SplitInstallSessionStatus.CANCELED:
                        Toast.makeText(this,"Canceled",Toast.LENGTH_SHORT).show();
                        break;
                    case SplitInstallSessionStatus.CANCELING:
                        progressDialog.setMessage("Cancelling");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.DOWNLOADED:
                        break;
                    case SplitInstallSessionStatus.FAILED:
                        break;
                    case SplitInstallSessionStatus.INSTALLING:
                        progressDialog.setMessage("Installing");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.PENDING:
                        progressDialog.setMessage("Pending");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION:
                        break;
                    case SplitInstallSessionStatus.UNKNOWN:
                        break;
                }

            }
        };

        splitInstallManager.registerListener(listener);

        splitInstallManager.startInstall(request)
                .addOnFailureListener(e ->
                        Log.e("MainActivity", "Error installing module", e)
                )
                .addOnSuccessListener(sessionId -> mySessionId = sessionId);

    }

 */

    @Override
    public void onBackPressed() {
        // Get the fragment manager
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();

        // Check if there are fragments in the back stack
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // If there are fragments, pop the top fragment from the back stack
            fragmentManager.popBackStack();
        } else {
            // If no fragments in back stack, finish the activity
            super.onBackPressed();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Warnings Channel";
            String description = "Channel for displaying emergency warnings";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("warnings_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied - you can show a message explaining why the permission is needed
            }
        }
    }

}