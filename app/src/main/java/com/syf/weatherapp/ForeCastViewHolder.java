package com.syf.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.syf.weatherapp.forecast.Results;
import com.syf.weatherapp.weather.WeatherApiService;

public class ForeCastViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView dayOfWeek;
    TextView highOf;
    TextView lowof;
    TextView forecastCondition;


    public static ForeCastViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_card, parent, false);
        return new ForeCastViewHolder(view);
    }


    public ForeCastViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Results results) {
        findViewsById();
        Picasso.get()
                .load(WeatherApiService.getImageURL(results.getWeather().get(0).getId()))
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_error_black_24dp)
                .fit()
                .into(icon);
        dayOfWeek.setText(WeatherApiService.formatDate(results.getDtTxt()));
        highOf.setText(Utils.createString("High: ",String.valueOf(Math.round(results.getMain().getTempMax())),"°F"));
        lowof.setText(Utils.createString("Low: ",String.valueOf(Math.round(results.getMain().getTempMin())),"°F"));
        forecastCondition.setText(WeatherApiService.formatDescString(results.getWeather().get(0).getId()));

    }


    private void findViewsById() {
        icon = itemView.findViewById(R.id.icon_weather);
        dayOfWeek = itemView.findViewById(R.id.day_of_week);
        highOf = itemView.findViewById(R.id.high_of_forecast);
        lowof = itemView.findViewById(R.id.low_of_forecast);
        forecastCondition=itemView.findViewById(R.id.forecast_condition);

    }
}
