package com.syf.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.syf.weatherapp.forecast.Results;

import java.util.List;

class ForecastAdapter extends RecyclerView.Adapter {

    private static final int CURRENT_VHOLDER = 0;
    private static final int FORCAST_VH = 1;
    private final List<Results> forecastResults;
    private final Results resultsFromCurrent;

    public ForecastAdapter(List<Results> forcastResults, Results resultFromCurrent) {
        this.forecastResults = forcastResults;
        this.resultsFromCurrent =resultFromCurrent;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof ForeCastViewHolder) {
            ((ForeCastViewHolder) viewHolder).bind(forecastResults.get(i-1));
        }else {
            ((CurrentWeatherViewHolder) viewHolder).bind(resultsFromCurrent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType==FORCAST_VH) {
            return ForeCastViewHolder.inflate(viewGroup);
        }else {
            return CurrentWeatherViewHolder.inflate(viewGroup);
        }
    }

    @Override
    public int getItemViewType(int position) {
       if (position==0){
           return CURRENT_VHOLDER;
       } else {
           return FORCAST_VH;
       }

    }

    @Override
    public int getItemCount() {
        return forecastResults.size();
    }
}

