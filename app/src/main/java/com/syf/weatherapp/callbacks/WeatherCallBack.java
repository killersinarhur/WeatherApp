package com.syf.weatherapp.callbacks;


import com.syf.weatherapp.forecast.Results;

public interface WeatherCallBack {
    void onWeatherSuccess(Results results);

    void onWeatherFailure(int code, String message);
}
