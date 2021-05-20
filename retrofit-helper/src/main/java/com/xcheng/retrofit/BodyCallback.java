package com.xcheng.retrofit;

import androidx.annotation.NonNull;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Callback methods are executed using the {@link retrofit2.Retrofit} callback executor. When none is
 * specified, the following defaults are used:
 * <ul>
 * <li>Android: Callbacks are executed on the application's main (UI) thread.</li>
 * <li>JVM: Callbacks are executed on the background thread which performed the request.</li>
 * </ul>
 * <p>
 *
 * @param <T> Successful response body type.
 */
public abstract class BodyCallback<T> implements Callback<T> {
    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        T body = response.body();
        if (body != null) {
            onSuccess(call, body);
        } else {
            //HttpError error = parseThrowable(call, new HttpException(response));
            //Objects.requireNonNull(error);
            //onError(call, error);
            onFailure(call, new HttpException(response));
        }
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        HttpError error = parseThrowable(call, t);
        Objects.requireNonNull(error);
        onError(call, error);
    }

    protected abstract void onError(Call<T> call, HttpError error);

    protected abstract void onSuccess(Call<T> call, T t);

    /**
     * @param t 统一解析throwable对象转换为HttpError对象，
     *          <ul>
     *          <li>如果throwable为{@link HttpError}则为{@link retrofit2.Converter#convert(Object)}内抛出的异常</li>
     *          <li>如果为{@link retrofit2.HttpException}则为{@link Response#body()}为null的时候抛出的</li>
     *          <ui/>
     * @see #onFailure(Call, Throwable)
     */
    @NonNull
    protected HttpError parseThrowable(Call<T> call, Throwable t) {
        if (t instanceof HttpError) {
            return (HttpError) t;
        } else if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            final String msg;
            switch (httpException.code()) {
                case 400:
                    msg = "参数错误";
                    break;
                case 401:
                    msg = "身份未授权";
                    break;
                case 403:
                    msg = "禁止访问";
                    break;
                case 404:
                    msg = "地址未找到";
                    break;
                default:
                    msg = "服务异常";
            }
            return new HttpError(msg, httpException);
        } else if (t instanceof UnknownHostException) {
            return new HttpError("网络异常", t);
        } else if (t instanceof ConnectException) {
            return new HttpError("网络异常", t);
        } else if (t instanceof SocketException) {
            return new HttpError("服务异常", t);
        } else if (t instanceof SocketTimeoutException) {
            return new HttpError("响应超时", t);
        } else {
            return new HttpError("请求失败", t);
        }
    }
}