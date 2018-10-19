package com.syf.weatherapp.configuration;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class UnsafeHostnameVerifierConfig implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}