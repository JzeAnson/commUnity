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
import android.widget.Button;
import android.widget.ImageButton;
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

public class RadarGraphFragment extends Fragment {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private RadarChart radarChart;
    private ImageButton btnDownload;

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

        // Initialize the radar chart
        setupRadarChart();

        // Set up the download button
        btnDownload.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                saveChartToGallery();
            } else {
                requestStoragePermission();
            }
        });
    }

    private void setupRadarChart() {
        // Create a list of RadarEntry for the chart
        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(120f));
        entries.add(new RadarEntry(250f));
        entries.add(new RadarEntry(1200f));
        entries.add(new RadarEntry(700f));
        entries.add(new RadarEntry(80f));
        entries.add(new RadarEntry(100f));

        // Create a RadarDataSet
        RadarDataSet radarDataSet = new RadarDataSet(entries, "Popularity");
        radarDataSet.setColor(Color.MAGENTA);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(Color.MAGENTA);
        radarDataSet.setValueTextSize(14f);

        // Create RadarData
        ArrayList<IRadarDataSet> dataSets = new ArrayList<>();
        dataSets.add(radarDataSet);
        RadarData radarData = new RadarData(dataSets);

        // Set labels for the x-axis
        String[] labels = {"KK12", "PASUM", "DTC", "KL Gate", "PJ Gate", "FSKTM"};
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // Set data to the radar chart
        radarChart.setData(radarData);

        // Refresh the chart to display the data
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
