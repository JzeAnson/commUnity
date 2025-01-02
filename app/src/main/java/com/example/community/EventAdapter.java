package com.example.community;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventList;
    private Context context;
    private int layoutType; // 0 for first format, 1 for second format

    public EventAdapter(Fragment fragment, ArrayList<Event> eventList, int layoutType) {
        this.context = fragment.getContext();
        this.eventList = eventList;
        this.layoutType = layoutType;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutType; // Use the layoutType passed to the adapter
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            // Inflate first layout
            view = LayoutInflater.from(context).inflate(R.layout.item_event_first_format, parent, false);
        } else {
            // Inflate second layout
            view = LayoutInflater.from(context).inflate(R.layout.item_event_second_format, parent, false);
        }

        return new EventViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        if (getItemViewType(position) == 0) {
            // Bind data for the first layout
            holder.textViewTitle.setText(event.getTitle());
            holder.textViewTimeStart.setText(event.getTimeStart());
            holder.textViewTimeEnd.setText(event.getTimeEnd());

            String duration = calculateDuration(event.getTimeStart(), event.getTimeEnd());
            holder.textViewDuration.setText(duration);

        } else {
            // Bind data for the second layout
            holder.textViewTitle.setText(event.getTitle());

            String details = event.getLocation() + ", " + event.getTimeStart() + " >> " + event.getTimeEnd();
            holder.textViewDetails.setText(details);
        }

        // Set common item click listener
        holder.itemView.setOnClickListener(v -> showEventDialog(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void showEventDialog(Event event) {
        Log.d("EventAdapter", "Showing dialog for event: " + event.getTitle());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.item_popup_event_details, null);
        dialogBuilder.setView(dialogView);

        TextView textViewTitle = dialogView.findViewById(R.id.titleD);
        TextView textViewTime = dialogView.findViewById(R.id.textViewTimeD);
        TextView textViewLocation = dialogView.findViewById(R.id.textViewLocationD);
        TextView textViewDescription = dialogView.findViewById(R.id.textViewDescriptionD);
        TextView textViewDate = dialogView.findViewById(R.id.textViewDateD); // New date TextView

        textViewTitle.setText(event.getTitle());
        textViewTime.setText(event.getTimeStart() + " >> " + event.getTimeEnd());
        textViewLocation.setText(event.getLocation());
        textViewDescription.setText(event.getDescription());

        // Format and set the date
        String formattedDate = formatDate(event.getDate()); // event.getDate() is expected to return "yyyy-MM-dd"
        textViewDate.setText(formattedDate);

        AlertDialog dialog = dialogBuilder.create();
        ImageButton buttonDismiss = dialogView.findViewById(R.id.back);
        buttonDismiss.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rectangle_darkblue);
        dialog.show();
    }

    public static String calculateDuration(String startTime, String endTime) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        try {
            Date startDate = format.parse(startTime);
            Date endDate = format.parse(endTime);

            long difference = endDate.getTime() - startDate.getTime();
            long minutes = (difference / (1000 * 60)) % 60;
            long hours = difference / (1000 * 60 * 60);

            return hours + "h " + minutes + "m";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid time";
        }
    }

    private String formatDate(String date) {
        try {
            // Input: "2024-11-26"
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            // Output: "Saturday, 26 November 2024"
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Fallback to original string
        }
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewTimeStart, textViewTimeEnd, textViewDuration, textViewDetails;

        public EventViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            if (viewType == 0) {
                // Initialize views for the first layout
                textViewTitle = itemView.findViewById(R.id.textViewTitle);
                textViewTimeStart = itemView.findViewById(R.id.txtTimeStart);
                textViewTimeEnd = itemView.findViewById(R.id.txtTimeEnd);
                textViewDuration = itemView.findViewById(R.id.txtDuration);

            } else {
                // Initialize views for the second layout
                textViewTitle = itemView.findViewById(R.id.title);
                textViewDetails = itemView.findViewById(R.id.details);
            }
        }
    }
}
