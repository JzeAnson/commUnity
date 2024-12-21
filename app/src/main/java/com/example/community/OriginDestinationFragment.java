package com.example.community;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OriginDestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OriginDestinationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "busLine";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String busLine;
    private String mParam2;

    public OriginDestinationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OriginDestinationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OriginDestinationFragment newInstance(String param1, String param2) {
        OriginDestinationFragment fragment = new OriginDestinationFragment();
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
            busLine = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_origin_destination, container, false);

        Spinner originSpinner = view.findViewById(R.id.spinner_origin);
        Spinner destinationSpinner = view.findViewById(R.id.spinner_destination);

        // Retrieve the bus line from arguments
        String busLine = getArguments().getString("busLine");

        // Get the bus stops for the selected line
        List<String> busStops = BusStopData.BUS_STOPS.get(busLine);

        // Populate the origin spinner
        ArrayAdapter<String> originAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, busStops.subList(0, busStops.size() - 1));
        originAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        originSpinner.setAdapter(originAdapter);

        // Handle origin spinner selection
        originSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Populate the destination spinner based on the selected origin
                List<String> subsequentStops = busStops.subList(position + 1, busStops.size());
                ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_item, subsequentStops);
                destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                destinationSpinner.setAdapter(destinationAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                destinationSpinner.setAdapter(null); // Clear the destination spinner
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner originSpinner = view.findViewById(R.id.spinner_origin);
        Spinner destinationSpinner = view.findViewById(R.id.spinner_destination);
        Button continueButton = view.findViewById(R.id.continue_button);

        continueButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            String origin = (String) originSpinner.getSelectedItem();
            String destination = (String) destinationSpinner.getSelectedItem();

            // Create a Bundle to pass data to the next fragment
            Bundle args = new Bundle();
            args.putString("origin", origin);
            args.putString("destination", destination);
            args.putString("busLine", busLine);
            navController.navigate(R.id.action_DestOriginDestination_to_busTrackingFragment, args);
        });
    }
}