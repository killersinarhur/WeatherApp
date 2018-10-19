package com.syf.weatherapp.callbacks;


import com.syf.weatherapp.forecast.ForecastData;

public interface ForecastCallback {
    void onForecastSuccess(ForecastData forecastData);
        void onForecastFailure(int code, String message);
}
