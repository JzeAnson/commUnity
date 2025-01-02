package com.example.community;

import android.app.Dialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private TextView txtDateView;
    private CalendarView calendar;
    private ImageButton btnBack, btnAdd;
    private FirebaseFirestore firestore;
    private CollectionReference eventsCollection;
    private ArrayList<Event> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        eventsCollection = firestore.collection("events");

        txtDateView = view.findViewById(R.id.txtDateView);
        calendar = view.findViewById(R.id.calendar);
        btnAdd = view.findViewById(R.id.btnADD);

        // Set current date to txtDateView in the required format
        setCurrentDate();

        // Button to go to AddEventFragment
        btnAdd.setOnClickListener(v -> {
            // Create and replace AddEventFragment
            Fragment addEventFragment = new AddEventFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, addEventFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });

        // Handle date change in the CalendarView
        calendar.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Format the selected date
            String selectedDate = getFormattedDateMonth(year, month, dayOfMonth);
            txtDateView.setText(selectedDate); // Set the formatted date in the TextView

            // Fetch events for the selected date and show them in the dialog
            showDialog(year, month, dayOfMonth);
        });

        // Handle "Back" button click
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
         });

        return view;
    }

    // Method to set the current date in the required format
    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
        String monthName = sdf.format(new Date()).toUpperCase();  // Get full month name in uppercase

        sdf.applyPattern("dd.MM.yyyy");  // Change the pattern for day, month, and year
        String dayMonthYear = sdf.format(new Date());  // Get current date in "dd.MM.yyyy" format

        // Set the formatted date in txtDateView
        String currentDate = monthName + "\n" + dayMonthYear;
        txtDateView.setText(currentDate);
    }

    private String getFormattedDate(int year, int month, int dayOfMonth) {
        // Create a new Date object with the selected year, month, and day
        Date selectedDate = new Date(year - 1900, month, dayOfMonth);  // year - 1900 is because Date constructor takes years since 1900

        // Format the full date (day of the week + day.month.year)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()); // Format for date
        String formattedDate = dateFormat.format(selectedDate);

        return formattedDate;  // Return formatted string (e.g., "SUNDAY\n17.11.2024")
    }

    // Method to get the formatted date in "MONTH\nDD.MM.YYYY" format
    private String getFormattedDateMonth(int year, int month, int dayOfMonth) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        String monthName = months[month]; // Get the month name from the array
        return monthName.toUpperCase() + "\n" + dayOfMonth + "." + (month + 1) + "." + year;
    }

    // Method to get the formatted date in "MONTH\nDD.MM.YYYY" format
    private String getFormattedDateDay(int year, int month, int dayOfMonth) {
        // Create a new Date object with the selected year, month, and day
        Date selectedDate = new Date(year - 1900, month, dayOfMonth);  // year - 1900 is because Date constructor takes years since 1900

        // Format for day of the week and full date
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault()); // Full day name (e.g., Sunday)
        String dayOfWeek = dayFormat.format(selectedDate).toUpperCase();  // Convert day to uppercase (e.g., "SUNDAY")

        // Format the full date (day of the week + day.month.year)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()); // Format for date
        String formattedDate = dateFormat.format(selectedDate);

        return dayOfWeek + "\n" + formattedDate;  // Return formatted string (e.g., "SUNDAY\n17.11.2024")
    }

    // Fetch events for the selected date from Firestore
    private void fetchEventsForSelectedDate(String selectedDate) {
        eventsCollection.whereEqualTo("date", selectedDate)  // Filter events by date
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();  // Clear existing data

                        // Add new events to the list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventList.add(event);
                        }

                        // Log the events that were fetched
                        Log.d("EventList", "Fetched " + eventList.size() + " events.");
                        for (Event event : eventList) {
                            Log.d("EventList", "Event: " + event.getTitle());
                        }

                        // Sort the list in ascending order based on timeStart (hh:mm a)
                        eventList.sort((event1, event2) -> {
                            try {
                                // Create SimpleDateFormat with 12-hour format (AM/PM)
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                Date date1 = sdf.parse(event1.getTimeStart());
                                Date date2 = sdf.parse(event2.getTimeStart());

                                // Compare the parsed Date objects
                                return date1.compareTo(date2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;  // In case of error, do not change order
                            }
                        });

                        // Log sorted events
                        Log.d("SortedEventList", "Sorted events by time:");
                        for (Event event : eventList) {
                            Log.d("SortedEventList", "Event: " + event.getTitle() + ", Time: " + event.getTimeStart());
                        }

                        // Notify adapter to update the list after sorting
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("CalendarFragment", "Error getting documents.", task.getException());
                        Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Show the dialog with the selected date's events
    private void showDialog(int year, int month, int dayOfMonth) {
        // Format the selected date
        String selectedDateDay = getFormattedDateDay(year, month, dayOfMonth);
        String selectedDate = getFormattedDate(year, month, dayOfMonth);

        // Fetch events for the selected date from Firestore
        fetchEventsForSelectedDate(selectedDate);

        // Create and show the dialog
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_popup_list_events);  // Assuming this is your dialog layout
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rectangle_lightblue);
        dialog.show();

        // Set the selected date in the dialog
        TextView txtDialogDate = dialog.findViewById(R.id.txtDialogDate);
        if (txtDialogDate != null) {
            txtDialogDate.setText(selectedDateDay);
        }

        // Set up the RecyclerView in the dialog
        RecyclerView recyclerView = dialog.findViewById(R.id.eventSecondFormRecyleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(this, eventList, 1);
        recyclerView.setAdapter(eventAdapter);

        ImageButton buttonDismiss = dialog.findViewById(R.id.btnDismiss);
        buttonDismiss.setOnClickListener(v -> dialog.dismiss());
    }
}