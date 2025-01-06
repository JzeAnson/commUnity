package com.example.bustrackingmodule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class TravelDistanceViewModel extends ViewModel{
    private final MutableLiveData<Integer> distanceTravelled = new MutableLiveData<>();
    public void setDistanceTravelled(int distance) {
        distanceTravelled.setValue(distance);
    }

    public LiveData<Integer> getDistanceTravelled() {
        return distanceTravelled;
    }
}
