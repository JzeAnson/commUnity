package com.example.bustrackingmodule;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Handler handler = new Handler();
    private HashMap<String, Marker> busMarkers = new HashMap<>();
    private static final String ROUTES_API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes?key=AIzaSyBBgJ7CggoyCxdRHZsBq3lkzGwNHNtOuC8";
    private static final String API_KEY = "AIzaSyBBgJ7CggoyCxdRHZsBq3lkzGwNHNtOuC8";
    private static final LatLng MRT_PintuA_Start = new LatLng(3.128438447190037, 101.64274654810369);
    private static final LatLng TiaraDamansara_Utara = new LatLng(3.1275635804478363, 101.63901768956313);
    private static final LatLng TiaraDamanasara_Selatan = new LatLng(3.124992859957881, 101.63846187760383);
    private static final LatLng UIA_PJ_Barat = new LatLng(3.1208018723072053, 101.63843737238882);
    private static final LatLng UIA_PJ_Selatan = new LatLng(3.1188236604835007, 101.63911062350631);
    private static final LatLng MahsaUniversity = new LatLng(3.117095102496668, 101.64975908654775);
    private static final LatLng FPE = new LatLng(3.1188670397012688, 101.65443240037251);
    private static final LatLng UMCentral = new LatLng(3.121027756234481, 101.65349128020985);
    private static final LatLng DTC = new LatLng(3.1218819658455965, 101.65719294869952);
    private static final LatLng PASUM = new LatLng(3.1217511405550593, 101.65833869448123);
    private static final LatLng KK12 = new LatLng(3.1245714637578663, 101.66010058227747);
    private static final LatLng KK5 = new LatLng(3.1267063630066154, 101.65974762999079);
    private static final LatLng PusatSukan = new LatLng(3.1293930491586157, 101.66016663492819);
    private static final LatLng AcademyIslamicStudies = new LatLng(3.1321668479501765, 101.65935085971323);
    private static final LatLng KK10 = new LatLng(3.1298740181320435, 101.65052510291004);
    private static final LatLng FSKTM = new LatLng(3.127695764318769, 101.65026015220838);
    private static final LatLng APM = new LatLng(3.126214095271264, 101.6516057516757);
    private static final LatLng KK4 = new LatLng(3.1244051989249657, 101.65153815248865);
    private static final LatLng FacultySains = new LatLng(3.122072472156583, 101.65355602196804);
    private static final LatLng FacultyLaw = new LatLng(3.1186795247095045, 101.66107382897934);
    private static final LatLng KK1 = new LatLng(3.1181287938367412, 101.65931500912448);
    private static final LatLng FacultyEngineering_Utara = new LatLng(3.119101374179019, 101.65554195854519);
    private static final LatLng FacultyEngineering_Barat = new LatLng(3.1184133846949904, 101.654263726321);
    private static final LatLng MahsaUniversity_Opposite = new LatLng(3.117953590546266, 101.64805381158756);
    private static final LatLng SMKSultanAbdSamad_Barat = new LatLng(3.118341551686053, 101.64506453180877);
    private static final LatLng PusatAsasiMahallahAisyah = new LatLng(3.119639625296002, 101.64321924160726);
    private static final LatLng MRT_PintuA_End =  new LatLng(3.1283087068732125, 101.64286190984333);
    private static final LatLng LRT = new LatLng(3.114765283231864, 101.66181830810577);
    private static final LatLng MasjidArRahman = new LatLng(3.117516266611659, 101.66273216567676);
    private static final LatLng KLGateway = new LatLng(3.112867494408178, 101.66272866005723);
    private static final LatLng TheVertical = new LatLng(3.110336660576631, 101.66596890684279);
    private static final LatLng FlatSriAngkasa = new LatLng(3.1096038161260493, 101.668154161246);
    private static final LatLng CentrioPantaiHillpark_Utara = new LatLng(3.1083881413918357, 101.66568953028715);
    private static final LatLng CondominiumAndalusia = new LatLng(3.1076446990799975, 101.66235855091263);
    private static final LatLng PantaiHillpart_Phase5 = new LatLng(3.1065153223027386, 101.66154267034251);
    private static final LatLng PusatKomunitiLembahPantai = new LatLng(3.1036826648293503, 101.66121104158411);
    private static final LatLng InwoodResidences = new LatLng(3.1026787920526187, 101.66357870430434);
    private static final LatLng PantaiMurni = new LatLng(3.1040985630841855, 101.66611156903977);
    private static final LatLng CentrioPantaiHillpark_Timur = new LatLng(3.1068894985734965, 101.66602959828776);
    private static final LatLng BangsarSouth = new LatLng(3.108966194507094, 101.66801018876366);
    private static final LatLng NexusBangsarSouth = new LatLng(3.110021328769349, 101.6661533168561);
    private static final LatLng FlatPKNS = new LatLng(3.11229581554081, 101.66209718406036);
    private LatLng userLocation;
    private LatLng userDestination;
    private String UserLocation;
    private String UserDestination;
    List<BusStop> LRTbusStops = Arrays.asList(
            new BusStop("LRT Universiti",LRT.latitude, LRT.longitude),
            new BusStop("Masjid Ar-Rahman",MasjidArRahman.latitude, MasjidArRahman.longitude),
            new BusStop("Faculty of Law",FacultyLaw.latitude, FacultyLaw.longitude),
            new BusStop("KK1",KK1.latitude, KK1.longitude),
            new BusStop("Faculty of Engineering (Utara)",FacultyEngineering_Utara.latitude, FacultyEngineering_Utara.longitude),
            new BusStop("UM Central",UMCentral.latitude, UMCentral.longitude),
            new BusStop("Dewan Tunku Canselor", DTC.latitude, DTC.longitude),
            new BusStop("PASUM",PASUM.latitude, PASUM.longitude),
            new BusStop("KL Gateway",KLGateway.latitude, KLGateway.longitude),
            new BusStop("The Vertical",TheVertical.latitude, TheVertical.longitude),
            new BusStop("Flat Sri Angkasa",FlatSriAngkasa.latitude, FlatSriAngkasa.longitude),
            new BusStop("Centrio Pantai Hillpark (Utara)",CentrioPantaiHillpark_Utara.latitude, CentrioPantaiHillpark_Utara.longitude),
            new BusStop("Condominium Andalusia",CondominiumAndalusia.latitude, CondominiumAndalusia.longitude),
            new BusStop("Pantai Hillpark (Phase 5)",PantaiHillpart_Phase5.latitude, PantaiHillpart_Phase5.longitude),
            new BusStop("Pusat Komunikasi Lembah Pantai",PusatKomunitiLembahPantai.latitude, PusatKomunitiLembahPantai.longitude),
            new BusStop("Inwood Residences",InwoodResidences.latitude, InwoodResidences.longitude),
            new BusStop("Pantai Murni",PantaiMurni.latitude, PantaiMurni.longitude),
            new BusStop("Centrio Pantai Hillpark (Timur)",CentrioPantaiHillpark_Timur.latitude, CentrioPantaiHillpark_Timur.longitude),
            new BusStop("Bangsar South",BangsarSouth.latitude, BangsarSouth.longitude),
            new BusStop("Nexus Bangsar South",NexusBangsarSouth.latitude, NexusBangsarSouth.longitude),
            new BusStop("Flat PKNS",FlatPKNS.latitude, FlatPKNS.longitude)
    );
    List<BusStop> MRTbusStops = Arrays.asList(
            new BusStop("MRT Pintu A (Departure)",MRT_PintuA_Start.latitude, MRT_PintuA_Start.longitude),
            new BusStop("Tiara Damansara (Utara)",TiaraDamansara_Utara.latitude, TiaraDamansara_Utara.longitude),
            new BusStop("Tiara Damanasara (Selatan)",TiaraDamanasara_Selatan.latitude, TiaraDamanasara_Selatan.longitude),
            new BusStop("UIA PJ (Barat)", UIA_PJ_Barat.latitude, UIA_PJ_Barat.longitude),
            new BusStop("UIA PJ (Selatan)", UIA_PJ_Selatan.latitude, UIA_PJ_Selatan.longitude),
            new BusStop("Mahsa University", MahsaUniversity.latitude, MahsaUniversity.longitude),
            new BusStop("Faculty of Business and Economics",FPE.latitude, FPE.longitude),
            new BusStop("UM Central",UMCentral.latitude, UMCentral.longitude),
            new BusStop("Dewan Tunku Canselor", DTC.latitude, DTC.longitude),
            new BusStop("PASUM",PASUM.latitude, PASUM.longitude),
            new BusStop("KK12", KK12.latitude, KK12.longitude),
            new BusStop("KK5",KK5.latitude, KK5.longitude),
            new BusStop("Pusat Sukan",PusatSukan.latitude, PusatSukan.longitude),
            new BusStop("Academy of Islamic Studies",AcademyIslamicStudies.latitude, AcademyIslamicStudies.longitude),
            new BusStop("KK10",KK10.latitude, KK10.longitude),
            new BusStop("Faculty of Computer Science and Information Technology",FSKTM.latitude, FSKTM.longitude),
            new BusStop("Academy of Malay Studies",APM.latitude, APM.longitude),
            new BusStop("KK4",KK4.latitude, KK4.longitude),
            new BusStop("Faculty of Sains",FacultySains.latitude, FacultySains.longitude),
            new BusStop("Faculty of Law",FacultyLaw.latitude, FacultyLaw.longitude),
            new BusStop("KK1",KK1.latitude, KK1.longitude),
            new BusStop("Faculty of Engineering (Utara)",FacultyEngineering_Utara.latitude, FacultyEngineering_Utara.longitude),
            new BusStop("Faculty of Engineering (Barat)",FacultyEngineering_Barat.latitude, FacultyEngineering_Barat.longitude),
            new BusStop("Opposite of Mahsa University",MahsaUniversity_Opposite.latitude, MahsaUniversity_Opposite.longitude),
            new BusStop("SMK Sultan Abd Samad (Barat)",SMKSultanAbdSamad_Barat.latitude, SMKSultanAbdSamad_Barat.longitude),
            new BusStop("Pusat Asasi Mahallah Aisyah",PusatAsasiMahallahAisyah.latitude, PusatAsasiMahallahAisyah.longitude),
            new BusStop("MRT Pintu A (Arrival)",MRT_PintuA_End.latitude, MRT_PintuA_End.longitude)
    );
    private final List<LatLng> MRTLatLng = Arrays.asList(
            MRT_PintuA_Start,
            TiaraDamansara_Utara,
            TiaraDamanasara_Selatan,
            UIA_PJ_Barat,
            UIA_PJ_Selatan,
            MahsaUniversity,
            FPE,
            UMCentral,
            DTC,
            PASUM,
            KK12,
            KK5,
            PusatSukan,
            AcademyIslamicStudies,
            KK10,
            FSKTM,
            APM,
            KK4,
            FacultySains,
            FacultyLaw,
            KK1,
            FacultyEngineering_Utara,
            FacultyEngineering_Barat,
            MahsaUniversity_Opposite,
            SMKSultanAbdSamad_Barat,
            PusatAsasiMahallahAisyah,
            MRT_PintuA_End
    );
    private BusDetailsViewModel detailsViewModel;
    private BusEtaViewModel etaViewModel;
    private TravelDistanceViewModel travelDistanceViewModel;
    private String extractedBusLine;
    private int routeId;
    private Runnable fetchBusDataRunnable = new Runnable() {
        @Override
        public void run() {
            new FetchBusDataTask().execute(); // Execute the AsyncTask
            handler.postDelayed(this, 30000); // Schedule again in 30 seconds
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String busline = getArguments().getString("busLine");
            String origin = getArguments().getString("origin");
            String destination = getArguments().getString("destination");

            Log.d("MapsFragment", "Bus Line: " + busline + ", Origin: " + origin + ", Destination: " + destination);
            extractedBusLine = busline;
            UserLocation = origin;
            UserDestination = destination;
        }
        if (getArguments() == null){
            Log.d("MapsFragment","Arguments is null");
        }
        List<BusStop>BusStopsToCompare = LRTbusStops;
        if ("T815".equals(extractedBusLine))
            BusStopsToCompare = MRTbusStops;

        for (BusStop stop : BusStopsToCompare) {
            Log.d("MapsFragment", "Bus Stop: " + stop.getName() + ",");
            if (UserLocation.equals(stop.getName())) {
                userLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                Log.d("MapsFragment", "User Location: " + userLocation);
            }
            if (UserDestination.equals(stop.getName())) {
                userDestination = new LatLng(stop.getLatitude(), stop.getLongitude());
                Log.d("MapsFragment", "User Destination: " + userDestination);
                break;
            }
        }
        calculateRouteDistance(userLocation,userDestination);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailsViewModel = new ViewModelProvider(requireActivity()).get(BusDetailsViewModel.class);
        etaViewModel = new ViewModelProvider(requireActivity()).get(BusEtaViewModel.class);
        travelDistanceViewModel = new ViewModelProvider(requireActivity()).get(TravelDistanceViewModel.class);

        // Initialize the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapsFragment", "SupportMapFragment is null");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Start periodic updates
        handler.post(fetchBusDataRunnable);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MRT_PintuA_Start, 15));
        if ("T789".equals(extractedBusLine))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LRT, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Load static markers and routes
        initializeMarkers();
        loadRoutes(mMap);
    }

    private void initializeMarkers() {
        List<BusStop> BusStops=MRTbusStops;
        if ("T789".equals(extractedBusLine)){
            BusStops=LRTbusStops;
        }

        for (BusStop stop : BusStops) {
            LatLng position = new LatLng(stop.getLatitude(), stop.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Bus Stop: " + stop.getName()));
        }
    }

    private void loadRoutes(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            routeId = R.raw.mrt_bus_routes;
            if ("T789".equals(extractedBusLine)){
                routeId = R.raw.lrt_bus_routes;
            }
            // Read the JSON file
            InputStream inputStream = getResources().openRawResource(routeId);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray routes = new JSONArray(json);

            // Loop through each encoded polyline and draw it
            for (int i = 0; i < routes.length(); i++) {
                String encodedPolyline = routes.getString(i);
                List<LatLng> decodedPath = PolyUtil.decode(encodedPolyline);

                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(decodedPath)
                        .width(7f)
                        .color(Color.BLUE);
                mMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBusMarker(String licensePlate, LatLng position) {
        if (mMap == null) {
            Log.e("MapsFragment", "Google Map is not ready yet.");
            return;
        }

        Marker marker = busMarkers.get(licensePlate);
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.bus_icon)))
                    .title("Bus ID: " + licensePlate));
            busMarkers.put(licensePlate, marker);
        } else {
            marker.setPosition(position);
        }
    }

    private void updateBusDetails(String plate, double speed) {
        // Update ViewModel to share data with BusDetailsFragment
        detailsViewModel.setBusDetails(plate, speed);
    }

    private class FetchBusDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL selectedURL;
                selectedURL = new URL("https://api.data.gov.my/gtfs-realtime/vehicle-position/prasarana?category=rapid-bus-mrtfeeder");
                if (extractedBusLine.equals("T789")){
                     selectedURL = new URL("https://api.data.gov.my/gtfs-realtime/vehicle-position/prasarana?category=rapid-bus-kl");
                }
                FeedMessage feed = FeedMessage.parseFrom(selectedURL.openStream());

                for (FeedEntity entity : feed.getEntityList()) {
                    if (entity.hasVehicle()) {
                        double latitude = entity.getVehicle().getPosition().getLatitude();
                        double longitude = entity.getVehicle().getPosition().getLongitude();
                        String routeId = entity.getVehicle().getTrip().getRouteId();
                        String licensePlate = entity.getVehicle().getVehicle().getLicensePlate();
                        double speed = entity.getVehicle().getPosition().getSpeed();
                        double formattedSpeed = Double.parseDouble(String.format("%.2f", speed));

                        if ("T815".equals(routeId)) {
                            LatLng position = new LatLng(latitude, longitude);
                            requireActivity().runOnUiThread(() -> updateBusMarker(licensePlate, position));
                            requireActivity().runOnUiThread(() ->updateBusDetails(licensePlate, formattedSpeed));
                            calculateETA(position, userLocation, MRTLatLng);
                        }

                        if ("T7890".equals(routeId)) {
                            LatLng position = new LatLng(latitude, longitude);
                            requireActivity().runOnUiThread(() -> updateBusMarker(licensePlate, position));
                            requireActivity().runOnUiThread(() ->updateBusDetails(licensePlate, formattedSpeed));
                            calculateETA(position, userLocation, MRTLatLng);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MapsFragment", "Failed to fetch bus data: " + e.getMessage());
            }
            return null;
        }
    }
    private static int parseDurationToSeconds(String duration) {
        int seconds = 0;

        try {
            if (duration.endsWith("s")) {
                // Duration in seconds (e.g., "53s")
                seconds = Integer.parseInt(duration.replace("s", "").trim());
            } else if (duration.endsWith("m")) {
                // Duration in minutes (e.g., "2m")
                seconds = Integer.parseInt(duration.replace("m", "").trim()) * 60;
            } else if (duration.endsWith("h")) {
                // Duration in hours (e.g., "1h")
                seconds = Integer.parseInt(duration.replace("h", "").trim()) * 3600;
            } else {
                Log.e("MapsActivity", "Unexpected duration format: " + duration);
            }
        } catch (NumberFormatException e) {
            Log.e("MapsActivity", "Failed to parse duration: " + duration + ". Error: " + e.getMessage());
        }

        return seconds;
    }

    private void updateBusEta(String eta) {
        // Update ViewModel to share data with BusDetailsFragment
        etaViewModel.setETA(eta);
    }
    private void calculateETA(LatLng origin, LatLng destination, List<LatLng> intermediates) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Prepare the API request
                    URL url = new URL(ROUTES_API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("X-Goog-Api-Key", API_KEY);
                    conn.setRequestProperty("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.legs");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Build the JSON payload
                    JSONObject payload = new JSONObject();

                    // Origin
                    JSONObject originLocation = new JSONObject();
                    originLocation.put("latitude", origin.latitude);
                    originLocation.put("longitude", origin.longitude);
                    payload.put("origin", new JSONObject()
                            .put("location", new JSONObject()
                                    .put("latLng", originLocation))
                            .put("vehicleStopover", true) // VehicleStopover set to true
                    );

                    // Destination
                    JSONObject destinationLocation = new JSONObject();
                    destinationLocation.put("latitude", destination.latitude);
                    destinationLocation.put("longitude", destination.longitude);
                    payload.put("destination", new JSONObject()
                            .put("location", new JSONObject()
                                    .put("latLng", destinationLocation))
                            .put("vehicleStopover", true) // VehicleStopover set to true
                    );

                    // Additional routing preferences
                    payload.put("travelMode", "DRIVE");
                    payload.put("routingPreference", "TRAFFIC_AWARE");
                    payload.put("units", "METRIC");

                    // Log the JSON request
                    Log.d("MapsActivity", "Request Payload: " + payload.toString());
                    // Write the payload
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(payload.toString());
                    writer.flush();

                    // Read the response
                    if (conn.getResponseCode() == 200) {
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                        }
                        return response.toString();
                    } else {
                        Log.e("MapsFragment", "API Request Failed: " + conn.getResponseMessage());
                    }
                } catch (Exception e) {
                    Log.e("MapsFragment", "Error in calculateETAWithIntermediates: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String response) {
                if (response != null) {
                    try {
                        // Parse the response
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray routes = jsonResponse.getJSONArray("routes");

                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONArray legs = route.getJSONArray("legs");
                            int totalDuration = 0;

                            for (int i = 0; i < legs.length(); i++) {
                                JSONObject leg = legs.getJSONObject(i);
                                String durationString = leg.getString("duration");
                                Log.d("MapsActivity", "Leg Duration: " + durationString);

                                // Convert duration string to seconds
                                totalDuration += parseDurationToSeconds(durationString);
                            }

                            // Calculate ETA
                            long currentTimeMillis = System.currentTimeMillis();
                            long etaMillis = currentTimeMillis + (totalDuration * 1000L);
                            Date eta = new Date(etaMillis);

                            // Format the time for Malaysia timezone
                            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                            timeFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                            String formattedTime = timeFormatter.format(eta);

                            // Update the ETA
                            updateBusEta(formattedTime);

                            Log.d("MapsActivity", "ETA to final destination: " + eta.toString());
                        } else {
                            Log.e("MapsActivity", "No routes available in API response.");
                        }
                    } catch (Exception e) {
                        Log.e("MapsActivity", "Error parsing API response: " + e.getMessage());
                    }
                }
            }
        }.execute();
    }

    private void updateDistanceView (int distanceMeters) {
        travelDistanceViewModel.setDistanceTravelled(distanceMeters);
    }
    private void calculateRouteDistance(LatLng origin, LatLng destination) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    // Prepare the API request
                    URL url = new URL(ROUTES_API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("X-Goog-Api-Key", API_KEY);
                    conn.setRequestProperty("X-Goog-FieldMask", "routes.distanceMeters");
                    conn.setDoOutput(true);

                    // Build the JSON payload
                    JSONObject payload = new JSONObject();

                    // Origin
                    JSONObject originLocation = new JSONObject();
                    originLocation.put("latitude", origin.latitude);
                    originLocation.put("longitude", origin.longitude);
                    payload.put("origin", new JSONObject()
                            .put("location", new JSONObject()
                                    .put("latLng", originLocation)));

                    // Destination
                    JSONObject destinationLocation = new JSONObject();
                    destinationLocation.put("latitude", destination.latitude);
                    destinationLocation.put("longitude", destination.longitude);
                    payload.put("destination", new JSONObject()
                            .put("location", new JSONObject()
                                    .put("latLng", destinationLocation)));

                    // Additional preferences
                    payload.put("travelMode", "DRIVE");
                    payload.put("units", "METRIC");

                    // Write the payload to the connection
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(payload.toString());
                    writer.flush();

                    // Read the response
                    if (conn.getResponseCode() == 200) {
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                        }

                        // Parse the response to extract distance
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONArray routes = jsonResponse.getJSONArray("routes");

                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            int distanceMeters = route.getInt("distanceMeters");
                            return distanceMeters;
                        }
                    } else {
                        Log.e("MapsFragment", "API Request Failed: " + conn.getResponseMessage());
                    }
                } catch (Exception e) {
                    Log.e("MapsFragment", "Error calculating distance: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer distanceMeters) {
                if (distanceMeters != null) {
                    Log.d("MapsFragment", "Total Route Distance: " + distanceMeters + " meters");

                    // You can update the UI or pass the value to a ViewModel if needed
                    updateDistanceView(distanceMeters);
                }
            }
        }.execute();
    }



}
