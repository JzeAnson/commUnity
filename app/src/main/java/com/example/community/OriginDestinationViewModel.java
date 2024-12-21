package com.example.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OriginDestinationViewModel extends ViewModel {
    private final MutableLiveData<String> origin = new MutableLiveData<>();
    private final MutableLiveData<String> destination = new MutableLiveData<>();

    public void setOrigin(String origin) {
        this.origin.setValue(origin);
    }

    public void setDestination(String destination) {
        this.destination.setValue(destination);
    }

    public LiveData<String> getOrigin() {
        return origin;
    }

    public LiveData<String> getDestination() {
        return destination;
    }
}
