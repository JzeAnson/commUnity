package com.example.community;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import com.example.community.databinding.ActivityHomeMainBinding;

public class HomeMain extends AppCompatActivity {

    ActivityHomeMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String documentID = userDocument.getInstance().getDocumentId();
        //Toast.makeText(getApplicationContext(),documentID,Toast.LENGTH_LONG).show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

            } else if (itemId==R.id.foodlogo){
                replaceFragment(new FoodFragment());
                return true;
            }
            return false;
        });
    }

//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout,fragment);
//        fragmentTransaction.commit();
//    }

    private void replaceFragment(Fragment fragment) {
//        Log.d("HomeMain", "Replacing fragment with: " + fragment.getClass().getSimpleName());
        Log.i("HomeMain", "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}