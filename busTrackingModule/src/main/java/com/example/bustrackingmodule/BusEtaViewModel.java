package com.example.bustrackingmodule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BusEtaViewModel extends ViewModel {
    private final MutableLiveData<String> etaLiveData = new MutableLiveData<>();

    public void setETA(String eta) {
        etaLiveData.setValue(eta);
    }

    public LiveData<String> getETA() {
        return etaLiveData;
    }
}
