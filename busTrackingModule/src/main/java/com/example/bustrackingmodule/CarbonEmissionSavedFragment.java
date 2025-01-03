package com.example.bustrackingmodule;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarbonEmissionSavedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarbonEmissionSavedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "travelDistance";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int distanceTravelled;
    private String mParam2;

    public CarbonEmissionSavedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarbonEmissionSavedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarbonEmissionSavedFragment newInstance(String param1, String param2) {
        CarbonEmissionSavedFragment fragment = new CarbonEmissionSavedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            distanceTravelled = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.bustrackingmodule.R.layout.fragment_carbon_emission_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Constants
        double carEmission = 120.0; // grams per kilometer
        double busEmission = 30.0; // grams per kilometer per passenger

        // Convert distance from meters to kilometers
        double distanceKm = distanceTravelled / 1000.0;

        // Calculate CO₂ saved
        double co2Saved = distanceKm * (carEmission - busEmission);

        // Display the result
        TextView co2SavedTextView = view.findViewById(R.id.co2_amount);
        co2SavedTextView.setText(String.format("%.2f grams", co2Saved));
    }
}