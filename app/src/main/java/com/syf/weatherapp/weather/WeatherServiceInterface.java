package com.syf.weatherapp.weather;

import com.syf.weatherapp.forecast.ForecastData;
import com.syf.weatherapp.forecast.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherServiceInterface {

    @GET("data/2.5/forecast")
    Call<ForecastData> getForecastData(@Query("lat") double lat,
                                       @Query("lon") double lon,
                                       @Query("APPID") String apiKey,
                                       @Query("units") String units);

    @GET("data/2.5/weather")
    Call<Results> getCurrentWeather(@Query("lat") double lat,
                                    @Query("lon") double lon,
                                    @Query("APPID") String apiKey,
                                    @Query("units") String units);
}
