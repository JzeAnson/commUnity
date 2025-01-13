package com.example.community;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
//import com.rometools.rome.feed.synd.SyndEntry;
//import com.rometools.rome.feed.synd.SyndFeed;
//import com.rometools.rome.io.SyndFeedInput;

import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

//import com.rometools.rome.feed.rss.Channel;
//import com.rometools.rome.io.SyndFeedInput;
//import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EmergencyHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private WarningAdapter adapter;
    private static final String BASE_URL = "https://api.met.gov.my/v2.1/";
    private static final String MET_TOKEN = "METToken e069e92eb518b1bd67084d5a1afec4f39329e760"; // Replace with your actual token
    private Handler handler;
    private Runnable refreshRunnable;
    private TextView noAlertsText;
    private ImageButton btnBack, btnEmergencyService;
    private FrameLayout btnNearestFacilities;
    private ProgressBar progressBar;
    // In your MainActivity or Application class
    public static final String CHANNEL_ID = "warnings_channel";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView and TextView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noAlertsText = view.findViewById(R.id.noWarningsText);
        progressBar = view.findViewById(R.id.progressBar);

        // Start fetching warnings
        fetchWarnings();

        // Back to before
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // Button to go to emergency call
        btnEmergencyService = view.findViewById(R.id.btnEmergencyService);
        btnEmergencyService.setOnClickListener(v -> {
            Fragment emergencyNoticeFragment = new EmergencyNoticeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, emergencyNoticeFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });

        // to nearest map
        btnNearestFacilities = view.findViewById(R.id.btnNearestFacilities);
        btnNearestFacilities.setOnClickListener(v -> {
            Fragment nearestFacMapFragment = new NearestFacMapFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, nearestFacMapFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });

        // Schedule periodic refresh
        if (handler != null && refreshRunnable != null) {
            handler.post(refreshRunnable);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency_home, container, false);
    }

    private void fetchWarnings() {
        Log.d("EmergencyHomeFragment", "fetchWarnings() called at: " + System.currentTimeMillis());
        // Show progress bar while loading data
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        String currentDate = getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        String[] warningTypes = {"QUAKETSUNAMI", "WINDSEA", "THUNDERSTORM", "RAIN"};
        List<WarningResponse.Warning> allWarnings = new ArrayList<>();
        String selectedLanguage = "en";
        final int[] completedRequests = {0};

        // Immediate data fetch
        fetchWarningsForTypes(apiService, warningTypes, currentDate, endDate, allWarnings, completedRequests, selectedLanguage);

        // Periodic refresh
        handler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d("EmergencyHomeFragment", "Refreshing warnings at: " + System.currentTimeMillis());
                fetchWarningsForTypes(apiService, warningTypes, currentDate, endDate, allWarnings, completedRequests, selectedLanguage);
                handler.postDelayed(this, 300000); // Refresh every 5 minutes
            }
        };
        handler.post(refreshRunnable);
    }

    private void fetchWarningsForTypes(ApiService apiService, String[] warningTypes, String currentDate, String endDate,
                                       List<WarningResponse.Warning> allWarnings, int[] completedRequests, String selectedLanguage) {
        allWarnings.clear();
        completedRequests[0] = 0;

        for (String warningType : warningTypes) {
            apiService.getWarnings(MET_TOKEN, "WARNING", warningType, currentDate, endDate)
                    .enqueue(new Callback<WarningResponse>() {
                        @Override
                        public void onResponse(Call<WarningResponse> call, Response<WarningResponse> response) {
                            if (!isAdded()) {
                                Log.e("EmergencyHomeFragment", "Fragment not attached. Skipping response handling.");
                                return;
                            }

                            if (response.isSuccessful()) {
                                Log.d("EmergencyHomeFragment", "Received response for type: " + warningType);
                                // Hide progress bar once data is received
                                progressBar.setVisibility(View.GONE);
                                WarningResponse warningResponse = response.body();
                                List<WarningResponse.Warning> warnings = warningResponse.getResults();

                                if (warnings != null) {
                                    allWarnings.addAll(warnings);
                                }

                                completedRequests[0]++;
                                if (completedRequests[0] == warningTypes.length) {
                                    Log.d("EmergencyHomeFragment", "All warning types processed");
                                    updateRecyclerView(allWarnings, selectedLanguage);
                                }
                            } else {
                                Log.e("EmergencyHomeFragment", "Error response code: " + response.code());
                                showError("Error: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<WarningResponse> call, Throwable t) {
                            // Hide progress bar once data is received
                            progressBar.setVisibility(View.GONE);
                            Log.e("EmergencyHomeFragment", "Failed to fetch warnings: " + t.getMessage());
                            showError(t.getMessage());
                        }
                    });
        }
    }

    private void updateRecyclerView(List<WarningResponse.Warning> allWarnings, String selectedLanguage) {
        if (!isAdded()) {
            Log.e("EmergencyHomeFragment", "Fragment not attached. Skipping UI update.");
            return;
        }

        List<WarningResponse.Warning> latestWarnings = getLatestWarningsByTypeAndLocation(allWarnings, "Terengganu");

        if (adapter == null) {
            adapter = new WarningAdapter(latestWarnings, selectedLanguage);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(latestWarnings);
        }

        noAlertsText.setVisibility(latestWarnings.isEmpty() ? View.VISIBLE : View.GONE);

        // Set up a set to keep track of which warning types have already triggered a notification
        Set<String> notifiedWarnings = new HashSet<>();

        for (WarningResponse.Warning warning : latestWarnings) {
            String warningType = warning.getDatatype();

            // Check if this warning type has already been notified
            if (!notifiedWarnings.contains(warningType)) {
                // Send a notification if it hasn't been notified yet
                showNotification("ALERT!", warning.getValue().getText().getEn().getWarning());
                notifiedWarnings.add(warningType);  // Mark this warning type as notified
            }
        }
    }


    private List<WarningResponse.Warning> getLatestWarningsByTypeAndLocation(List<WarningResponse.Warning> allWarnings, String location) {
        Map<String, WarningResponse.Warning> latestWarningsMap = new HashMap<>();

        for (WarningResponse.Warning warning : allWarnings) {
            String warningType = warning.getDatatype();
            String timestamp = warning.getAttributes().getTimestamp();
            String warningText = warning.getValue().getText().getEn().getWarning();

            if (warningText != null && warningText.contains(location)) {
                if (!latestWarningsMap.containsKey(warningType) || timestamp.compareTo(latestWarningsMap.get(warningType).getAttributes().getTimestamp()) > 0) {
                    latestWarningsMap.put(warningType, warning);
                }
            }
        }

        return new ArrayList<>(latestWarningsMap.values());
    }
    private void showNotification(String title, String message) {
        if (!isAdded()) {
            Log.e("EmergencyHomeFragment", "Fragment not attached. Skipping notification.");
            return;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        Intent intent = new Intent(requireContext(), EmergencyHomeFragment.class); // Replace with your target activity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_alert, "View Details", pendingIntent);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        } else {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    private void showError(String message) {
        if (!isAdded()) {
            Log.e("EmergencyHomeFragment", "Fragment not attached. Skipping error display.");
            return;
        }
        Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
        noAlertsText.setVisibility(View.VISIBLE);
        noAlertsText.setText("Failed to load warnings. Please try again.");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable); // Remove any existing instances
            fetchWarnings(); // Immediately fetch the data
            handler.post(refreshRunnable); // Restart the periodic refresh
        }
    }


}


