package com.example.bustrackingmodule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BusStopData {
    public static final HashMap<String, List<String>> BUS_STOPS = new HashMap<>();

    static {
        BUS_STOPS.put("T789", Arrays.asList("LRT Universiti", "Masjid Ar-Rahman", "Faculty of Law", "KK1","Faculty of Engineering (Utara)","UM Central","Dewan Tunku Canselor","PASUM","KL Gateway","The Vertical","Flat Sri Angkasa","Centrio Pantai Hillpark (Utara)","Condominium Andalusia","Pantai Hillpark Phase 5","Pusat Komuniti Lembah Pantai","Inwood Residences","Pantai Murni (Timur)","Bangsar South","Nexus Bangsar South","Flat PKNS","LRT Universiti"));
        BUS_STOPS.put("T815", Arrays.asList("MRT Pintu A (Departure)", "Tiara Damansara (Utara)", "Tiara Damansara (Selatan)", "UIA PJ (Barat)", "UIA PJ (Selatan)", "Mahsa University","Faculty of Business and Economics","UM Central","Dewan Tunku Canselor","PASUM", "KK12","KK5","Pusat Sukan","Academy of Islamic Studies","KK10","Faculty of Computer Science and Information Technology","Academy of Malay Studies","KK4","Faculty of Sains","Dewan Tunku Canselor","PASUM","Faculty of Law","KK1","Faculty of Engineering (Utara)","Faculty of Engineering (Barat)","Opposite of Mahsa University","SMK Sultan Abd Samad (Barat)","Pusat Asasi Mahllah Aisyah","MRT Pintu A (Arrival)"));
    }
}

