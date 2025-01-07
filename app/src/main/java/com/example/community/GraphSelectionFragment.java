package com.example.community;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphSelectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GraphSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphSelectionFragment newInstance(String param1, String param2) {
        GraphSelectionFragment fragment = new GraphSelectionFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnPublicTransport = view.findViewById(R.id.transportusability_button);

        btnPublicTransport.setOnClickListener(v -> {
            // Create a new instance of RadarGraphFragment
            Fragment radarGraphFragment = new RadarGraphFragment();

            // Pass arguments to the fragment
            Bundle args = new Bundle();
            args.putString("graphCategory", "Public Transport Usability");
            radarGraphFragment.setArguments(args);

            // Perform the fragment transaction
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, radarGraphFragment);
            transaction.addToBackStack(null); // Optional: allows back navigation
            transaction.commit();
        });
        ImageButton btnPopularity = view.findViewById(R.id.popularity_button);

        btnPopularity.setOnClickListener(v -> {
            // Create a new instance of RadarGraphFragment
            Fragment radarGraphFragment = new RadarGraphFragment();

            // Pass arguments to the fragment
            Bundle args = new Bundle();
            args.putString("graphCategory", "Popularity");
            radarGraphFragment.setArguments(args);

            // Perform the fragment transaction
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, radarGraphFragment);
            transaction.addToBackStack(null); // Optional: allows back navigation
            transaction.commit();
        });

        ImageButton btnGreenInitiative = view.findViewById(R.id.greeninitiative_button);

        btnGreenInitiative.setOnClickListener(v -> {
            // Create a new instance of RadarGraphFragment
            Fragment radarGraphFragment = new RadarGraphFragment();

            // Pass arguments to the fragment
            Bundle args = new Bundle();
            args.putString("graphCategory", "Green Initiative Execution");
            radarGraphFragment.setArguments(args);

            // Perform the fragment transaction
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, radarGraphFragment);
            transaction.addToBackStack(null); // Optional: allows back navigation
            transaction.commit();
        });

        ImageButton btnFoodWaste = view.findViewById(R.id.foodprevention_button);

        btnFoodWaste.setOnClickListener(v -> {
            // Create a new instance of RadarGraphFragment
            Fragment radarGraphFragment = new RadarGraphFragment();

            // Pass arguments to the fragment
            Bundle args = new Bundle();
            args.putString("graphCategory", "Food Waste Prevention");
            radarGraphFragment.setArguments(args);

            // Perform the fragment transaction
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, radarGraphFragment);
            transaction.addToBackStack(null); // Optional: allows back navigation
            transaction.commit();
        });

        ImageButton btn_GraphSelectionBack = view.findViewById(R.id.btn_back);

        btn_GraphSelectionBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
}