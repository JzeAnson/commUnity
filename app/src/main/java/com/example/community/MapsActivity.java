package com.example.community;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.community.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

import com.google.maps.android.PolyUtil;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

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
    private static final LatLng userLocation = UMCentral;
    private Handler handler = new Handler();
    private Runnable fetchBusDataRunnable = new Runnable() {
        @Override
        public void run() {
            new FetchBusDataTask().execute(); // Execute the AsyncTask
            handler.postDelayed(this, 30000); // Schedule again in 0.5 minute (30000ms)
        }
    };
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
            new BusStop("Dewan Tunku Canselor (2)", DTC.latitude, DTC.longitude),
            new BusStop("Pusat Sukan (2)", PASUM.latitude, PASUM.longitude),
            new BusStop("Faculty of Law",FacultyLaw.latitude, FacultyLaw.longitude),
            new BusStop("KK1",KK1.latitude, KK1.longitude),
            new BusStop("Faculty of Engineering (Utara)",FacultyEngineering_Utara.latitude, FacultyEngineering_Utara.longitude),
            new BusStop("Faculty of Engineering (Barat)",FacultyEngineering_Barat.latitude, FacultyEngineering_Barat.longitude),
            new BusStop("Opposite of Mahsa University",MahsaUniversity_Opposite.latitude, MahsaUniversity_Opposite.longitude),
            new BusStop("SMK Sultan Abd Samad (Barat)",SMKSultanAbdSamad_Barat.latitude, SMKSultanAbdSamad_Barat.longitude),
            new BusStop("Pusat Asasi Mahallah Aisyah",PusatAsasiMahallahAisyah.latitude, PusatAsasiMahallahAisyah.longitude),
            new BusStop("MRT Pintu A (Arrival)",MRT_PintuA_End.latitude, MRT_PintuA_End.longitude)
    );
    List<BusStop> LRTbusStops = Arrays.asList(
            new BusStop("LRT (Departure)",LRT.latitude, LRT.longitude),
            new BusStop("Masjud Ar-Rahman",MasjidArRahman.latitude, MasjidArRahman.longitude),
            new BusStop("Faculty of Law", FacultyLaw.latitude, FacultyLaw.longitude),
            new BusStop("KK1", KK1.latitude, KK1.longitude),
            new BusStop("Faculty of Engineering", FacultyEngineering_Utara.latitude, FacultyEngineering_Utara.longitude),
            new BusStop("UM Central", UMCentral.latitude, UMCentral.longitude),
            new BusStop("DTC", DTC.latitude, DTC.longitude),
            new BusStop("PASUM", PASUM.latitude, PASUM.longitude),
            new BusStop("KL Gateway",KLGateway.latitude,KLGateway.longitude),
            new BusStop("The Vertical",TheVertical.latitude,TheVertical.longitude),
            new BusStop("Flat Sri Angkasa",FlatSriAngkasa.latitude,FlatSriAngkasa.longitude),
            new BusStop("Centrio Pantai Hillpark (Utara)",CentrioPantaiHillpark_Utara.latitude,CentrioPantaiHillpark_Utara.longitude),
            new BusStop("Condominium Andalusia",CondominiumAndalusia.latitude,CondominiumAndalusia.longitude),
            new BusStop("Pantai Hillpark Phase 5", PantaiHillpart_Phase5.latitude, PantaiHillpart_Phase5.longitude),
            new BusStop("Pusat Komuniti Lembah Pantai", PusatKomunitiLembahPantai.latitude,PusatKomunitiLembahPantai.longitude),
            new BusStop("Inwood Residences", InwoodResidences.latitude,InwoodResidences.longitude),
            new BusStop("Pantai Murni",PantaiMurni.latitude,PantaiMurni.longitude),
            new BusStop("Centrio Pantai Hillpark (Timur)",CentrioPantaiHillpark_Timur.latitude,CentrioPantaiHillpark_Timur.longitude),
            new BusStop("Bangsar South",BangsarSouth.latitude,BangsarSouth.longitude),
            new BusStop("Nexus Bangsar South",NexusBangsarSouth.latitude,NexusBangsarSouth.longitude),
            new BusStop("Flat PKNS Kerinchi",FlatPKNS.latitude,FlatPKNS.longitude),
            new BusStop("LRT (Arrival)",LRT.latitude,LRT.longitude)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Attach the map ready callback
        } else {
            Log.e("MapsActivity", "SupportMapFragment is null");
        }

        // Start periodic updates
        handler.post(fetchBusDataRunnable);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap == null) {
            Log.e("MapsActivity", "GoogleMap is null");
            return;
        }
        Log.d("MapsActivity", "onMapReady called");
        mMap = googleMap;
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MRT_PintuA_Start.latitude,MRT_PintuA_Start.longitude), 17));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        for (BusStop stop : MRTbusStops) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.getLatitude(), stop.getLongitude()))
                    .title("Bus Stop: " + stop.getName()));
        }

        loadRoutes(mMap);  // Add this to load the polylines
    }
    private void loadRoutes(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            // Read the JSON file
            InputStream inputStream = getResources().openRawResource(R.raw.mrt_bus_routes);
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
            Log.e("MapsActivity", "Google Map is not ready yet.");
            return;
        }

        Marker marker = busMarkers.get(licensePlate);
        if (marker == null) {
            // Create a new marker if it doesn't exist
            marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)) // Custom bus icon
                    .title("Bus ID: " + licensePlate));
            busMarkers.put(licensePlate, marker);
        } else {
            // Update the marker's position
            marker.setPosition(position);
        }
    }

    private HashMap<String, Marker> busMarkers = new HashMap<>();
    // AsyncTask to fetch data from GTFS Realtime API
    private class FetchBusDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // URL for the GTFS real-time feed (replace with your desired category)
                URL url = new URL("https://api.data.gov.my/gtfs-realtime/vehicle-position/prasarana?category=rapid-bus-mrtfeeder");

                // Parse the feed from the GTFS Realtime API
                FeedMessage feed = FeedMessage.parseFrom(url.openStream());

                // Loop through the entities and process the bus positions
                for (FeedEntity entity : feed.getEntityList()) {
                    if (entity.hasVehicle()) {
                        // Get vehicle position details
                        double latitude = entity.getVehicle().getPosition().getLatitude();
                        double longitude = entity.getVehicle().getPosition().getLongitude();
                        String tripId = entity.getVehicle().getTrip().getTripId();
                        String routeId = entity.getVehicle().getTrip().getRouteId();
                        double speed = entity.getVehicle().getPosition().getSpeed();
                        String licensePlate = entity.getVehicle().getVehicle().getLicensePlate();

                        if ("T815".equals(routeId)){
                            LatLng position = new LatLng(latitude, longitude);

                            runOnUiThread(() -> updateBusMarker(licensePlate, position));
                        }
                        // Log the bus information (you can also update markers here)
                        Log.d("MapsActivity", "Bus " + licensePlate + " (Route " + routeId + ") is at: " + latitude + ", " + longitude);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MapsActivity", "Failed to fetch bus data: " + e.getMessage());
            }
            return null;
        }




}}