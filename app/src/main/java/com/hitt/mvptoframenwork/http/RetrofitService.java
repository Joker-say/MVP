package com.hitt.mvptoframenwork.http;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hitt.mvptoframenwork.MVPApplication;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit;
    private static Gson gsonConverterFactory = new GsonBuilder().create();
    private static String mToken = "";

    public static RequestRouter getAPIService() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://v.juhe.cn/") //设置Base的访问路径
                .addConverterFactory(GsonConverterFactory.create(gsonConverterFactory)) //设置默认的解析库：Gson
                .client(defaultOkHttpClient())
                .build();
        return retrofit.create(RequestRouter.class);
    }

    public static RequestRouter getAPIService(String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(url) //设置Base的访问路径
                .addConverterFactory(GsonConverterFactory.create(gsonConverterFactory)) //设置默认的解析库：Gson
                .client(defaultOkHttpClient())
                .build();
        return retrofit.create(RequestRouter.class);
    }

    /**
     * 拦截器
     */
    public static OkHttpClient defaultOkHttpClient() {
        OkHttpClient.Builder mOkHttpClient = new OkHttpClient.Builder();
        mOkHttpClient.sslSocketFactory(ClientManager.getSSLSocketFactory());
        mOkHttpClient.writeTimeout(30 * 1000, TimeUnit.MILLISECONDS);
        mOkHttpClient.readTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        mOkHttpClient.connectTimeout(10 * 1000, TimeUnit.MILLISECONDS);

        //设置缓存路径
        File httpCacheDirectory = new File(MVPApplication.getContext().getCacheDir(), "okhttpCache");
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        mOkHttpClient.cache(cache);
        //设置拦截器
        mOkHttpClient.addInterceptor(LoggingInterceptor);
        mOkHttpClient.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        mOkHttpClient.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        return mOkHttpClient.build();
    }

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            boolean netWorkConection = NetUtils.hasNetWorkConection(MVPApplication.getContext());
            Request request = chain.request();
            if (!netWorkConection) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response response = chain.proceed(request);
            if (netWorkConection) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                response.newBuilder()
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .header("Cache-Control", cacheControl)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 7;
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    };

    private static final Interceptor LoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            okhttp3.Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.i("TAG", "-----LoggingInterceptor----- :\nrequest url:" +
                    request.url() + "\ntime:" + (t2 - t1) / 1e6d + "\nbody:" + content + "\n");
            return response.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("token", mToken)
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    };
}
