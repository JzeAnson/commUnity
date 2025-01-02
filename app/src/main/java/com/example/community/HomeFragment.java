package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    ImageButton btn_busTracking;
    ImageButton btn_graphSelection;
    private int mySessionId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_busTracking = view.findViewById(R.id.transportlogo);

        btn_busTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloading();
            }
        });

        btn_graphSelection = view.findViewById(R.id.graphlogo);

        btn_graphSelection.setOnClickListener(v -> {
            // Create a new instance of RadarGraphFragment
            Fragment graphSelectionFragment = new GraphSelectionFragment();

            // Perform the fragment transaction
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, graphSelectionFragment);
            transaction.addToBackStack(null); // Optional: allows back navigation
            transaction.commit();
        });

    }
    private void startDownloading() {
        SplitInstallManager splitInstallManager = SplitInstallManagerFactory.create(requireContext());
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule("busTrackingModule").build();
        SplitInstallStateUpdatedListener listener = splitInstallState -> {
            if (splitInstallState.sessionId() == mySessionId) {
                switch (splitInstallState.status()) {
                    case SplitInstallSessionStatus.DOWNLOADING:
                        progressDialog.setMessage("Downloading");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.INSTALLED:
                        Intent intent = new Intent();
                        intent.setClassName(BuildConfig.APPLICATION_ID, "com.example.bustrackingmodule.BusTrackingMainActivity");
                        startActivity(intent);
                    case SplitInstallSessionStatus.CANCELED:
                        Toast.makeText(requireContext(),"Canceled",Toast.LENGTH_SHORT).show();
                        break;
                    case SplitInstallSessionStatus.CANCELING:
                        progressDialog.setMessage("Cancelling");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.DOWNLOADED:
                        break;
                    case SplitInstallSessionStatus.FAILED:
                        break;
                    case SplitInstallSessionStatus.INSTALLING:
                        progressDialog.setMessage("Installing");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.PENDING:
                        progressDialog.setMessage("Pending");
                        progressDialog.show();
                        break;
                    case SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION:
                        break;
                    case SplitInstallSessionStatus.UNKNOWN:
                        break;
                }

            }
        };

        splitInstallManager.registerListener(listener);

        splitInstallManager.startInstall(request)
                .addOnFailureListener(e ->
                        Log.e("HomeFragment", "Error installing module", e)
                )
                .addOnSuccessListener(sessionId -> mySessionId = sessionId);

    }
}