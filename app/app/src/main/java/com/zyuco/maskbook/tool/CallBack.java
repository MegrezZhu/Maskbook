package com.zyuco.maskbook.tool;

import com.google.gson.Gson;
import com.zyuco.maskbook.model.ErrorResponse;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.http.HTTP;

public abstract class CallBack<T> implements Observer<T> {
    private static final Gson GSON = new Gson();
    @Override
    public final void onSubscribe(@NonNull Disposable d) {}
    @Override
    public final void onComplete() {}
    @Override
    public final void onNext(@NonNull T t) {
        onSuccess(t);
    }
    @Override
    public final void onError(@NonNull Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException)e;
            if (httpException.code() == 400) {
                try {
                    String json = httpException.response().errorBody().string();
                    ErrorResponse errorResponse = GSON.fromJson(json, ErrorResponse.class);
                    onFail(errorResponse);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else onException(e);
        } else onException(e);
    }
    public abstract void onSuccess(@NonNull T t);
    public abstract void onFail(@NonNull ErrorResponse e);
    public abstract void onException(@NonNull Throwable e);
}
