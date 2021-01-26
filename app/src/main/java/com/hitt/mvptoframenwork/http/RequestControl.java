package com.hitt.mvptoframenwork.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class RequestControl {
    private static final String SOCKET_TIME_OUT = "网络连接超时";
    private static final String CONNECT = "请求服务器异常";
    private static final String JSON_EXCEPTION = "数据解析错误";
    private static final String UNKNOWN = "未知错误";
    private static final String NETWORK_EXCEPTION = "请检查网络连接是否正常";

    public static void request(final int what, final Call<ResponseBody> call, final Class<?> tClass, final NetworkCallBack callBack) {
        call.enqueue(new Callback<ResponseBody>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ResponseBody body = response.body();
                        Gson gson = new Gson();
                        try {
                            String json = body.string();
                            if (!TextUtils.isEmpty(json)) {
                                Object data = gson.fromJson(json, tClass);
                                callBack.onSuccess(what,  data);
                            } else {
                                callBack.onFail(what, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        callBack.onFail(what, NETWORK_EXCEPTION);
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    callBack.onFail(what, SOCKET_TIME_OUT);
                } else if (t instanceof ConnectException) {
                    callBack.onFail(what, CONNECT);
                } else if (t instanceof JsonSyntaxException) {
                    callBack.onFail(what, JSON_EXCEPTION + "\n" + t.toString());
                } else {
                    callBack.onFail(what, "[ " + what + " ]" + UNKNOWN + "\n" + t.toString());
                }
            }
        });
    }
}
