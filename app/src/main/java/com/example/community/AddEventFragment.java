package com.example.community;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AddEventFragment extends Fragment {

    private EditText editTextDate, editTextTimeStart, editTextTimeEnd, editTextTitle, editTextLocation, editTextDescription;
    private Button buttonSaveEvent;
    private ImageButton btnBack;
    private FirebaseFirestore db; // Firestore instance

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find views
        editTextDate = view.findViewById(R.id.edtTxtDate);
        editTextTimeStart = view.findViewById(R.id.edtTxtTimeStart);
        editTextTimeEnd = view.findViewById(R.id.edtTxtTimeEnd);
        editTextTitle = view.findViewById(R.id.edtTxtTitle);
        editTextLocation = view.findViewById(R.id.edtTxtLocation);
        editTextDescription = view.findViewById(R.id.edtTxtDescription);
        buttonSaveEvent = view.findViewById(R.id.btnDone);

        // Date Picker
        editTextDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        (view1, selectedYear, selectedMonth, selectedDay) -> {
                            // Format the day and month to always have two digits
                            String formattedDate = String.format("%02d.%02d.%04d", selectedDay, selectedMonth + 1, selectedYear);
                            editTextDate.setText(formattedDate);
                        }, year, month, day);

                datePickerDialog.show();
                return true;
            }
            return false;
        });


        // Time Picker for Start Time
        editTextTimeStart.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showTimePicker(editTextTimeStart);  // Reusing the method for start time
                return true;
            }
            return false;
        });

        // Time Picker for End Time
        editTextTimeEnd.setOnTouchListener((view1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showTimePicker(editTextTimeEnd);  // Reusing the method for end time
                return true;
            }
            return false;
        });

        // Save Event
        buttonSaveEvent.setOnClickListener(view1 -> {
            String title = editTextTitle.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String timeStart = editTextTimeStart.getText().toString().trim();
            String timeEnd = editTextTimeEnd.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty() || timeStart.isEmpty() || timeEnd.isEmpty() || location.isEmpty()|| description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new Event object
            Event event = new Event(title, date, timeStart, timeEnd, location, description);

            // Save event to Firestore
            saveEventToFirestore(event);

            getFragmentManager().popBackStack();

        });

        // Back to Event/Calendar home page
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Navigate back using Navigation Component
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void showTimePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(),
                (view, hourOfDay, minuteOfHour) -> {
                    // Format time to 12-hour format with AM/PM
                    String period = (hourOfDay < 12) ? "AM" : "PM";
                    int hourIn12HrFormat = hourOfDay % 12;
                    if (hourIn12HrFormat == 0) hourIn12HrFormat = 12;

                    String formattedTime = String.format("%02d:%02d %s", hourIn12HrFormat, minuteOfHour, period);
                    editText.setText(formattedTime);
                },
                hour, minute, false); // false for 12-hour format

        timePickerDialog.show();
    }

    private void saveEventToFirestore(Event event) {
        // Add the event to Firestore
        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AddEventFragment", "Event added with ID: " + documentReference.getId());
                    // Ensure the fragment is attached before showing the toast
                    if (getContext() != null && isAdded()) {
                        Toast.makeText(getContext(), "Event saved successfully", Toast.LENGTH_SHORT).show();
                    }
                    // Go back to the previous fragment after saving the event
                    if (getActivity() != null && isAdded()) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("AddEventFragment", "Error adding event", e);
                    // Ensure the fragment is attached before showing the toast
                    if (getContext() != null && isAdded()) {
                        Toast.makeText(getContext(), "Error saving event", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
