package com.zyuco.maskbook.service;

import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface APIService {
    // String BASE_URL = "http://zyuco.com:7001/api/";
//    String BASE_URL = "http://laptop.zyuco.com:7001/api/";
     String BASE_URL = "http://192.168.191.1:7001";

    @POST("join")
    @FormUrlEncoded
    Observable<User> regist(@NonNull @Field("username") String username,
                            @NonNull @Field("password") String password,
                            @Field("nickname") String nickname,
                            @Field("avatar") String avatar);

    @POST("session")
    @FormUrlEncoded
    Observable login(@NonNull @Field("username") String username,
                     @NonNull @Field("password") String password);

    @PUT("user")
    @FormUrlEncoded
    Observable<User> updateUser(@Field("nickname") String nickname,
                                @Field("avatar") String avatar);

    @GET("user")
    Observable<User> getUserInformation();

    // TODO: wrap
    @POST("posts")
    @Multipart
    Observable<Post> newPost(@NonNull @Part("parameter") RequestBody parameter,
                             @NonNull @Part("price") RequestBody price,
                             @NonNull @Part MultipartBody.Part file,
                             @Part("content") RequestBody content);
}
