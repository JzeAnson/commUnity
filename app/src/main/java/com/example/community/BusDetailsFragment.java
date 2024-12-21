package com.example.community;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BusDetailsViewModel detailsViewModel;
    private BusEtaViewModel etaViewModel;

    public BusDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusDetailsFragment newInstance(String param1, String param2) {
        BusDetailsFragment fragment = new BusDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_bus_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        detailsViewModel = new ViewModelProvider(requireActivity()).get(BusDetailsViewModel.class);
        etaViewModel = new ViewModelProvider(requireActivity()).get(BusEtaViewModel.class);

        // Observe ETA updates
        etaViewModel.getETA().observe(getViewLifecycleOwner(), eta -> {
            TextView etaTextView = view.findViewById(R.id.eta); // Replace with your TextView ID
            etaTextView.setText(eta);
        });

        // Initialize UI elements
        TextView busPlateTextView = view.findViewById(R.id.bus_number_plate);
        TextView busSpeedTextView = view.findViewById(R.id.current_speed);

        // Observe ViewModel data
        detailsViewModel.getBusPlate().observe(getViewLifecycleOwner(), busPlate -> busPlateTextView.setText(busPlate));
        detailsViewModel.getBusSpeed().observe(getViewLifecycleOwner(), speed -> busSpeedTextView.setText(speed + " km/h"));
    }

}