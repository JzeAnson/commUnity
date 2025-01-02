package com.example.community;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearestFacMapFragment extends Fragment implements OnMapReadyCallback,
        OnMyLocationButtonClickListener, OnMyLocationClickListener {

    ConstraintLayout btnRoute;
    private FrameLayout btnFireStation, btnHospital, btnPoliceStation;
    private Button resetButton;
    private FloatingActionButton btnBack;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLat = 0, currentLong = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_nearest_fac_map, container, false);


        Button resetButton = rootView.findViewById(R.id.btn_reset_panel);
        resetButton.setOnClickListener(v -> showDefaultLayout());



        // Initialize UI elements
        btnFireStation = rootView.findViewById(R.id.btn_fire_station);
        btnHospital = rootView.findViewById(R.id.btn_hospital);
        btnPoliceStation = rootView.findViewById(R.id.btn_police_station);


        // Back to before
        btnBack = rootView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // Initialize map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        // Set up click listeners for the buttons
        setButtonListeners();

        // Initialize fused location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Check for location permission and request if necessary
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }




        return rootView;
    }

    private void setButtonListeners() {
        btnFireStation.setOnClickListener(v -> {
            v.setEnabled(false); // Avoid double clicks
            findFacilities("fire_station");
            v.setEnabled(true); // Re-enable after processing
        });

        btnHospital.setOnClickListener(v -> {
            v.setEnabled(false); // Avoid double clicks
            findFacilities("hospital");
            v.setEnabled(true); // Re-enable after processing
        });

        btnPoliceStation.setOnClickListener(v -> {
            v.setEnabled(false); // Avoid double clicks
            findFacilities("police");
            v.setEnabled(true); // Re-enable after processing
        });
    }

    private void findFacilities(String facilityType) {
        // Collapse the sliding panel when a facility button is clicked
        resetSlidingPanel();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + currentLat + "," + currentLong +
                "&radius=5000&sensor=true&type=" + facilityType +
                "&key=" + getResources().getString(R.string.map_api_key);
        new PlaceTask().execute(url); // Execute AsyncTask to fetch and display markers
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;  // Permission is not granted, handle appropriately
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLong = location.getLongitude();

                // Ensure the map is ready before using it
                if (map != null) {
                    // Enable My Location Layer to show the user's location on the map
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                    }

                    // Move camera to current location
                    LatLng currentLocation = new LatLng(currentLat, currentLong);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                }
            }
        });
    }

    private void getETA(LatLng destination) {
        String origin = currentLat + "," + currentLong; // Current location
        String dest = destination.latitude + "," + destination.longitude; // Selected marker location

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin +
                "&destination=" + dest +
                "&key=" + getResources().getString(R.string.map_api_key);



        new GetETA().execute(url);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Enable My Location Layer to show the user's location on the map
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Handle MyLocation button click if needed
        return false;  // Return false to use the default behavior for the location button.
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        // Handle MyLocation click if needed
    }

    private void showDefaultLayout() {
        View facilitiesPanel = getView().findViewById(R.id.layout_facilities_panel);
        View markerDetailsPanel = getView().findViewById(R.id.layout_facilities_details);

        if (facilitiesPanel != null) {
            facilitiesPanel.setVisibility(View.VISIBLE);
        }
        if (markerDetailsPanel != null) {
            markerDetailsPanel.setVisibility(View.GONE);
        }

        // Configure sliding panel for default layout
        configureSlidingPanelForDefault();

        // Reset the camera to the user's current location
        LatLng currentLocation = new LatLng(currentLat, currentLong);
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
        }
    }


    private void configureSlidingPanelForDefault() {
        SlidingUpPanelLayout slidingLayout = getView().findViewById(R.id.sliding_layout);
        if (slidingLayout != null) {
            // Set the panel height for the default layout
            slidingLayout.setPanelHeight((int) getResources().getDimension(R.dimen.default_panel_height));

            // Disable sliding up for the default layout
            slidingLayout.setTouchEnabled(true);

            // Set panel state to COLLAPSED
            // Collapse the panel to zero height (hidden)
            slidingLayout.setPanelHeight(230);
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }


    private void resetSlidingPanel() {
        SlidingUpPanelLayout slidingLayout = getView().findViewById(R.id.sliding_layout);
        if (slidingLayout != null) {
            // Set the panel state to COLLAPSED
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            // Optionally, reset touch behavior
            slidingLayout.setTouchEnabled(true);
        }
    }


    private class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("API Response", s); // Log the API response
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList = null;
            try {
                JSONObject object = new JSONObject(strings[0]);

                // Ensure the response contains the expected fields
                if (object.has("results")) {
                    mapList = jsonParser.parseResult(object);
                } else {
                    Log.e("ParserTask", "No results found in the response");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }


        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            if (map != null) {
                map.clear(); // Clear existing markers
                for (HashMap<String, String> hashMap : hashMaps) {
                    double lat = Double.parseDouble(hashMap.get("lat"));
                    double lng = Double.parseDouble(hashMap.get("lng"));
                    String name = hashMap.get("name");
                    String placeId = hashMap.get("place_id"); // Get place_id for fetching details

                    Log.d("ParserTask", "Place ID: " + placeId); // Log the place_id

                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions options = new MarkerOptions().position(latLng).title(name);

                    String type = hashMap.get("type");
                    if (type != null) {
                        int iconRes = getIconForType(type);
                        options.icon(vectorToBitmap(iconRes));
                    }

                    // Add marker and store it for later reference
                    Marker marker = map.addMarker(options);

                    // Fetch place details using place_id for each marker click
                    if (marker != null) {
                        marker.setTag(placeId); // Set place_id as a tag for later use
                    }
                }

                // Add marker click listener
                map.setOnMarkerClickListener(marker -> {
                    // Clear previous details
                    clearDetailsPanel();

                    // Move the camera to the clicked marker and zoom in
                    LatLng markerPosition = marker.getPosition();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 16)); // Zoom level 16 can be adjusted

                    // Fetch place details for the clicked marker
                    String placeId = (String) marker.getTag(); // Retrieve place_id from the marker tag
                    if (placeId != null) {
                        fetchPlaceDetails(marker, placeId); // Fetch details using placeId
                    }
                    getETA(markerPosition); // Get ETA to clicked marker




                    // Show the marker details layout
                    showMarkerDetailsLayout(marker.getTitle());
                    return true; // Consume the event
                });
            }
        }


        private void fetchPlaceDetails(Marker marker, String placeId) {
            // Log the URL for debugging purposes
            String url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                    "placeid=" + placeId + "&key=" + getResources().getString(R.string.map_api_key);

            Log.d("FetchPlaceDetails", "Request URL: " + url);  // Log URL for debugging

            // Execute background task to fetch details
            new FetchPlaceDetailsTask(marker).execute(url);
        }

        private void clearDetailsPanel() {
            View markerDetailsPanel = getView().findViewById(R.id.layout_facilities_details);
            if (markerDetailsPanel != null) {
                TextView addressTextView = markerDetailsPanel.findViewById(R.id.txtViewAddress);
                TextView phoneTextView = markerDetailsPanel.findViewById(R.id.txtViewContact);
                TextView hoursTextView = markerDetailsPanel.findViewById(R.id.txtViewHours);

                // Clear old data
                addressTextView.setText("");
                phoneTextView.setText("");
                hoursTextView.setText("");
            }
        }


        private void showMarkerDetailsLayout(String markerTitle) {
            // Switch to marker details panel
            View facilitiesPanel = getView().findViewById(R.id.layout_facilities_panel);
            View markerDetailsPanel = getView().findViewById(R.id.layout_facilities_details);

            if (facilitiesPanel != null) {
                facilitiesPanel.setVisibility(View.GONE);
            }
            if (markerDetailsPanel != null) {
                markerDetailsPanel.setVisibility(View.VISIBLE);
                TextView titleView = markerDetailsPanel.findViewById(R.id.txtViewFacilitiesName);
                titleView.setText(markerTitle);
            }


            // Configure sliding panel for alternate layout
            configureSlidingPanelForAlternate();

        }



        private void configureSlidingPanelForAlternate() {
            SlidingUpPanelLayout slidingLayout = getView().findViewById(R.id.sliding_layout);
            if (slidingLayout != null) {
                // Set the panel height for the alternate layout
                slidingLayout.setPanelHeight((int) getResources().getDimension(R.dimen.alternate_panel_height));

                // Enable sliding for the alternate layout
                slidingLayout.setTouchEnabled(true);

                // Set panel state to a desired state (e.g., COLLAPSED or PEEK)
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); // Or SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }


        private int getIconForType(String type) {
            switch (type.toLowerCase()) {
                case "fire_station":
                    return R.drawable.ic_fire_station;
                case "hospital":
                    return R.drawable.ic_hospital;
                case "police":
                    return R.drawable.ic_police_station;
                default:
                    return R.drawable.ic_location_small; // Default marker icon
            }
        }
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), id);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private class FetchPlaceDetailsTask extends AsyncTask<String, Void, String> {
        private Marker marker;

        // Constructor to pass the marker
        public FetchPlaceDetailsTask(Marker marker) {
            this.marker = marker;
        }

        @Override
        protected String doInBackground(String... strings) {
            String placeDetailsJson = null;
            try {
                placeDetailsJson = downloadUrl(strings[0]);
                Log.d("PlaceDetails", "Response: " + placeDetailsJson); // Log the API response
            } catch (IOException e) {
                e.printStackTrace();
            }
            return placeDetailsJson;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONObject resultObj = jsonResponse.getJSONObject("result");

                String address = resultObj.optString("formatted_address");
                String phone = resultObj.optString("international_phone_number");
                String hours = getOperatingHours(resultObj);

                // Log the details to verify
                Log.d("PlaceDetails", "Address: " + address);
                Log.d("PlaceDetails", "Phone: " + phone);
                Log.d("PlaceDetails", "Hours: " + hours);

                // Update UI with place details
                updateDetailsPanel(address, phone, hours);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getOperatingHours(JSONObject resultObj) {
            try {
                if (resultObj.has("opening_hours")) {
                    JSONObject hoursObj = resultObj.getJSONObject("opening_hours");
                    if (hoursObj.has("weekday_text")) {
                        // Get the weekday text and format it for display
                        JSONArray weekdayText = hoursObj.getJSONArray("weekday_text");
                        StringBuilder formattedHours = new StringBuilder();
                        for (int i = 0; i < weekdayText.length(); i++) {
                            formattedHours.append(weekdayText.getString(i));
                            if (i != weekdayText.length() - 1) {
                                formattedHours.append("\n"); // New line between days
                            }
                        }
                        return formattedHours.toString();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Not available";
        }

    }





    private void updateDetailsPanel(String address, String phone, String hours) {
        View markerDetailsPanel = getView().findViewById(R.id.layout_facilities_details);
        if (markerDetailsPanel != null) {
            TextView addressTextView = markerDetailsPanel.findViewById(R.id.txtViewAddress);
            TextView phoneTextView = markerDetailsPanel.findViewById(R.id.txtViewContact);
            TextView hoursTextView = markerDetailsPanel.findViewById(R.id.txtViewHours);

            // Update with actual data, handle empty values as needed
            addressTextView.setText(address != null ? address : "No address available");
            phoneTextView.setText(phone != null && !phone.isEmpty() ? phone : "No contact number available");
            hoursTextView.setText(hours != null && !hours.isEmpty() ? hours : "No operating hours available");
        }
    }




    private class GetETA extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = null;
            try {
                data = downloadUrl(params[0]); // Get the JSON response from Directions API
                // Log the response to see if it's correct
                Log.d("DirectionsAPI", "Response: " + data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray routes = jsonObject.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject leg = route.getJSONArray("legs").getJSONObject(0);

                    // Extract duration
                    String duration = leg.getJSONObject("duration").getString("text");

                    // Extract distance
                    String distance = leg.getJSONObject("distance").getString("text");

                    Log.d("ETA", "ETA received: " + duration);
                    Log.d("Distance", "Distance received: " + distance);

                    // Update UI with both ETA and distance
                    updateETADisplay(duration, distance);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void updateETADisplay(String eta, String distance) {
            TextView etaTextView = getView().findViewById(R.id.txtViewETA); // Reference to your ETA TextView
            etaTextView.setText(eta + " ("+distance+")");
        }



    }

    // AsyncTask to fetch directions
    private class GetDirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            try {
                // Send HTTP request to the Directions API
                URL directionUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) directionUrl.openConnection();
                connection.setRequestMethod("GET");
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                // Parse the JSON result here and plot the route on the map
                parseDirections(result);
            }
        }

        private void parseDirections(String jsonResponse) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray routes = jsonObject.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");

                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                    // Log or display distance and duration
                    Log.d("Route Info", "Distance: " + distanceText + ", Duration: " + durationText);

                    // Get the polyline points and plot the route
                    String polyline = route.getJSONObject("overview_polyline").getString("points");
                    List<LatLng> routePoints = decodePoly(polyline);

                    // Add polyline to map
                    PolylineOptions polylineOptions = new PolylineOptions().addAll(routePoints).color(Color.BLUE).width(10);
                    map.addPolyline(polylineOptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Method to decode polyline string to list of LatLng
        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dLat = ((result & 0x1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dLat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dLng = ((result & 0x1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dLng;

                LatLng position = new LatLng((lat / 1E5), (lng / 1E5));
                poly.add(position);
            }
            return poly;
        }


    }

    private String buildDirectionsUrl(LatLng origin, LatLng destination) {
        String originStr = origin.latitude + "," + origin.longitude;
        String destStr = destination.latitude + "," + destination.longitude;
        String apiKey = getResources().getString(R.string.map_api_key);  // Your API Key
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + originStr +
                "&destination=" + destStr +
                "&key=" + apiKey;
    }



}


