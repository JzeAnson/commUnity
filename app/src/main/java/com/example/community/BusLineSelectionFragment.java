package com.example.community;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusLineSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusLineSelectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BusLineSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusLineSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusLineSelectionFragment newInstance(String param1, String param2) {
        BusLineSelectionFragment fragment = new BusLineSelectionFragment();
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
        return inflater.inflate(R.layout.fragment_bus_line_selection, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BusLineViewModel buslineViewModel = new ViewModelProvider(requireActivity()).get(BusLineViewModel.class);

        // Button for T789 bus line
        ImageButton t789Button = view.findViewById(R.id.t789_button);
        ImageButton t815Button = view.findViewById(R.id.t815_button);

        t789Button.setOnClickListener(v -> {
            //buslineViewModel.setBusLine("T789");
            NavController navController = Navigation.findNavController(v);
            Bundle args = new Bundle();
            args.putString("busLine", "T789");
            navController.navigate(R.id.action_busLineSelectionFragment_to_originDestinationFragment, args);
        });

        t815Button.setOnClickListener(v -> {
            //buslineViewModel.setBusLine("T815");
            NavController navController = Navigation.findNavController(v);
            Bundle args = new Bundle();
            args.putString("busLine", "T815");
            navController.navigate(R.id.action_busLineSelectionFragment_to_originDestinationFragment, args);
        });
    }



}