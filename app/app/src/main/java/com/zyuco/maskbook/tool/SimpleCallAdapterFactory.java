package com.zyuco.maskbook.tool;

import android.widget.SimpleAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class SimpleCallAdapterFactory extends CallAdapter.Factory {
    private RxJava2CallAdapterFactory factory = RxJava2CallAdapterFactory.create();
    public static SimpleCallAdapterFactory create() {
        return new SimpleCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter<?, ?> adapter = factory.get(returnType, annotations, retrofit);
        return new SimpleCallAdapter(adapter);
    }
}
