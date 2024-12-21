package com.example.community;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;

public class RadarChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar_chart);

        // Initialize the radar chart
        RadarChart radarChart = findViewById(R.id.radar_Chart);

        // Create a list of RadarEntry for popularity
        ArrayList<RadarEntry> popularity = new ArrayList<>();
        popularity.add(new RadarEntry(0f, 120f)); // Note: RadarEntry expects two floats (index, value)
        popularity.add(new RadarEntry(1f, 250f));
        popularity.add(new RadarEntry(2f, 1200f));
        popularity.add(new RadarEntry(3f, 700f));
        popularity.add(new RadarEntry(4f, 80f));
        popularity.add(new RadarEntry(5f, 100f));

        // Create a list of RadarEntry for crime rate
        ArrayList<RadarEntry> crime_rate = new ArrayList<>();
        crime_rate.add(new RadarEntry(0f, 120f)); // Note: RadarEntry expects two floats (index, value)
        crime_rate.add(new RadarEntry(1f, 250f));
        crime_rate.add(new RadarEntry(2f, 1000f));
        crime_rate.add(new RadarEntry(3f, 700f));
        crime_rate.add(new RadarEntry(4f, 80f));
        crime_rate.add(new RadarEntry(5f, 100f));

        // Create a RadarDataSet for popularity
        RadarDataSet radarDataSetForPopularity = new RadarDataSet(popularity, "Popularity");
        radarDataSetForPopularity.setColor(Color.MAGENTA);
        radarDataSetForPopularity.setLineWidth(2f);
        radarDataSetForPopularity.setValueTextColor(Color.MAGENTA);
        radarDataSetForPopularity.setValueTextSize(14f);

        // Create a RadarDataSet for crime rate
        /*RadarDataSet radarDataSetForCrimeRate = new RadarDataSet(crime_rate, "Crime Rate");
        radarDataSetForCrimeRate.setColor(Color.GREEN);
        radarDataSetForCrimeRate.setLineWidth(2f);
        radarDataSetForCrimeRate.setValueTextColor(Color.GREEN);
        radarDataSetForCrimeRate.setValueTextSize(14f);
*/
        // Create a RadarData and add both datasets
        ArrayList<IRadarDataSet> dataSets = new ArrayList<>();
        dataSets.add(radarDataSetForPopularity);
        //dataSets.add(radarDataSetForCrimeRate);
        RadarData radarData = new RadarData(dataSets);

        // Set labels for the x-axis
        String[] labels = {"KK12", "PASUM", "DTC", "KL Gate", "PJ Gate", "FSKTM", "KK8"};
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // Set data to the radar chart
        radarChart.setData(radarData);

        // Refresh the chart to display the data
        radarChart.invalidate();
    }
}
