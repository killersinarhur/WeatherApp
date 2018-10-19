package com.syf.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.syf.weatherapp.forecast.Results;
import com.syf.weatherapp.weather.WeatherApiService;

import static com.syf.weatherapp.Utils.createString;

class CurrentWeatherViewHolder extends RecyclerView.ViewHolder {
    private TextView currentWeather;
    private TextView weatherConditions;
    private TextView weatherHighOf;
    private TextView weatherLowOf;
    private TextView humidity;
    private TextView pressure;
    private TextView currentDay;
    private ImageView weatherImage;

    public static CurrentWeatherViewHolder inflate(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.current_weather_vh, viewGroup, false);
        return new CurrentWeatherViewHolder(view);

    }

    public CurrentWeatherViewHolder(@NonNull View itemView) {
        super(itemView);

    }

    public void bind(Results resultFromCurrent) {
        currentWeather = itemView.findViewById(R.id.current_weather);
        weatherConditions = itemView.findViewById(R.id.weather_condition);
        weatherHighOf = itemView.findViewById(R.id.high_of);
        weatherLowOf = itemView.findViewById(R.id.low_of);
        humidity = itemView.findViewById(R.id.humidity);
        pressure = itemView.findViewById(R.id.pressure);

        currentDay = itemView.findViewById(R.id.todays_date);
        weatherImage = itemView.findViewById(R.id.weather_image);

        currentDay.setText(new StringBuilder().append("Date: ").append(WeatherApiService.formatUnixDate(resultFromCurrent.getDt())));
        Picasso.get()
                .load(WeatherApiService.getImageURL(resultFromCurrent.getId()))
                .placeholder(itemView.getContext().getDrawable(R.drawable.ic_launcher_foreground))
                .error(itemView.getContext().getDrawable(R.drawable.ic_error_black_24dp))
                .fit()
                .into(weatherImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Picasso Error", e.toString());
                        e.printStackTrace();

                    }
                });
        currentWeather.setText(createString("Current Weather: ", " ", String.valueOf(Math.round(resultFromCurrent.getMain().getTemp())), "°F"));
        weatherLowOf.setText(createString("Low of: ", " ", String.valueOf(Math.round(resultFromCurrent.getMain().getTempMin())), "°F"));
        weatherHighOf.setText(createString("High of: ", " ", String.valueOf(Math.round(resultFromCurrent.getMain().getTempMax())), "°F"));
        weatherConditions.setText(createString("You Can Expect: ", " ", WeatherApiService.formatDescString(resultFromCurrent.getWeather().get(0).getId())));
        pressure.setText(createString("Pressure: ", " ", String.valueOf(Math.round(resultFromCurrent.getMain().getPressure())), " hPa"));
        humidity.setText(createString("Humidity: ", " ", String.valueOf(Math.round(resultFromCurrent.getMain().getHumidity())), "%"));

    }


}
