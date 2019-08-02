package com.xcheng.retrofit;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Response;

/**
 * 创建时间：2018/4/8
 * 编写人： chengxin
 * 功能描述：支持生命周期绑定的Call{@link retrofit2.Call}
 */
public interface LifeCall<T> extends Observer<Lifecycle.Event> {

    Response<T> execute() throws IOException;

    void enqueue(LifeCallback<T> callback2);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    LifeCall<T> clone();

    Request request();

    /**
     * called before {@link #enqueue(LifeCallback)} and {@link #execute()}
     *
     * @param provider
     * @return
     */
    LifeCall<T> bindToLifecycle(@NonNull LifecycleProvider provider);
}
