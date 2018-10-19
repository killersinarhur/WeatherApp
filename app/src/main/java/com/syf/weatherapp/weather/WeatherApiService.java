package com.syf.weatherapp.weather;


import android.location.Address;
import android.net.Uri;

import com.syf.weatherapp.callbacks.ForecastCallback;
import com.syf.weatherapp.callbacks.WeatherCallBack;
import com.syf.weatherapp.configuration.InterceptorConfig;
import com.syf.weatherapp.configuration.OkHttpManagerConfig;
import com.syf.weatherapp.configuration.UnsafeHostnameVerifierConfig;
import com.syf.weatherapp.configuration.UnsafeTrustManagerConfig;
import com.syf.weatherapp.forecast.ForecastData;
import com.syf.weatherapp.forecast.Results;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherApiService {

    private static final String BASE_URL_WEATHER = "https://api.openweathermap.org/";
    private static final String API_KEY = "09bd078e0f4013ea81c6e4dde8a3ae6d";
    private static final String UNTIS = "imperial";
    private final WeatherServiceInterface weatherServiceInterface;

    public WeatherApiService() {
        weatherServiceInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL_WEATHER)
                .addConverterFactory(GsonConverterFactory.create())
                //This was done to allow for SSL Proxying to occuring to view request and response as they occured
                .client(new OkHttpManagerConfig(new UnsafeTrustManagerConfig(), new InterceptorConfig(), new UnsafeHostnameVerifierConfig()).getOkHttpClient())
                .build()
                .create(WeatherServiceInterface.class);
    }

    public static String formatUnixDate(long time) {
        time=time*1000L;
        SimpleDateFormat newFormat = new SimpleDateFormat("E MM-dd", Locale.US);
        try {
            Date date= new Date(time);
            return newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public void getFiveDayForecast(ForecastCallback callback, Address address) {
        double lat = address.getLatitude();
        double lon = address.getLongitude();

        weatherServiceInterface.getForecastData(lat, lon, API_KEY, UNTIS).enqueue(new Callback<ForecastData>() {
            @Override
            public void onResponse(Call<ForecastData> call, Response<ForecastData> response) {
                if (response.isSuccessful() && response.body().getCod().equals("200")) {
                    callback.onForecastSuccess(response.body());
                } else {
                    callback.onForecastFailure((response != null) ? response.code() : -1, (response != null) ? response.message() : "Response is null");
                }
            }

            @Override
            public void onFailure(Call<ForecastData> call, Throwable t) {
                callback.onForecastFailure(-1, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void getCurrentWeather(WeatherCallBack callback, Address address) {
        double lat = address.getLatitude();
        double lon = address.getLongitude();
        weatherServiceInterface.getCurrentWeather(lat, lon, API_KEY, UNTIS).enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.isSuccessful()) {
                    callback.onWeatherSuccess(response.body());
                } else {
                    callback.onWeatherFailure((response != null) ? response.code() : -1, (response != null) ? response.message() : "Response is null");
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                callback.onWeatherFailure(-1, t.getMessage());
                t.printStackTrace();
            }
        });
    }


    //Takes Codes and Makes them into something Helpful
    public static String formatDescString(int id) {
        String returnString = "";
        if (200 <= id && id < 300) {
            returnString = "Thunderstorms";
        } else if (300 <= id && id < 500) {
            returnString = "Drizzle";
        } else if (500 <= id && id < 600) {
            returnString = "Rain";
        } else if (600 <= id && id < 700) {
            returnString = "Snow";
        } else if (700 <= id && id < 800) {
            switch (id) {
                case 701:
                    returnString = "Mist";
                    break;
                case 711:
                    returnString = "Smoke";
                    break;
                case 721:
                    returnString = "Haze";
                    break;
                case 731:
                    returnString = "Sand, Dust Whirls ";
                    break;
                case 741:
                    returnString = "Fog";
                    break;
                case 751:
                    returnString = "Sand";
                    break;
                case 761:
                    returnString = "Dust";
                    break;
                case 762:
                    returnString = "Volcanic Ash";
                    break;
                case 771:
                    returnString = "Squalls";
                    break;
                case 781:
                    returnString = "Tornado";
                    break;
            }
        } else if (801 <= id && id < 805) {
            returnString = "Cloudy Skies";
        } else {
            returnString = "Clear Skies";
        }
        return returnString;
    }

    public static Uri getImageURL(int id) {
        String returnString = "http://openweathermap.org/img/w/";
        if (200 <= id && id < 300) {
            returnString += "11d.png";
        } else if (300 <= id && id < 500) {
            returnString += "09d.png";
        } else if (500 <= id && id < 600) {
            returnString += "10d.png";
        } else if (600 <= id && id < 700) {
            returnString += "13d.png";
        } else if (700 <= id && id < 800) {
            returnString += "50d.png";
        } else if (801 <= id && id < 805) {
            returnString += "02d.png";
        } else {
            returnString += "01d.png";
        }
        return Uri.parse(returnString);
    }


    //Limitations in the calendar API force this work around
    public boolean isSameDay(Calendar cal1, Calendar cal2) {

        return cal1 != null && cal2 != null
                && (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }    //Limitations in the calendar API force this work around

    public boolean isSameDay(Date date, Calendar cal2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        return isSameDay(cal1,cal2);
    }

    public boolean isSameDay(Date date, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1,cal2);
    }

    public boolean isSameDay(String fromService, Calendar cal2) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(fromService);
            return isSameDay(date, cal2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean isSameDay(String fromService, String secondService) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(fromService);
            Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(secondService);
            return isSameDay(date, date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String formatDate(String dtTxt) {
        String returnString = "";

        try {
            Date fromService = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).parse(dtTxt);
            SimpleDateFormat newFormat = new SimpleDateFormat("E", Locale.US);
            return newFormat.format(fromService);
        } catch (ParseException var5) {
            var5.printStackTrace();
            return returnString;
        }
    }


}
