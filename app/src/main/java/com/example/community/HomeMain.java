//package com.example.community;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.appcompat.widget.Toolbar;
//
//import com.example.community.databinding.ActivityHomeMainBinding;
//
//public class HomeMain extends AppCompatActivity {
//
//    ActivityHomeMainBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityHomeMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        ImageButton btnFood;
//
//        btnFood=findViewById(R.id.foodlogo);
//
//        String documentID = userDocument.getInstance().getDocumentId();
//        //Toast.makeText(getApplicationContext(),documentID,Toast.LENGTH_LONG).show();
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        replaceFragment(new HomeFragment());
//
//        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.home) {
//                replaceFragment(new HomeFragment());
//                return true;
//
//            } else if (itemId == R.id.profile) {
//                replaceFragment(new ProfileFragment());
//                return true;
//
//            } else if (itemId == R.id.setting) {
//                replaceFragment(new SettingFragment());
//                return true;
//            }
//            return false;
//        });
//
//        btnFood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pressFood(v);
//            }
//        });
//    }
//
////    private void replaceFragment(Fragment fragment){
////        FragmentManager fragmentManager = getSupportFragmentManager();
////        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////        fragmentTransaction.replace(R.id.frame_layout,fragment);
////        fragmentTransaction.commit();
////    }
//
//    public void pressFood (View v){
//        replaceFragment(new FoodFragment());
//    }
//
//    private void replaceFragment(Fragment fragment) {
////        Log.d("HomeMain", "Replacing fragment with: " + fragment.getClass().getSimpleName());
//        Log.i("HomeMain", "Replacing fragment with: " + fragment.getClass().getSimpleName());
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }
//}

package com.example.community;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.community.databinding.ActivityHomeMainBinding;
import com.google.firebase.FirebaseApp;

//import com.example.community.databinding.ActivityHomeMainBinding;

public class HomeMain extends AppCompatActivity {

    private ActivityHomeMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        // Inflate the layout using View Binding
        binding = ActivityHomeMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize the ImageButton
        ImageButton btnFood = findViewById(R.id.foodlogo);
        if (btnFood != null) {
            btnFood.setOnClickListener(this::pressFood);
        } else {
            Log.e("HomeMain", "btnFood is null. Check R.id.foodlogo in your layout.");
        }

        // Replace the initial fragment with HomeFragment
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
                return true;
            } else if (itemId == R.id.setting) {
                replaceFragment(new SettingFragment());
                return true;
            }
            return false;
        });


        // Example of obtaining the document ID (ensure userDocument is properly implemented)
        try {
            String documentID = userDocument.getInstance().getDocumentId();
            Log.i("HomeMain", "Document ID: " + documentID);
        } catch (Exception e) {
            Log.e("HomeMain", "Error fetching document ID: " + e.getMessage());
        }
    }

    /**
     * Replaces the fragment in the frame layout.
     *
     * @param fragment The fragment to display.
     */
    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            Log.e("HomeMain", "Fragment is null. Cannot replace.");
            return;
        }
        Log.i("HomeMain", "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Handles the click on the food button.
     *
     * @param v The view that was clicked.
     */
    public void pressFood(View v) {
        Log.i("HomeMain", "Food button pressed.");
        replaceFragment(new FoodListingFragment());
    }
}
