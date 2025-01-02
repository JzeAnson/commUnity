package com.example.community;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.community.R;
import com.example.community.WarningResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WarningAdapter extends RecyclerView.Adapter<WarningAdapter.WarningViewHolder> {

    private List<WarningResponse.Warning> warnings;
    private String language; // This will store the selected language, e.g., "en" or "ms"

    public WarningAdapter(List<WarningResponse.Warning> warnings, String language) {
        this.warnings = warnings;
        this.language = language; // Set the language (English or Malay)
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        WarningResponse.Warning warning = warnings.get(position);

        // Log the warning content
        Log.d("WarningResponse", "Date: " + warning.getDate());
        Log.d("WarningResponse", "Datatype: " + warning.getDatatype());
        Log.d("WarningResponse", "Heading EN: " + warning.getValue().getHeading().getEn());
        Log.d("WarningResponse", "Warning EN: " + warning.getValue().getText().getEn().getWarning());
        Log.d("WarningResponse", "Warning MS: " + warning.getValue().getText().getMs().getWarning());

        Log.d("Warning", "Valid from: " + warning.getAttributes().getValid_from());
        Log.d("Warning", "Valid to: " + warning.getAttributes().getValid_to());

        // Set the heading text
        holder.heading.setText(warning.getValue().getHeading().getEn());  // Or getMs() based on the language

        // Set the warning message text
        holder.description.setText(getWarningMessage(warning));  // Use the getWarningMessage method

//        // Display datatype (e.g., "RAIN")
//        holder.datatype.setText(warning.getDatatype());


        String formatedDateFrom = warning.getAttributes().getValid_from();
        String formattedDateTO = warning.getAttributes().getValid_to();



        // Display valid_from and valid_to
        holder.validity.setText("Starting: " + formatDate(formatedDateFrom) + "\nExpected to End: " +formatDate(formattedDateTO) );
    }

    // Method to format the date string
    private String formatDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // Input format (ISO 8601)
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy"); // Desired output format

        try {
            Date date = inputFormat.parse(dateStr); // Parse the original date string
            return outputFormat.format(date); // Format it to the desired format
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // Return the original date if parsing fails
        }
    }

    // Add this method to update the dataset
    public void updateData(List<WarningResponse.Warning> newWarnings) {
        this.warnings.clear();
        this.warnings.addAll(newWarnings);
        notifyDataSetChanged(); // Notify RecyclerView to refresh
    }


    @Override
    public int getItemCount() {
        return warnings.size();
    }

    // Helper method to get the warning message based on language
    private String getWarningMessage(WarningResponse.Warning warning) {
        if ("ms".equals(language)) {
            // Get the warning message in Malay
            return warning.getValue().getText().getEn().getWarning();
        } else {
            // Get the warning message in English
            return warning.getValue().getText().getEn().getWarning();
        }
    }

    public static class WarningViewHolder extends RecyclerView.ViewHolder {

        TextView heading, description, datatype, validity;

        public WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.warningHeading);
            description = itemView.findViewById(R.id.warningDescription);
//            datatype = itemView.findViewById(R.id.warningDatatype); // Add this
            validity = itemView.findViewById(R.id.warningValidity);   // Add this
        }
    }

}
