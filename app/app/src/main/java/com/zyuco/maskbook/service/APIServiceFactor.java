package com.zyuco.maskbook.service;

import com.zyuco.maskbook.tool.SimpleCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIServiceFactor {
    private static final int DEFAULT_TIMEOUT = 5;
    private static APIService instance = null;

    public static APIService getInstance() {
        if (instance == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            //TODO cookie
            Retrofit retrofit = new Retrofit.Builder()
                    .client(httpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(SimpleCallAdapterFactory.create())
                    .baseUrl(APIService.BASE_URL)
                    .build();
            instance = retrofit.create(APIService.class);
        }
        return instance;
    }
}
