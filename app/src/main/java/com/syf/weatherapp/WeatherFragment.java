package com.syf.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.syf.weatherapp.callbacks.ForecastCallback;
import com.syf.weatherapp.callbacks.WeatherCallBack;
import com.syf.weatherapp.forecast.ForecastData;
import com.syf.weatherapp.forecast.Results;
import com.syf.weatherapp.weather.WeatherApiService;

import java.util.ArrayList;
import java.util.List;


public class WeatherFragment extends Fragment implements WeatherCallBack, ForecastCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    Geocoder geocoder;
    FusedLocationProviderClient locationProviderClient;
    private EditText editText;
    private WeatherApiService weatherApi;
    Address address;
    private Results resultFromCurrent;
    ProgressDialog progressDialog;

    private LinearLayout resultsLayout;

    private RecyclerView recyclerView;


    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherApi = new WeatherApiService();
        geocoder = new Geocoder(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_weather, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViewsbyID(view);
        setOnClickListener(view);


    }

    private void findViewsbyID(View view) {
        editText = view.findViewById(R.id.et_location);
        resultsLayout = view.findViewById(R.id.results_layout);
        recyclerView = view.findViewById(R.id.recycler);

    }

    @SuppressLint("MissingPermission")
    private void setOnClickListener(View view) {
        view.findViewById(R.id.ic_location).setOnClickListener(v -> {
            if (checkPermissions()) {
                showProgressDialog();
                locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
                locationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                    try {
                        List<Address> locationfound = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (null != locationfound && !locationfound.isEmpty()) {
                            editText.setText(Utils.formatAddress(locationfound.get(0)));
                            dismissProgressDialog();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgressDialog();
                        Toast.makeText(getContext(), "Unable to Fetch Lcoation", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        view.findViewById(R.id.submit).setOnClickListener(v -> {
            showProgressDialog();
            address = Utils.geocodeAddress(geocoder, editText.getText().toString());
            if (address != null) {
                weatherApi.getCurrentWeather(this, address);
            } else {
                dismissProgressDialog();
                Toast.makeText(getContext(), "Process Failed", Toast.LENGTH_LONG).show();
            }

        });

    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait While we fetch your information...");
        progressDialog.show();

    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onForecastSuccess(ForecastData forecastData) {
        dismissProgressDialog();
        resultsLayout.setVisibility(View.VISIBLE);
        setForecastLayout(forecastData);

    }

    private void setForecastLayout(ForecastData forecastData) {
        List<Results> forcastResults = filterResults(forecastData.getList());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ForecastAdapter(forcastResults,resultFromCurrent));

    }

    private List<Results> filterResults(List<Results> forecastData) {
        ArrayList<Results> resultList = new ArrayList<>();
        String checkDate = forecastData.get(0).getDtTxt();
        if (!weatherApi.isSameDay(checkDate,WeatherApiService.formatUnixDate(resultFromCurrent.getDt()))) {
            resultList.add(forecastData.get(0));
        }
        for (Results results : forecastData) {
            if (!weatherApi.isSameDay(checkDate, results.getDtTxt())) {
                checkDate = results.getDtTxt();
                resultList.add(results);
            }
        }

        return resultList;
    }



    @Override
    public void onForecastFailure(int code, String message) {
        dismissProgressDialog();
        Toast.makeText(getContext(), "Process Failed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onWeatherSuccess(Results results) {
        resultFromCurrent = results;
        weatherApi.getFiveDayForecast(this, address);

    }

    @Override
    public void onWeatherFailure(int code, String message) {
        dismissProgressDialog();
        Toast.makeText(getContext(), "Process Failed", Toast.LENGTH_LONG).show();

    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }


}
