package com.hitt.mvptoframenwork.http;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RequestRouter {
    @GET("{url}")
    Call<ResponseBody> getRequestBody(@Path("url") String url, @Body RequestBody route, @QueryMap Map<String, Object> map);

    @GET("{url}")
    Call<ResponseBody> getRequestRouter(@Path("url") String url, @QueryMap Map<String, Object> map);

    @POST("{url}")
    Call<ResponseBody> postRequestBody(@Path("url") String url, @Body RequestBody route, @QueryMap Map<String, Object> map);

    @POST("{url}")
    Call<ResponseBody> postRequestRouter(@Path("url") String url, @FieldMap Map<String, Object> map);

    @Multipart
    @POST("{url}")
    Call<ResponseBody> uploadFilesMultipartBodyParts(@Path("url") String url, @Part List<MultipartBody.Part> partList);
}
