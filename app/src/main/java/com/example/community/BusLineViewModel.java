package com.example.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BusLineViewModel extends ViewModel {
    private final MutableLiveData<String> busLine = new MutableLiveData<>();

    public void setBusLine(String line) {
        busLine.setValue(line);
    }

    public LiveData<String> getBusLine() {
        return busLine;
    }
}
