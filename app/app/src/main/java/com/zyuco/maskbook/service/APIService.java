package com.zyuco.maskbook.service;

import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
//    String BASE_URL = "http://www.zyuco.com:7001/api/";
    String BASE_URL = "http://laptop.zyuco.com:7001/api/";
//    String BASE_URL = "http://192.168.191.1:7001";

    @POST("join")
    @Multipart
    Observable<User> regist(@Part("username") RequestBody username,
                            @Part("password") RequestBody password,
                            @Part("nickname") RequestBody nickname,
                            @Part MultipartBody.Part avatar);

    @POST("session")
    Observable<User> login(@Body User user);

    @GET("user")
    Observable<User> getUserInformation();

    @POST("posts")
    @Multipart
    Observable<Post> newPost(@Part MultipartBody.Part image,
                             @Part("parameter") RequestBody parameter,
                             @Part("content") RequestBody content,
                             @Part("price") RequestBody price);

    @DELETE("posts/:id")
    Observable deletePost(@Path("id") int id);



    @GET("posts")
    Observable<List<Post>> getPosts(@Query("before")Date before,
                                    @Query("limit") int limit,
                                    @Query("filter") String filter);

    @GET("users/:id/posts")
    Observable<List<Post>> getPostsFromUser(@Path("id") int id,
                                            @Query("before")Date before,
                                            @Query("limit") int limit);

    @GET("posts/:id")
    Observable<Post> getPostDetail(@Path("id") int id);

    @POST("posts/id/like")
    Observable likePost(@Path("id") int id);

    @DELETE("posts/id/like")
    Observable unlikePost(@Path("id") int id);

    @POST("posts/id/unlock")
    Observable unlockPost(@Path("id") int id);
}
