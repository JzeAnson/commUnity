package com.example.bustrackingmodule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BusDetailsViewModel extends ViewModel {
    private final MutableLiveData<String> busPlate = new MutableLiveData<>();
    private final MutableLiveData<Double> busSpeed = new MutableLiveData<>();

    public void setBusDetails(String plate, double speed) {
        busPlate.setValue(plate);
        busSpeed.setValue(speed);
    }

    public LiveData<String> getBusPlate() {
        return busPlate;
    }

    public LiveData<Double> getBusSpeed() {
        return busSpeed;
    }
}

