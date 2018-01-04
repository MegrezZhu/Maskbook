package com.zyuco.maskbook.service;

import com.zyuco.maskbook.model.User;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface APIService {
    // String BASE_URL = "http://zyuco.com:7001";
    String BASE_URL = "http://192.168.191.1:7001";
    @GET("/")
    Observable<User> get();
}
