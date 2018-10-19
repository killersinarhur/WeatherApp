package com.syf.weatherapp.configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;

public class OkHttpManagerConfig {

    private UnsafeTrustManagerConfig unsafeTrustManager;
    private UnsafeHostnameVerifierConfig unsafeHostnameVerifier;

    private InterceptorConfig interceptor;


    public OkHttpManagerConfig(UnsafeTrustManagerConfig unsafeTrustManager, InterceptorConfig interceptor, UnsafeHostnameVerifierConfig unsafeHostnameVerifier) {
        this.unsafeTrustManager = unsafeTrustManager;
        this.unsafeHostnameVerifier = unsafeHostnameVerifier;
        this.interceptor = interceptor;
    }

    public OkHttpClient getOkHttpClient() {
        try {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder();

            // Sets the user agent through Interceptor only if the string is not null and not empty
            final SSLContext sslContext = SSLContext.getInstance("TLS");


            // Allows all certificate chains
            sslContext.init(null, new TrustManager[]{unsafeTrustManager}, new java.security.SecureRandom());
            okHttpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), unsafeTrustManager);

            // Allows mismatched hostnames on the certificate
            okHttpClientBuilder.hostnameVerifier(unsafeHostnameVerifier);

            return okHttpClientBuilder
                    // sets interceptor for response code handling
                    .addInterceptor(interceptor)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            return new OkHttpClient();
        }
    }


}