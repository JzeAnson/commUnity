package com.example.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventsTabFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView noEventsText;


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsCollection = db.collection("events");

    public static EventsTabFragment newInstance(String date, int layoutType) {
        EventsTabFragment fragment = new EventsTabFragment();
        Bundle args = new Bundle();
        args.putString("selected_date", date);
        args.putInt("layout_type", layoutType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar2);
        noEventsText = view.findViewById(R.id.noEventText);

        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve layout type and selected date
        int layoutType = getArguments() != null ? getArguments().getInt("layout_type", 0) : 0;
        String selectedDate = getArguments() != null ? getArguments().getString("selected_date") : "";

        // Fetch events for the selected date
        fetchEventsForSpecificDate(selectedDate);

        // Initialize the adapter and set it to the RecyclerView
        eventAdapter = new EventAdapter(this, eventList, layoutType);
        recyclerView.setAdapter(eventAdapter);


    }

    private void fetchEventsForSpecificDate(String date) {
        progressBar.setVisibility(View.VISIBLE);  // Show progress bar
        eventsCollection.whereEqualTo("date", date)  // Filter events by date
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);  // Hide progress bar after task completes
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

                        // Update UI visibility based on events fetched
                        if (eventList.isEmpty()) {
                            noEventsText.setVisibility(View.VISIBLE);  // Show "No Events" text
                            recyclerView.setVisibility(View.GONE);  // Hide RecyclerView
                        } else {
                            noEventsText.setVisibility(View.GONE);  // Hide "No Events" text
                            recyclerView.setVisibility(View.VISIBLE);  // Show RecyclerView
                        }

                        // Notify adapter to update the list after sorting
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("EventsTabFragment", "Error getting documents.", task.getException());
                        noEventsText.setVisibility(View.VISIBLE);  // Show "No Events" text if error
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }

}
