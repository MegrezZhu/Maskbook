package com.zyuco.maskbook.service;

import android.content.Context;
import android.support.annotation.Nullable;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.zyuco.maskbook.model.Post;
import com.zyuco.maskbook.model.User;
import com.zyuco.maskbook.tool.SimpleCallAdapterFactory;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private static final int DEFAULT_TIMEOUT = 5;
    private static APIService service = null;

    public static void init(Context context) {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .cookieJar(cookieJar);
        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(SimpleCallAdapterFactory.create())
            .baseUrl(APIService.BASE_URL)
            .build();
        service = retrofit.create(APIService.class);
    }

    public static Observable<User> regist(String username, String password, String nickname, @Nullable File avatar) {
        MultipartBody.Part filePart = null;
        if (avatar != null) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), avatar);
            filePart = MultipartBody.Part.createFormData("avatar", avatar.getName(), body);
        }

        return service.regist(RequestBody.create(MultipartBody.FORM, username),
            RequestBody.create(MultipartBody.FORM, password),
            RequestBody.create(MultipartBody.FORM, nickname),
            filePart);
    }

    public static Observable<User> login(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return service.login(user);
    }

    public static Completable logout() {
        return service.logout();
    }

    public static Observable<User> getUserInformation() {
        return service.getUserInformation();
    }

    public static Observable<Post> newPost(File image, double parameter, String content, int price) {
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("avatar", image.getName(), body);
        return service.newPost(filePart,
            RequestBody.create(MultipartBody.FORM, String.valueOf(parameter)),
            RequestBody.create(MultipartBody.FORM, content),
            RequestBody.create(MultipartBody.FORM, String.valueOf(price)));
    }

    public static Completable deletePost(int id) {
        return service.deletePost(id);
    }

    public enum PostFilter {
        all, unlocked, mine;
    }

    public static Observable<List<Post>> getPosts(Date before, int limit, PostFilter filter) {
        return service.getPosts(before, limit, filter.name());
    }

    public static Observable<List<Post>> getPosts() {
//        return getPosts(new Date(), 30, PostFilter.all);
        return getPosts(new Date(), 3, PostFilter.all);
    }

    public static Observable<List<Post>> getPostsFromUser(int userId, Date before, int limit) {
        return service.getPostsFromUser(userId, before, limit);
    }

    public static Observable<List<Post>> getPostsFromUser(int userId) {
        return getPostsFromUser(userId, new Date(), 30);
    }

    public static Observable<Post> getPostDetail(int postId) {
        return service.getPostDetail(postId);
    }

    public static Completable likePost(int postId) {
        return service.likePost(postId);
    }

    public static Completable unlikePost(int postId) {
        return service.unlikePost(postId);
    }

    public static Completable unlockPost(int postId) {
        return service.unlockPost(postId);
    }
}
