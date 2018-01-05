package com.zyuco.maskbook.tool;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyuco.maskbook.MainActivity;
import com.zyuco.maskbook.model.ErrorResponse;
import com.zyuco.maskbook.model.User;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

public class SimpleCallAdapter<R> implements CallAdapter<R, Object> {
    private CallAdapter<R, Object> adapter;

    SimpleCallAdapter(CallAdapter<R, Object> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Type responseType() {
        return this.adapter.responseType();
    }

    @Override
    public Object adapt(Call<R> call) {
        Object obj = this.adapter.adapt(call);
        if (obj instanceof Observable) {
            Observable<R> observable = (Observable<R>) obj;
            return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        } else {
            Completable completable = (Completable) obj;
            return completable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        }
    }
}
