package com.example.community;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private CollectionReference eventsCollection;
    private ImageButton btnCalendar2, btnAdd, btnBack;
    private TextView txtDateView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtDateView = view.findViewById(R.id.txtViewTodayDate);
        String todayDate = setCurrentDate();
        String dayAfterTomorrowDate = calculateDayAfterTomorrow(todayDate);

        // Initialize Firestore
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set up the adapter for the ViewPager2
        EventsPagerAdapter adapter = new EventsPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);

        // Connect the TabLayout with the ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Today");
                    break;
                case 1:
                    tab.setText("Tomorrow");
                    break;
                case 2:
                    tab.setText(dayAfterTomorrowDate);
                    break;
            }
        }).attach();


        btnCalendar2 = view.findViewById(R.id.btnCalendar2);

        btnCalendar2.setOnClickListener(v -> {
            // Create and replace CalendarFragment
            Fragment calendarFragment = new CalendarFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, calendarFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });

        btnBack =  view.findViewById(R.id.btnBack);
        // Back to Event/Calendar home page
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        btnAdd = view.findViewById(R.id.btnADD);

        btnAdd.setOnClickListener(v -> {
            Fragment addEventFragment = new AddEventFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, addEventFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the correct layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    // Method to set the current date in the required format
    private String setCurrentDate() {
        // Format for month name (e.g., "January")
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
        String monthName = sdf.format(new Date()).toUpperCase();  // Get full month name in uppercase

        // Change the pattern for day, month, and year (e.g., "25.12.2024")
        sdf.applyPattern("dd.MM.yyyy");
        String dayMonthYear = sdf.format(new Date());  // Get current date in "dd.MM.yyyy" format

        // Set the formatted date in txtDateView
        String currentDate = monthName + "\n" + dayMonthYear;
        txtDateView.setText(currentDate);

        // Return the day, month, and year string
        return dayMonthYear;
    }

    // Helper method to calculate the "Day After Tomorrow" date
    private String calculateDayAfterTomorrow(String todayDate) {
        try {
            // Parse today's date from "dd.MM.yyyy" format
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Date today = sdf.parse(todayDate);

            // Add 2 days to the date
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 2);

            // Format the new date as "dd MMM" (e.g., "17 NOV")
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
            return outputFormat.format(calendar.getTime()).toUpperCase(); // Return in uppercase
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty string in case of error
        }
    }

//    private void fetchEventsForSpecificDate(String date) {
//        eventsCollection.whereEqualTo("date", date)  // Filter events by date
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        eventList.clear();  // Clear existing data
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Event event = document.toObject(Event.class);
//                            eventList.add(event);
//                        }
//
//                        // Sort the list in ascending order based on timeStart (hh:mm a)
//                        eventList.sort((event1, event2) -> {
//                            try {
//                                // Create SimpleDateFormat with 12-hour format (AM/PM)
//                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
//                                Date date1 = sdf.parse(event1.getTimeStart());
//                                Date date2 = sdf.parse(event2.getTimeStart());
//
//                                // Compare the parsed Date objects
//                                return date1.compareTo(date2);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                                return 0;  // In case of error, do not change order
//                            }
//                        });
//
//                        Log.d("EventsFragment", "Fetched " + eventList.size() + " events.");
//                        eventAdapter.notifyDataSetChanged();  // Notify adapter to update the list
//                    } else {
//                        Log.w("EventsFragment", "Error getting documents.", task.getException());
//                    }
//                });
//    }



}
