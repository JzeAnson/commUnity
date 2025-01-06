package com.example.community;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ApiService {
    @GET("data")
    Call<WarningResponse> getWarnings(
            @Header("Authorization") String token,
            @Query("datasetid") String datasetid,
            @Query("datacategoryid") String datacategoryid,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate
    );
}


