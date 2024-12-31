package com.example.community;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


public class HomeFragment extends Fragment {


    private ImageButton btnCalendar, btnEmergency;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // Always call the superclass method.

        btnCalendar = view.findViewById(R.id.calendarlogo);

        btnCalendar.setOnClickListener(v -> {
            Fragment eventsFragment = new EventsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, eventsFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });

        btnEmergency = view.findViewById(R.id.reportlogo);

        btnEmergency.setOnClickListener(v -> {
            Fragment emergencyHomeFragment = new EmergencyHomeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, emergencyHomeFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });


    }

}