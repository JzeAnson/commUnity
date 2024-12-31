package com.example.community;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class EmergencyNoticeFragment extends Fragment {

    private Button btnContinueEmergency;
    private ImageButton btnBack2;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back to before
        btnBack2 = view.findViewById(R.id.btnBack2);
        btnBack2.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        btnContinueEmergency = view.findViewById(R.id.btnContinueEmergency);

        btnContinueEmergency.setOnClickListener(v -> onEmergencyButtonClick());
    }

    private void onEmergencyButtonClick() {
        // Hardcoded emergency number (e.g., 911)
        String emergencyNumber = "123";
        Uri emergencyUri = Uri.parse("tel:" + emergencyNumber);

        // Check if CALL_PHONE permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, emergencyUri);
            startIntent(intent);
        } else {
            // Request CALL_PHONE permission
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) { // Request code for CALL_PHONE
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the emergency call
                onEmergencyButtonClick();
            } else {
                // Permission denied, show a message
                Log.e("EmergencyNotice", "CALL_PHONE permission denied");
            }
        }
    }

    private void startIntent(Intent intent) {
        try {
            startActivity(intent);
        } catch (SecurityException e) {
            Log.e("EmergencyNotice", "startIntent: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency_notice, container, false);
    }
}
