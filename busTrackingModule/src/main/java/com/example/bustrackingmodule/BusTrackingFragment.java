package com.example.bustrackingmodule;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusTrackingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusTrackingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    public BusTrackingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusTrackingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusTrackingFragment newInstance(String param1, String param2) {
        BusTrackingFragment fragment = new BusTrackingFragment();
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
            String busline = getArguments().getString("busLine");
            String origin = getArguments().getString("origin");
            String destination = getArguments().getString("destination");

            MapsFragment mapsFragment = (MapsFragment) getChildFragmentManager().findFragmentById(R.id.mapsFragment);
            if (mapsFragment == null) {
                mapsFragment = new MapsFragment();
                Bundle args = new Bundle();
                args.putString("busLine", busline);
                args.putString("origin", origin);
                args.putString("destination", destination);
                mapsFragment.setArguments(args);

                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mapsFragment, mapsFragment)
                        .commitNow();
            }
            else if (mapsFragment != null) {
                Bundle args = new Bundle();
                args.putString("busLine", busline);
                args.putString("origin", origin);
                args.putString("destination", destination);
                mapsFragment.setArguments(args);

                // Add the fragment dynamically if it's not already added
                if (getChildFragmentManager().findFragmentById(R.id.mapsFragment) == null) {
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mapsFragment, mapsFragment)
                            .commitNow();
                }
            }
            else
                Log.d("Bus tracking Fragment","mapsFragment is null");
        }
        else
            Log.d("Bus tracking Fragment","getArgument() is null");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TravelDistanceViewModel travelDistanceViewModel = new ViewModelProvider(requireActivity()).get(TravelDistanceViewModel.class);

        travelDistanceViewModel.getDistanceTravelled().observe(getViewLifecycleOwner(), distance -> {
            if (distance != null) {
                Log.d("BusTrackingFragment", "Distance received: " + distance);

                Button reachedButton = view.findViewById(R.id.btn_reached);

                reachedButton.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(v);
                    Bundle args = new Bundle();
                    args.putInt("travelDistance", distance);
                    navController.navigate(R.id.action_busTrackingFragment_to_carbonEmissionSavedFragment, args);
                });
            }
        });
    }



}