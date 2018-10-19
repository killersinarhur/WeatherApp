package com.syf.weatherapp;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String formatAddress(Address address) {
        String strAddress;
        StringBuilder sb = new StringBuilder();

        // add city
        if (address.getLocality() != null && !address.getLocality().equals("")) {
            sb.append(address.getLocality());
        }

        // add state
        if (address.getAdminArea() != null && !address.getAdminArea().equals("")) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(address.getAdminArea());
        }

        // add zip code
        if (address.getPostalCode() != null && !address.getPostalCode().equals("")) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(address.getPostalCode());
        }

        // couldn't read the components of address
        // so try to append all of the address lines together and use that
        if (sb.length() == 0) {
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(address.getAddressLine(i));
            }
        }
        strAddress = sb.toString();
        if (strAddress.equals("null")) {
            strAddress = "";
        }
        return strAddress;
    }

    //Method that looks for Zip Code Inputs
    public static boolean isZipCode(String location) {
        String regex = "^[0-9]{5}(?:-[0-9]{4})?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(location);
        return matcher.matches();
    }


    @Nullable
    public static Address geocodeAddress(Geocoder geocoder, String s) {
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(s, 1);
        } catch (IOException e) {
            e.printStackTrace();

        }
        if (null != list && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public static String createString(String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

}
