package com.example.community;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RadarGraphFragment extends Fragment {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private RadarChart radarChart;
    private ImageButton btnDownload;
    private String category;
    private Spinner spinnerTimeline;
    private String selectedTimeline = "January"; // Default timeline

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("graphCategory");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_radar_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radarChart = view.findViewById(R.id.radar_graph);
        btnDownload = view.findViewById(R.id.btn_download_graph);
        spinnerTimeline = view.findViewById(R.id.spinner_timeline);

        // Populate spinner with month options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getMonthsList()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeline.setAdapter(adapter);

        // Set up spinner listener
        spinnerTimeline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeline = parent.getItemAtPosition(position).toString();
                updateChartForTimeline(selectedTimeline);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        setupRadarChartForCategory(category, selectedTimeline);
        // Set up the download button
        btnDownload.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                saveChartToGallery();
            } else {
                requestStoragePermission();
            }
        });

        ImageButton btn_RadarGraphBack = view.findViewById(R.id.button_radargraphback);
        btn_RadarGraphBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private List<String> getMonthsList() {
        List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        return months;
    }
    private void updateChartForTimeline(String timeline) {
        // Re-fetch data based on the selected timeline
        setupRadarChartForCategory(category, timeline);
    }

    private void setupRadarChartForCategory(String category, String timeline) {
        ArrayList<RadarEntry> entries = new ArrayList<>();
        String[] labels = new String[0];
        String chartLabel = "";

        // Fetch data based on category and timeline
        switch (category) {
            case "Public Transport Usability":
                switch (timeline) {
                    case "January":
                        entries.add(new RadarEntry(100f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(250f));
                        break;
                    case "February":
                        entries.add(new RadarEntry(120f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(140f));
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(240f));
                        break;
                    case "March":
                        entries.add(new RadarEntry(130f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(135f));
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(230f));
                        break;
                    case "April":
                        entries.add(new RadarEntry(110f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(130f));
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(220f));
                        break;
                    case "May":
                        entries.add(new RadarEntry(140f));
                        entries.add(new RadarEntry(250f));
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(270f));
                        break;
                    case "June":
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(260f));
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(280f));
                        break;
                    case "July":
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(270f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(290f));
                        break;
                    case "August":
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(280f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(300f));
                        break;
                    case "September":
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(290f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(310f));
                        break;
                    case "October":
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(300f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(240f));
                        entries.add(new RadarEntry(320f));
                        break;
                    case "November":
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(310f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(250f));
                        entries.add(new RadarEntry(330f));
                        break;
                    case "December":
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(155f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(260f));
                        break;
                }
                chartLabel = "Public Transport Usability (" + timeline + ")";
                labels = new String[]{"Ease of Use", "Availability", "Punctuality", "Coverage", "Comfort"};
                break;

            case "Popularity":
                switch (timeline) {
                    case "January":
                        entries.add(new RadarEntry(300f));
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(500f));
                        entries.add(new RadarEntry(700f));
                        entries.add(new RadarEntry(600f));
                        break;
                    case "February":
                        entries.add(new RadarEntry(310f));
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(520f));
                        entries.add(new RadarEntry(720f));
                        entries.add(new RadarEntry(620f));
                        break;
                    case "March":
                        entries.add(new RadarEntry(320f));
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(540f));
                        entries.add(new RadarEntry(740f));
                        entries.add(new RadarEntry(640f));
                        break;
                    case "April":
                        entries.add(new RadarEntry(330f));
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(560f));
                        entries.add(new RadarEntry(760f));
                        entries.add(new RadarEntry(660f));
                        break;
                    case "May":
                        entries.add(new RadarEntry(340f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(580f));
                        entries.add(new RadarEntry(780f));
                        entries.add(new RadarEntry(680f));
                        break;
                    case "June":
                        entries.add(new RadarEntry(350f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(600f));
                        entries.add(new RadarEntry(800f));
                        entries.add(new RadarEntry(700f));
                        break;
                    case "July":
                        entries.add(new RadarEntry(360f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(620f));
                        entries.add(new RadarEntry(820f));
                        entries.add(new RadarEntry(720f));
                        break;
                    case "August":
                        entries.add(new RadarEntry(370f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(640f));
                        entries.add(new RadarEntry(840f));
                        entries.add(new RadarEntry(740f));
                        break;
                    case "September":
                        entries.add(new RadarEntry(380f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(660f));
                        entries.add(new RadarEntry(860f));
                        entries.add(new RadarEntry(760f));
                        break;
                    case "October":
                        entries.add(new RadarEntry(390f));
                        entries.add(new RadarEntry(240f));
                        entries.add(new RadarEntry(680f));
                        entries.add(new RadarEntry(880f));
                        entries.add(new RadarEntry(780f));
                        break;
                    case "November":
                        entries.add(new RadarEntry(400f));
                        entries.add(new RadarEntry(250f));
                        entries.add(new RadarEntry(700f));
                        entries.add(new RadarEntry(900f));
                        entries.add(new RadarEntry(800f));
                        break;
                    case "December":
                        entries.add(new RadarEntry(410f));
                        entries.add(new RadarEntry(260f));
                        entries.add(new RadarEntry(720f));
                        entries.add(new RadarEntry(920f));
                        entries.add(new RadarEntry(820f));
                        break;
                }
                chartLabel = "Popularity (" + timeline + ")";
                labels = new String[]{"KK12", "PASUM", "DTC", "KL Gate", "PJ Gate"};
                break;

            case "Green Initiative Execution":
                switch (timeline) {
                    case "January":
                        entries.add(new RadarEntry(100f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(250f));
                        break;
                    case "February":
                        entries.add(new RadarEntry(120f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(140f));
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(240f));
                        break;
                    case "March":
                        entries.add(new RadarEntry(130f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(135f));
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(230f));
                        break;
                    case "April":
                        entries.add(new RadarEntry(110f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(130f));
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(220f));
                        break;
                    case "May":
                        entries.add(new RadarEntry(140f));
                        entries.add(new RadarEntry(250f));
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(270f));
                        break;
                    case "June":
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(260f));
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(280f));
                        break;
                    case "July":
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(270f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(290f));
                        break;
                    case "August":
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(280f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(300f));
                        break;
                    case "September":
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(290f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(310f));
                        break;
                    case "October":
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(300f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(240f));
                        entries.add(new RadarEntry(320f));
                        break;
                    case "November":
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(310f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(250f));
                        entries.add(new RadarEntry(330f));
                        break;
                    case "December":
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(155f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(260f));
                        break;
                }
                chartLabel = "Green Initiative Execution (" + timeline + ")";
                labels = new String[]{"Ease of Use", "Availability", "Punctuality", "Coverage", "Comfort"};
                break;

            case "Food Waste Prevention":
                switch (timeline) {
                    case "January":
                        entries.add(new RadarEntry(100f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(250f));
                        break;
                    case "February":
                        entries.add(new RadarEntry(120f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(140f));
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(240f));
                        break;
                    case "March":
                        entries.add(new RadarEntry(130f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(135f));
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(230f));
                        break;
                    case "April":
                        entries.add(new RadarEntry(110f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(130f));
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(220f));
                        break;
                    case "May":
                        entries.add(new RadarEntry(140f));
                        entries.add(new RadarEntry(250f));
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(270f));
                        break;
                    case "June":
                        entries.add(new RadarEntry(150f));
                        entries.add(new RadarEntry(260f));
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(280f));
                        break;
                    case "July":
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(270f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(290f));
                        break;
                    case "August":
                        entries.add(new RadarEntry(170f));
                        entries.add(new RadarEntry(280f));
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(300f));
                        break;
                    case "September":
                        entries.add(new RadarEntry(180f));
                        entries.add(new RadarEntry(290f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(310f));
                        break;
                    case "October":
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(300f));
                        entries.add(new RadarEntry(220f));
                        entries.add(new RadarEntry(240f));
                        entries.add(new RadarEntry(320f));
                        break;
                    case "November":
                        entries.add(new RadarEntry(200f));
                        entries.add(new RadarEntry(310f));
                        entries.add(new RadarEntry(230f));
                        entries.add(new RadarEntry(250f));
                        entries.add(new RadarEntry(330f));
                        break;
                    case "December":
                        entries.add(new RadarEntry(160f));
                        entries.add(new RadarEntry(210f));
                        entries.add(new RadarEntry(155f));
                        entries.add(new RadarEntry(190f));
                        entries.add(new RadarEntry(260f));
                        break;
                }
                chartLabel = "Food Waste Prevention (" + timeline + ")";
                labels = new String[]{"Ease of Use", "Availability", "Punctuality", "Coverage", "Comfort"};
                break;

        }


        RadarDataSet radarDataSet = new RadarDataSet(entries, chartLabel);
        if (category.equals("Public Transport Usability")){
            radarDataSet.setColor(Color.BLUE);
            radarDataSet.setLineWidth(2f);
            radarDataSet.setValueTextColor(Color.BLUE);
            radarDataSet.setValueTextSize(14f);
        }
        else if (category.equals("Popularity")){
            radarDataSet.setColor(Color.RED);
            radarDataSet.setLineWidth(2f);
            radarDataSet.setValueTextColor(Color.RED);
            radarDataSet.setValueTextSize(14f);
        }
        else if (category.equals("Green Initiative Execution")){
            radarDataSet.setColor(Color.GREEN);
            radarDataSet.setLineWidth(2f);
            radarDataSet.setValueTextColor(Color.GREEN);
            radarDataSet.setValueTextSize(14f);
        }
        else {
            radarDataSet.setColor(Color.MAGENTA);
            radarDataSet.setLineWidth(2f);
            radarDataSet.setValueTextColor(Color.MAGENTA);
            radarDataSet.setValueTextSize(14f);
        }

        ArrayList<IRadarDataSet> dataSets = new ArrayList<>();
        dataSets.add(radarDataSet);
        RadarData radarData = new RadarData(dataSets);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        radarChart.setData(radarData);
        radarChart.invalidate();
    }


    private boolean checkStoragePermission() {
        // Permissions are not required for Android Q (API 29) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        // Check for WRITE_EXTERNAL_STORAGE for Android 9 and below
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
    }

    private void saveChartToGallery() {
        Bitmap chartBitmap = radarChart.getChartBitmap();
        String filename = "RadarChart_" + System.currentTimeMillis() + ".png";

        try {
            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/RadarCharts");

                fos = requireContext().getContentResolver().openOutputStream(
                        requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));
            } else {
                fos = requireContext().getContentResolver().openOutputStream(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }

            if (fos != null) {
                chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Toast.makeText(requireContext(), "Chart saved to gallery!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error saving chart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveChartToGallery();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Unable to save chart.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
